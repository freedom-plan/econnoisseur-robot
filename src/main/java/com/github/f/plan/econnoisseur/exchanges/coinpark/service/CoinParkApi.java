package com.github.f.plan.econnoisseur.exchanges.coinpark.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.f.plan.econnoisseur.dto.HttpConsumer;
import com.github.f.plan.econnoisseur.exchanges.coinpark.model.CoinParkApiPath;
import com.github.f.plan.econnoisseur.exchanges.coinpark.model.Market;
import com.github.f.plan.econnoisseur.exchanges.coinpark.model.StatusMapping;
import com.github.f.plan.econnoisseur.exchanges.coinpark.util.SignUtil;
import com.github.f.plan.econnoisseur.exchanges.common.dto.*;
import com.github.f.plan.econnoisseur.exchanges.common.iface.IApi;
import com.github.f.plan.econnoisseur.exchanges.common.model.*;
import com.github.f.plan.econnoisseur.util.HttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Optional;

import static com.github.f.plan.econnoisseur.exchanges.common.model.Code.OK;
import static com.github.f.plan.econnoisseur.exchanges.common.model.Code.SERVER;
import static com.github.f.plan.econnoisseur.exchanges.coinpark.model.CoinParkApiPath.*;

/**
 * coinpark
 *
 * @author jimmy
 * @since version
 * 2018年07月11日 10:08:00
 */
public class CoinParkApi implements IApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(CoinParkApi.class);
    private String urlPrefix;

    private String apiKey;
    private SignUtil signUtil;

    public CoinParkApi(String urlPrefix, String apiKey, String secretKey) throws Exception {
        this.urlPrefix = urlPrefix;
        this.apiKey = apiKey;
        this.signUtil = new SignUtil(secretKey);
    }

    @Override
    public Ticker ticker(CurrencyPair pair) {
        Ticker ticker = new Ticker(SERVER);
        JSONObject param = new JSONObject()
                .fluentPut("pair", Market.valueOf(pair));
        String response = doRequest(TICKER_PATH, param);

        handler(response, ticker, (jsonObject, dto) ->
            dto.setBid(jsonObject.getBigDecimal("buy"))
                .setAsk(jsonObject.getBigDecimal("sell"))
                .setHigh(jsonObject.getBigDecimal("high"))
                .setLow(jsonObject.getBigDecimal("low"))
                .setLast(jsonObject.getBigDecimal("last"))
                .setVol(jsonObject.getBigDecimal("vol"))
        );
        return ticker;
    }

    @Override
    public Balances balances() {
        Balances balance = new Balances(SERVER);
        JSONObject param = new JSONObject()
            .fluentPut("select", 1);
        String response = doRequest(BALANCE_PATH, param);

        handlerArray(response, balance, (jsonArray, dto) ->
                Optional.ofNullable(jsonArray.getJSONObject(0).getJSONObject("result").getJSONArray("assets_list"))
                    .ifPresent(p->p.toJavaList(JSONObject.class)
                    .forEach(q -> {
                        dto.setBalance(Currency.get(q.getString("coin_symbol")), new Balance().setAvailable(q.getBigDecimal("balance")).setFrozen(q.getBigDecimal("freeze")));
                    }))
        );
        return balance;
    }

    @Override
    public Order limit(CurrencyPair pair, OrderOperation operation, BigDecimal price, BigDecimal amount) {
        Order order = new Order(SERVER);
        JSONObject jsonObject = new JSONObject(true)
            .fluentPut("pair", Market.valueOf(pair))
            .fluentPut("account_type", 0)
            .fluentPut("order_type", 2)
            .fluentPut("order_side", "SELL".equals(operation.name()) ? 2 : 1)
            .fluentPut("price", price)
            .fluentPut("amount", amount);
        String response = doRequest(TRADE_PATH, jsonObject);
        handlerArray(response, order, (jsonArray, dto) ->
            dto.setTotalAmount(amount)
                .setOperation(operation)
                .setTotalPrice(price)
                .setId(jsonArray.getJSONObject(0).getString("result"))
                .setType(OrderType.LIMIT)
                .setStatus(OrderStatus.NEW)
        );

        return order;
    }

    @Override
    public Order market(CurrencyPair pair, OrderOperation operation, BigDecimal amount) {
        throw new RuntimeException("Not supported");
    }

    @Override
    public Orders pandingOrders(CurrencyPair pair) {
        Orders orders = new Orders(SERVER);
        Order order = new Order();
        JSONObject jsonObject = new JSONObject(true)
            .fluentPut("pair", Market.valueOf(pair))
            .fluentPut("page", 1)
            .fluentPut("size", 20)
            ;
        String response = doRequest(PENDING_LIST_TRADE, jsonObject);
        System.out.println(response);
        handlerArray(response, order, (jsonArray, dto) -> {
            Optional.ofNullable(jsonArray.getJSONObject(0).getJSONObject("result").getJSONArray("items"))
                .ifPresent(p->p.toJavaList(JSONObject.class)
                    .forEach(q -> {
                        dto.setId(q.getString("id"))
                            .setStatus(StatusMapping.statusMap.get(q.getString("status")))
                            .setTotalPrice(q.getBigDecimal("price"))
                            .setTotalAmount(q.getBigDecimal("amount"))
                            .setAmount(q.getBigDecimal("deal_amount"))
                            .setType("2".equals(q.getString("order_type")) ? OrderType.MARKET : OrderType.LIMIT)
                            .setOperation("2".equals(q.getString("order_side")) ? OrderOperation.SELL : OrderOperation.BUY);
                        orders.addOrder(dto).setCode(OK);
                    }));
        });
        return orders;
    }

    @Override
    public Orders finishedOrders(CurrencyPair pair) {
        return null;
    }

    @Override
    public Order getOrder(CurrencyPair pair, String orderId) {
        Order order = new Order();
        JSONObject jsonObject = new JSONObject()
            .fluentPut("id", orderId);
        String response = doRequest(ORDERS_DETAIL_PATH, jsonObject);

        handlerArray(response, order, (jsonArray, dto) -> {
            JSONObject result = jsonArray.getJSONObject(0);
            dto.setId(orderId)
                .setType("2".equals(result.getString("order_type")) ? OrderType.MARKET : OrderType.LIMIT)
                .setOperation("2".equals(result.getString("order_side")) ? OrderOperation.SELL : OrderOperation.BUY)
                .setAmount(result.getBigDecimal("deal_amount"))
                .setTotalAmount(result.getBigDecimal("amount"))
                .setTotalPrice(result.getBigDecimal("price"))
                .setStatus(StatusMapping.statusMap.get(result.getString("status")))
            ;
        });
        return order;
    }

    @Override
    public Order cancelOrder(CurrencyPair pair, String orderId) {
        Order order = new Order(SERVER);
        JSONObject jsonObject = new JSONObject()
            .fluentPut("orders_id", orderId);
        String response = doRequest(CANCEL_TRADE, jsonObject);

        handlerArray(response, order, (jsonArray, dto) -> {
            dto.setType(OrderType.LIMIT).setId(orderId)
                .setStatus(null != jsonArray.getJSONObject(0).getString("result")? OrderStatus.CANCELED : OrderStatus.PARTIALLY_FILLED);
            }
        );

        return order;
    }

    private static <T extends BaseResp> void handler(String response, T model, HttpConsumer<JSONObject, T> action) {
        boolean bool = false;
        if (StringUtils.isNotBlank(response)) {
            try {
                JSONObject jsonObject = JSON.parseObject(response);
                if (!jsonObject.isEmpty()) {
                    JSONObject result = jsonObject.getJSONObject("result");
                    if (!result.isEmpty()) {
                        if (null != action) {
                            action.accept(result, model);
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

    private static <T extends BaseResp> void handlerArray(String response, T model, HttpConsumer<JSONArray, T> action) {
        boolean bool = false;
        if (StringUtils.isNotBlank(response)) {
            try {
                JSONObject jsonObject = JSON.parseObject(response);
                if (!jsonObject.isEmpty()) {
                    JSONArray result = jsonObject.getJSONArray("result");
                    if (!result.isEmpty()) {
                        if (null != action) {
                            action.accept(result, model);
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
    private String doRequest(CoinParkApiPath apiPath, JSONObject params) {
        if (null == params) {
            params = new JSONObject();
        }
        if (StringUtils.isNotBlank(apiPath.getCmd()) && "GET".equals(apiPath.getMethod())) {
            params.put("cmd", apiPath.getCmd());
        }
        if (apiPath.isAuth()) {
            JSONArray jsonArray = new JSONArray().fluentAdd(new JSONObject().fluentPut("cmd", apiPath.getCmd()).fluentPut("body", params));
            String sign = this.signUtil.getSign(jsonArray.toJSONString());
            JSONObject data = new JSONObject().fluentPut("cmds", jsonArray).fluentPut("apikey", this.apiKey).fluentPut("sign", sign);
            System.out.println(data.toJSONString());
            return HttpRequest.request(this.urlPrefix + apiPath.getPath(), apiPath.getMethod(), data);
        } else {
            return HttpRequest.request(this.urlPrefix + apiPath.getPath(), apiPath.getMethod(), params);
        }
    }
}
