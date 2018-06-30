package com.github.kevin.econnoisseur.exchanges.kkcoin.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.kevin.econnoisseur.dto.*;
import com.github.kevin.econnoisseur.exchanges.kkcoin.model.Market;
import com.github.kevin.econnoisseur.exchanges.kkcoin.dto.BalanceDto;
import com.github.kevin.econnoisseur.exchanges.kkcoin.model.KKCoinApiPath;
import com.github.kevin.econnoisseur.exchanges.kkcoin.util.RSAUtil;
import com.github.kevin.econnoisseur.model.*;
import com.github.kevin.econnoisseur.service.IApi;
import com.github.kevin.econnoisseur.util.HttpRequest;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.kevin.econnoisseur.exchanges.kkcoin.model.KKCoinApiPath.*;
import static com.github.kevin.econnoisseur.model.Code.OK;

/**
 * StockApi
 *
 * @author jimmy
 * @version 0.1.0
 * @desc econnoisseur-robot
 * @date 18-6-29 上午10:13
 */
public class StockApi implements IApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockApi.class);

    private String timeMill = "";

    private static final String SUCCESS = "0";
    private static final String BUY = "0";

    private String urlPrefix;

    private String apiKey;

    private String privateKey;

    public StockApi(String urlPrefix, String apiKey, String privateKey) {
        this.urlPrefix = urlPrefix;
        this.apiKey = apiKey;
        this.privateKey = privateKey;
    }

    /**
     * 获取某个币挂单价
     *
     * @return
     */
    public String ticker() {
        return null;
    }

    /**
     * 查询委托状态 done
     *
     * @param orderId 订单编号
     * @return
     */
    public String order(String orderId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", orderId);
        String sign = sign("order", jsonObject.toJSONString());
        if (null == sign) {
            return null;
        }
        return doRequest(sign, ORDER_STATE_PATH, jsonObject);
    }

    /**
     * 查询某个币种委托 done
     *
     * @param symbol 交易对符号
     * @return
     */
    public String openorders(String symbol) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("symbol", symbol);
        String sign = sign("openorders", jsonObject.toJSONString());
        if (null == sign) {
            return null;
        }
        return doRequest(sign, ORDERS_PATH, jsonObject);
    }

    /**
     * 委托交易 done
     *
     * @param symbol 交易对符号
     * @param orderType 委托类型 LIMIT/限价单
     * @param orderOp BUY / 买，SELL / 卖
     * @param price 委托价格
     * @param amount 委托数量
     * @return
     */
    public String trade(String symbol, String orderType, String orderOp, String price, String amount) {
        JSONObject jsonObject =
            new JSONObject(true)
                .fluentPut("amount", amount)
                .fluentPut("orderop", orderOp)
                .fluentPut("ordertype", orderType)
                .fluentPut("price", price)
                .fluentPut("symbol", symbol);
        String sign = sign("trade", jsonObject.toJSONString());
        if (null == sign) {
            return null;
        }
        return doRequest(sign, TRADE_PATH, jsonObject);
    }

    /**
     * 取消委托
     *
     * @param orderId 委托id
     * @return
     */
    public String cancel(String orderId) {
        JSONObject jsonObject =
            new JSONObject().fluentPut("id", orderId);
        String sign = sign("cancel", jsonObject.toJSONString());
        if (null == sign) {
            return null;
        }
        return doRequest(sign, CANCEL_TRADE, jsonObject);
    }

    /**
     * 获取签名
     * @param tradeType 类型
     * @param data 请求数据
     * @return
     */
    private String sign(String tradeType, String data) {
        StringBuilder signStr = new StringBuilder(tradeType);
        String timeMillisStr = String.valueOf(System.currentTimeMillis());
        timeMill = timeMillisStr.substring(0, timeMillisStr.length() - 3);
        if (null == data) {
            signStr.append("[]");
        } else {
            signStr.append(data);
        }
        String string = signStr.append(timeMill).toString();
        try {
            LOGGER.info(string);
            return new RSAUtil().generateSign(this.privateKey, string);
        } catch (Exception e) {
            LOGGER.info("签名失败", e);
            return null;
        }
    }

    /**
     * 发送请求
     *
     * @param sign 签名字符串
     * @param apiPath 请求地址
     * @param params 请求参数
     * @return
     */
    private String doRequest(String sign, KKCoinApiPath apiPath, Map<String, Object> params) {
        if (null != sign && sign.length() > 0) {
            Header kkcoinapikey = new BasicHeader("KKCOINAPIKEY", this.apiKey);
            Header kkcoinsign = new BasicHeader("KKCOINSIGN", sign);
            Header kkcointimestamp = new BasicHeader("KKCOINTIMESTAMP", timeMill);
            return request(urlPrefix, apiPath, params, kkcoinapikey, kkcoinsign, kkcointimestamp);
        } else {
            return request(urlPrefix, apiPath, params);
        }
    }

    /**
     * 发起请求
     * @param urlPrefix
     * @param apiPath
     * @param map
     * @param headers
     * @return
     */
    private String request(String urlPrefix, KKCoinApiPath apiPath, Map<String, Object> map,
        Header... headers) {
        String url = urlPrefix + apiPath.getPath();
        HttpRequestBase http;
        switch (apiPath.getMethod()) {
            case "POST":
                HttpPost httpPost = new HttpPost(url);
                List<NameValuePair> params = new ArrayList<>();
                map.forEach((key, value) -> params.add(new BasicNameValuePair(key, String.valueOf(value))));
                httpPost.setEntity(new UrlEncodedFormEntity(params, HttpRequest.DEFAULT_CHARSET));
                http = httpPost;
                break;
            default: // "GET"
                HttpGet httpGet = new HttpGet(HttpRequest.generateURL(url, map));
                http = httpGet;
                break;
        }

        http.setConfig(HttpRequest.CONFIG);
        for (Header header : headers) {
            http.addHeader(header);
        }
        return HttpRequest.execute(http);
    }

    public static void main(String[] args) {
//        System.out.println(stockApi.order("20180629000000580420"));
        //        System.out.println(stockApi.cancel("20180629000000580420"));
//        System.out.println(stockApi.openorders("IOST_ETH"));

//        Balances balances = stockApi.balances();
//        System.out.println(balances);
//        Ticker ticker = stockApi.ticker(CurrencyPair.IOST_ETH);
//        Order trade = stockApi.trade(CurrencyPair.IOST_ETH, OrderType.LIMIT, OrderOperation
//            .SELL, new BigDecimal("1"), new BigDecimal("200"));
//        Order order = stockApi.getOrder(null, "20180630000000271507");
//        System.out.println("test");
    }

    @Override public Ticker ticker(CurrencyPair pair) {
        JSONObject symbol = new JSONObject().fluentPut("symbol", Market.valueOf(pair));
        String response = doRequest("", TICKER_PATH, symbol);
        JSONArray array = JSON.parseArray(response);
        return new Ticker(OK)
            .setBid(array.getBigDecimal(1))
            .setAsk(array.getBigDecimal(3))
            .setHigh(array.getBigDecimal(9))
            .setLow(array.getBigDecimal(10))
            .setLast(array.getBigDecimal(7))
            .setVol(array.getBigDecimal(8));
    }

    @Override public Balances balances() {
        String sign = sign("balance", null);
        if (null == sign) {
            return null;
        }
        String response = doRequest(sign, BALANCE_PATH, null);
        List<BalanceDto> balArray = JSON.parseArray(response, BalanceDto.class);
        Balances balances = new Balances(OK);
        balArray.forEach(p -> {
            balances.setBalance(Currency.get(p.getAssetSymbol()), new Balance().setTotal(p.getBal
                ()).setAvailable(p.getAvailableBal()).setFrozen(p.getFrozenBal()));
        });
        return balances;
    }

    @Override public Order trade(CurrencyPair pair, OrderType type, OrderOperation operation,
        BigDecimal price, BigDecimal amount) {
        JSONObject jsonObject =
            new JSONObject(true)
                .fluentPut("amount", amount.toString())
                .fluentPut("orderop", operation)
                .fluentPut("ordertype", type)
                .fluentPut("price", price.toString())
                .fluentPut("symbol", Market.valueOf(pair));
        String sign = sign("trade", jsonObject.toJSONString());
        if (null == sign) {
            return null;
        }
        String response = doRequest(sign, TRADE_PATH, jsonObject);

        JSONArray trade = JSON.parseArray(response);
        if (null != trade && trade.size() > 0) {
            String code = trade.getJSONObject(0).getString("code");
            Order order = new Order().setStatus(code);
            if (SUCCESS.equals(code)) {
                return order.setId(trade.getJSONObject(1).getString("order_id"));
            } else {
                LOGGER.info("订单提交失败，返回码错误");
                return order;
            }
        } else {
            LOGGER.info("订单提交失败，无值返回");
            return null;
        }
    }

    @Override public Orders pandingOrders(CurrencyPair pair) {
        return null;
    }

    @Override public Orders finishedOrders(CurrencyPair pair) {
        return null;
    }

    @Override public Order getOrder(CurrencyPair pair, String orderId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", orderId);
        String sign = sign("order", jsonObject.toJSONString());
        if (null == sign) {
            return null;
        }
        String responseStr = doRequest(sign, ORDER_STATE_PATH, jsonObject);
        LOGGER.info("获取订单消息返回结果：{}", responseStr);
        JSONObject response = JSONObject.parseObject(responseStr);
        return new Order().setId(response.getString("order_id"))
            .setAmount(new BigDecimal(response.getString("executed_amount")))
            .setPrice(new BigDecimal(response.getString("executed_price")))
            .setOperation(BUY.equals(response.getString("orderop")) ? OrderOperation.BUY :
                OrderOperation.SELL)
            .setTotalAmount(new BigDecimal(response.getString("origin_amount")))
            .setTotalPrice(new BigDecimal(response.getString("price")))
            .setType(OrderType.LIMIT)
            .setStatus(response.getString("status"));
    }

    @Override public Order cancelOrder(CurrencyPair pair, String orderId) {
        JSONObject jsonObject =
            new JSONObject().fluentPut("id", orderId);
        String sign = sign("cancel", jsonObject.toJSONString());
        if (null == sign) {
            return null;
        }
        String resoponse = doRequest(sign, CANCEL_TRADE, jsonObject);
        JSONArray cancelArray = JSON.parseArray(resoponse);
        if (null != cancelArray && cancelArray.size() > 0) {
            String code = cancelArray.getJSONObject(0).getString("code");
            Order order = new Order().setStatus(code);
            if (SUCCESS.equals(code)) {
                return order.setId(cancelArray.getJSONObject(1).getString("order_id"));
            } else {
                LOGGER.info("订单{}取消失败，返回码错误", orderId);
                return order;
            }
        } else {
            LOGGER.info("订单{}取消失败，无值返回", orderId);
            return null;
        }
    }
}

