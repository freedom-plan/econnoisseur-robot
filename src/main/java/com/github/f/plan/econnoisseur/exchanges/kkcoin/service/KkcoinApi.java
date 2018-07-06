package com.github.f.plan.econnoisseur.exchanges.kkcoin.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.f.plan.econnoisseur.exchanges.common.dto.*;
import com.github.f.plan.econnoisseur.exchanges.common.iface.IApi;
import com.github.f.plan.econnoisseur.exchanges.common.model.*;
import com.github.f.plan.econnoisseur.exchanges.kkcoin.dto.BalanceDto;
import com.github.f.plan.econnoisseur.exchanges.kkcoin.model.KkcoinApiPath;
import com.github.f.plan.econnoisseur.exchanges.kkcoin.model.Market;
import com.github.f.plan.econnoisseur.exchanges.kkcoin.util.RSAUtil;
import com.github.f.plan.econnoisseur.util.HttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Map;

import static com.github.f.plan.econnoisseur.exchanges.common.model.Code.OK;
import static com.github.f.plan.econnoisseur.exchanges.common.model.Code.SERVER;
import static com.github.f.plan.econnoisseur.exchanges.kkcoin.model.KkcoinApiPath.*;

/**
 * KkcoinApi
 *
 * @author jimmy
 * @version 0.1.0
 * @desc econnoisseur-robot
 * @date 18-6-29 上午10:13
 */
public class KkcoinApi implements IApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(KkcoinApi.class);
    private String urlPrefix;
    private String apiKey;
    private PrivateKey privateKey;

    private static final String SUCCESS_CODE = "0";
    private static RSAUtil rsaUtil = new RSAUtil(RSAUtil.CHARSET_UTF8, RSAUtil.SIGNATURE_ALGORITHM);

    public KkcoinApi(String urlPrefix, String apiKey, String privateKey) throws InvalidKeySpecException {
        this.urlPrefix = urlPrefix;
        this.apiKey = apiKey;
        this.privateKey = rsaUtil.restorePrivateKey(privateKey);
    }

    /**
     * 获取某个币挂单价
     *
     * @return
     */
    @Override
    public Ticker ticker(CurrencyPair pair) {
        Ticker ticker = new Ticker(SERVER);
        JSONObject param = new JSONObject()
                .fluentPut("symbol", Market.valueOf(pair));

        String response = doRequest(TICKER_PATH, param);
        HttpRequest.handler(response, ticker, (resp, dto) -> {
            JSONArray array = JSON.parseArray(resp);
            if (null != array && array.size() == 12) {
                dto.setBid(array.getBigDecimal(1))
                        .setAsk(array.getBigDecimal(3))
                        .setHigh(array.getBigDecimal(9))
                        .setLow(array.getBigDecimal(10))
                        .setLast(array.getBigDecimal(7))
                        .setVol(array.getBigDecimal(8))
                        .setCode(OK);
            }
        });
        return ticker;
    }

    @Override
    public Balances balances() {
        Balances balances = new Balances(SERVER);
        String response = doRequest(BALANCE_PATH, null);
        HttpRequest.handler(response, balances, (resp, dto) -> {
            List<BalanceDto> balArray = JSON.parseArray(resp, BalanceDto.class);
            if (!CollectionUtils.isEmpty(balArray)) {
                for (BalanceDto item : balArray) {
                    dto.setBalance(Currency.get(item.getAssetSymbol()), new Balance()
                            .setTotal(item.getBal())
                            .setAvailable(item.getAvailableBal())
                            .setFrozen(item.getFrozenBal()));
                }
                dto.setCode(OK);
            }
        });
        return balances;
    }

    /**
     * 委托交易 done
     *
     * @param pair      交易对符号
     * @param operation BUY / 买，SELL / 卖
     * @param price     委托价格
     * @param amount    委托数量
     * @return
     */
    @Override
    public Order limit(CurrencyPair pair, OrderOperation operation, BigDecimal price, BigDecimal amount) {
        Order order = new Order(SERVER);
        JSONObject jsonObject = new JSONObject(true)
                        .fluentPut("amount", amount.toString())
                        .fluentPut("orderop", operation.name())
                        .fluentPut("ordertype", OrderType.LIMIT.name())
                        .fluentPut("price", price.toString())
                        .fluentPut("symbol", Market.valueOf(pair));
        String response = doRequest(TRADE_PATH, jsonObject);

        HttpRequest.handler(response, order, (resp, dto) -> {
            JSONArray trade = JSON.parseArray(resp);
            if (!CollectionUtils.isEmpty(trade)) {
                String code = trade.getJSONObject(0).getString("code");
                if (SUCCESS_CODE.equals(code)) {
                    dto.setId(trade.getJSONObject(1).getString("order_id"))
                            .setCode(OK);
                } else {
                    LOGGER.info("订单提交失败，返回码错误");
                }
            }
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
        JSONObject jsonObject = new JSONObject()
                .fluentPut("symbol", Market.valueOf(pair));
        String response = doRequest(ORDERS_PATH, jsonObject);

        HttpRequest.handler(response, orders, (resp, dto) -> {
            JSONArray array = JSON.parseArray(resp);
            if (!CollectionUtils.isEmpty(array)) {
                for (Object item : array) {
                    if (item instanceof JSONObject) {
                        JSONObject object = (JSONObject) item;
                        dto.addOrder(new Order()
                                .setId(object.getString("order_id"))
                                .setAmount(new BigDecimal(object.getString("executed_amount")))
                                .setPrice(new BigDecimal(object.getString("executed_price")))
                                .setOperation(OrderOperation.get(object.getString("orderop")))
                                .setTotalAmount(new BigDecimal(object.getString("origin_amount")))
                                .setTotalPrice(new BigDecimal(object.getString("price")))
                                .setType(OrderType.get(object.getString("type")))
                                .setStatus(OrderStatus.valueOf(object.getString("status")))
                        );
                    }
                }
                dto.setCode(OK);
            }
        });
        return orders;
    }

    @Override
    public Orders finishedOrders(CurrencyPair pair) {
        throw new RuntimeException("Not supported");
    }


    @Override
    public Order getOrder(CurrencyPair pair, String orderId) {
        Order order = new Order(SERVER);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", orderId);
        String response = doRequest(ORDER_STATE_PATH, jsonObject);

        HttpRequest.handler(response, order, (resp, dto) -> {
            JSONObject object = JSONObject.parseObject(resp);
            if (null != dto) {
                dto.setId(object.getString("order_id"))
                        .setAmount(new BigDecimal(object.getString("executed_amount")))
                        .setPrice(new BigDecimal(object.getString("executed_price")))
                        .setOperation(OrderOperation.get(object.getString("orderop")))
                        .setTotalAmount(new BigDecimal(object.getString("origin_amount")))
                        .setTotalPrice(new BigDecimal(object.getString("price")))
                        .setType(OrderType.get(object.getString("type")))
                        .setStatus(OrderStatus.valueOf(object.getString("status")))
                        .setCode(OK);
            }
        });
        return order;
    }

    /**
     * 取消委托
     *
     * @param pair    交易对符号
     * @param orderId 委托id
     * @return
     */
    @Override
    public Order cancelOrder(CurrencyPair pair, String orderId) {
        Order order = new Order(SERVER);
        JSONObject jsonObject = new JSONObject()
                .fluentPut("id", orderId);
        String response = doRequest(CANCEL_TRADE, jsonObject);

        HttpRequest.handler(response, order, (resp, dto) -> {
            JSONArray cancelArray = JSON.parseArray(resp);
            if (!CollectionUtils.isEmpty(cancelArray)) {
                String code = cancelArray.getJSONObject(0).getString("code");
                if (SUCCESS_CODE.equals(code)) {
                    dto.setId(cancelArray.getJSONObject(1).getString("order_id")).setCode(OK);
                } else {
                    LOGGER.info("取消失败，返回码错误");
                }
            } else {
                LOGGER.info("取消失败，无值返回");
            }
        });
        return order;
    }


    /**
     * 获取签名
     *
     * @param tradeType  类型
     * @param data       请求数据
     * @param second     交易时间毫秒
     * @return
     */
    private String sign(String tradeType, String data, String second) {
        StringBuilder sb = new StringBuilder(tradeType);
        if (null == data) {
            sb.append("[]");
        } else {
            sb.append(data);
        }
        String string = sb.append(second).toString();
        try {
            return rsaUtil.generateSign(this.privateKey, string);
        } catch (Exception e) {
            LOGGER.info("签名失败", e);
            return null;
        }
    }

    /**
     * 发送请求
     *
     * @param apiPath 请求地址
     * @param params  请求参数
     * @return
     */
    private String doRequest(KkcoinApiPath apiPath, JSONObject params) {
        if (StringUtils.isNotBlank(apiPath.getTradeType())) {
            String second = String.valueOf(System.currentTimeMillis() / 1000);
            String data = null == params ? null : params.toJSONString();
            Header kkcoinapikey = new BasicHeader("KKCOINAPIKEY", this.apiKey);
            Header kkcoinsign = new BasicHeader("KKCOINSIGN", sign(apiPath.getTradeType(), data, second));
            Header kkcointimestamp = new BasicHeader("KKCOINTIMESTAMP", second);
            return request(this.urlPrefix, apiPath, params, kkcoinapikey, kkcoinsign, kkcointimestamp);
        } else {
            return request(this.urlPrefix, apiPath, params);
        }
    }

    /**
     * 发起请求
     *
     * @param urlPrefix
     * @param apiPath
     * @param map
     * @param headers
     * @return
     */
    private String request(String urlPrefix, KkcoinApiPath apiPath, Map<String, Object> map, Header... headers) {
        String url = urlPrefix + apiPath.getPath();
        return HttpRequest.request(url, apiPath.getMethod(), map, headers);
    }
}

