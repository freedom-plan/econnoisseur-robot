package com.github.f.plan.econnoisseur.exchanges.gate.io.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.f.plan.econnoisseur.dto.HttpConsumer;
import com.github.f.plan.econnoisseur.exchanges.common.dto.*;
import com.github.f.plan.econnoisseur.exchanges.common.iface.IApi;
import com.github.f.plan.econnoisseur.exchanges.common.model.Currency;
import com.github.f.plan.econnoisseur.exchanges.common.model.CurrencyPair;
import com.github.f.plan.econnoisseur.exchanges.common.model.OrderOperation;
import com.github.f.plan.econnoisseur.exchanges.common.model.OrderStatus;
import com.github.f.plan.econnoisseur.exchanges.gate.io.model.GateIoApiPath;
import com.github.f.plan.econnoisseur.exchanges.gate.io.model.Market;
import com.github.f.plan.econnoisseur.exchanges.gate.io.model.OrderStatusMapping;
import com.github.f.plan.econnoisseur.exchanges.gate.io.util.SignUtil;
import com.github.f.plan.econnoisseur.util.HttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import java.math.BigDecimal;
import java.util.Map;

import static com.github.f.plan.econnoisseur.exchanges.common.model.Code.OK;
import static com.github.f.plan.econnoisseur.exchanges.common.model.Code.SERVER;
import static com.github.f.plan.econnoisseur.exchanges.gate.io.model.GateIoApiPath.*;

/**
 * GateioApi
 *
 * @author Kevin Huang
 * @since version
 * 2018年07月05日 10:08:00
 */
public class GateIoApi implements IApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(GateIoApi.class);
    private String urlPrefix;
    private String dataPrefix;

    private String apiKey;
    private Mac mac;

    public GateIoApi(String urlPrefix, String dataPrefix, String apiKey, String secretKey) throws Exception {
        this.urlPrefix = urlPrefix;
        this.dataPrefix = dataPrefix;
        this.apiKey = apiKey;
        this.mac = SignUtil.mac(secretKey);
    }

    @Override
    public Ticker ticker(CurrencyPair pair) {
        Ticker ticker = new Ticker(SERVER);
        JSONObject param = new JSONObject()
                .fluentPut("pair", Market.valueOf(pair));
        String response = doRequest(TICKER_PATH, param);

        handler(response, ticker, (jsonObject, dto) -> dto.setBid(jsonObject.getBigDecimal("highestBid"))
                .setAsk(jsonObject.getBigDecimal("lowestAsk"))
                .setHigh(jsonObject.getBigDecimal("high24hr"))
                .setLow(jsonObject.getBigDecimal("low24hr"))
                .setLast(jsonObject.getBigDecimal("last"))
                .setVol(jsonObject.getBigDecimal("baseVolume"))
        );
        return ticker;
    }

    @Override
    public Balances balances() {
        Balances balances = new Balances(SERVER);
        String response = doRequest(BALANCE_PATH, null);
        handler(response, balances, (jsonObject, dto) -> {
            if (jsonObject.containsKey("available")) {
                JSONObject available = jsonObject.getJSONObject("available");
                if (!available.isEmpty()) {
                    available.keySet().forEach(key -> {
                        Currency currency = Currency.get(key);
                        if (null != currency) {
                            dto.setBalance(currency, new Balance().setAvailable(available.getBigDecimal(key)));
                        }
                    });
                }
            }

            if (jsonObject.containsKey("locked")) {
                JSONObject locked = jsonObject.getJSONObject("locked");
                if (!locked.isEmpty()) {
                    locked.keySet().forEach(key -> {
                        Currency currency = Currency.get(key);
                        if (null != currency) {
                            Balance balance = dto.getBalance(currency);
                            if (null != balance) {
                                balance.setFrozen(locked.getBigDecimal(key));
                            } else {
                                dto.setBalance(currency, new Balance().setFrozen(locked.getBigDecimal(key)));
                            }
                        }
                    });
                }
            }
        });

        return balances;
    }

    @Override
    public Order limit(CurrencyPair pair, OrderOperation operation, BigDecimal price, BigDecimal amount) {
        Order order = new Order(SERVER);
        JSONObject params = new JSONObject(true)
                .fluentPut("amount", amount.toString())
                .fluentPut("rate", price.toString())
                .fluentPut("currencyPair", Market.valueOf(pair));
        GateIoApiPath apiPath = OrderOperation.BUY == operation ? BUY_PATH : SELL_PATH;
        String response = doRequest(apiPath, params);

        handler(response, order, (jsonObject, dto) -> {
            OrderStatus status = OrderStatus.NEW;
            if (BigDecimal.ZERO.equals(jsonObject.getBigDecimal("leftAmount"))) {
                status = OrderStatus.FILLED;
            } else if (!BigDecimal.ZERO.equals(jsonObject.getBigDecimal("filledAmount"))) {
                status = OrderStatus.PARTIALLY_FILLED;
            }

            dto.setId(jsonObject.getString("orderNumber"))
                    .setPrice(jsonObject.getBigDecimal("filledRate"))
                    .setStatus(status);
        });
        return order;
    }

    @Override
    public Order market(CurrencyPair pair, OrderOperation operation, BigDecimal amount) {
        throw new RuntimeException("Not supported");
    }

    @Override
    public Orders pandingOrders(CurrencyPair pair) {
        Orders orders = new Orders(SERVER);
        String response = doRequest(OPEN_ORDERS_PATH, null);
        handler(response, orders, (jsonObject, dto) -> {
            JSONArray array = jsonObject.getJSONArray("orders");

            if (!array.isEmpty()) {
                for (Object item : array) {
                    JSONObject order = (JSONObject) item;

                    if (!Market.valueOf(pair).equalsIgnoreCase(order.getString("currencyPair"))) {
                        continue;
                    }
                    String s = order.getString("status");
                    OrderStatus status = OrderStatusMapping.get(s);

                    BigDecimal amount = order.getBigDecimal("filledAmount");
                    if (OrderStatus.NEW == status) {
                        if (null != amount && !BigDecimal.ZERO.equals(amount)) {
                            status = OrderStatus.PARTIALLY_FILLED;
                        }
                    }

                    dto.addOrder(new Order()
                            .setId(order.getString("orderNumber"))
                            .setPrice(order.getBigDecimal("filledRate"))
                            .setAmount(amount)
                            .setOperation(OrderOperation.get(order.getString("type")))
                            .setTotalAmount(order.getBigDecimal("initialAmount"))
                            .setTotalPrice(order.getBigDecimal("initialRate"))
                            .setStatus(status));
                }
            }
        });
        return orders;
    }

    @Override
    public Orders finishedOrders(CurrencyPair pair) {
        Orders orders = new Orders(SERVER);
        JSONObject params = new JSONObject(true)
                .fluentPut("currencyPair", Market.valueOf(pair));

        String response = doRequest(MYTRADE_HISTORY_PATH, params);
        handler(response, orders, (jsonObject, dto) -> {
            JSONArray array = jsonObject.getJSONArray("trades");

            if (!array.isEmpty()) {
                for (Object item : array) {
                    JSONObject order = (JSONObject) item;

                    String s = order.getString("status");
                    OrderStatus status = OrderStatusMapping.get(s);

                    dto.addOrder(new Order()
                            .setId(order.getString("orderid"))
                            .setPrice(order.getBigDecimal("rate"))
                            .setAmount(order.getBigDecimal("amount"))
                            .setOperation(OrderOperation.get(order.getString("type")))
                            .setStatus(status));
                }
            }
        });
        return orders;
    }

    @Override
    public Order getOrder(CurrencyPair pair, String orderId) {
        Order order = new Order(SERVER);
        JSONObject params = new JSONObject()
                .fluentPut("orderNumber", orderId)
                .fluentPut("currencyPair", Market.valueOf(pair));
        String response = doRequest(GET_ORDER_PATH, params);

        handler(response, order, (jsonObject, dto) -> {
            JSONObject object = jsonObject.getJSONObject("order");

            String s = object.getString("status");
            OrderStatus status = OrderStatusMapping.get(s);

            BigDecimal amount = object.getBigDecimal("amount");
            if (OrderStatus.NEW == status) {
                if (null != amount && !BigDecimal.ZERO.equals(amount)) {
                    status = OrderStatus.PARTIALLY_FILLED;
                }
            }

            dto.setId(object.getString("id"))
                    .setPrice(object.getBigDecimal("initialRate"))
                    .setAmount(amount)
                    .setOperation(OrderOperation.get(object.getString("type")))
                    .setPrice(object.getBigDecimal("rate"))
                    .setTotalAmount(object.getBigDecimal("initialAmount"))
                    .setTotalPrice(object.getBigDecimal("initialRate"))
                    .setStatus(status);
        });
        return order;
    }

    @Override
    public Order cancelOrder(CurrencyPair pair, String orderId) {
        Order order = new Order(SERVER);
        JSONObject params = new JSONObject()
                .fluentPut("orderNumber", orderId)
                .fluentPut("currencyPair", Market.valueOf(pair));
        String response = doRequest(CANCEL_ORDER_PATH, params);
        handler(response, order, null);
        return order;
    }

    private static <T extends BaseResp> void handler(String response, T model, HttpConsumer<JSONObject, T> action) {
        boolean bool = false;
        if (StringUtils.isNotBlank(response)) {
            try {
                JSONObject jsonObject = JSON.parseObject(response);
                if (!jsonObject.isEmpty()) {
                    boolean result = jsonObject.getBoolean("result");
                    if (result) {
                        if (null != action) {
                            action.accept(jsonObject, model);
                        }
                        bool = true;
                    } else {
                        Thread.sleep(60000);
                        LOGGER.error("请求报文失败：{}", response);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("返回报文：{}", response);
                LOGGER.error("获取请求失败", e);
            }
        }

        if (bool) {
            model.setCode(OK);
        }
    }

    /**
     * 发送请求
     *
     * @param apiPath 请求地址
     * @param params  请求参数
     * @return
     */
    private String doRequest(GateIoApiPath apiPath, JSONObject params) {
        if (apiPath.isAuth()) {
            if (null == params) {
                params = new JSONObject();
            }
            String data = params.toJSONString();
            Header key = new BasicHeader("Key", this.apiKey);
            Header sign = new BasicHeader( "Sign", SignUtil.getSign(mac, data));
            return request(this.urlPrefix, apiPath, params, key, sign);
        } else {
            return request(this.dataPrefix, apiPath, params);
        }
    }

    private String request(String urlPrefix, GateIoApiPath apiPath, Map<String, Object> map, Header... headers) {
        StringBuilder url = new StringBuilder(urlPrefix)
                .append(apiPath.getPath());

        if (StringUtils.isNotBlank(apiPath.getPathVar()) && map.containsKey(apiPath.getPathVar())) {
            Object pathParam = map.remove(apiPath.getPathVar());
            url.append("/").append(pathParam.toString());
        }
        return HttpRequest.request(url.toString(), apiPath.getMethod(), map, headers);
    }
}
