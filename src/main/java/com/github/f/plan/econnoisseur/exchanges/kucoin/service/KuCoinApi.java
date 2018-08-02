package com.github.f.plan.econnoisseur.exchanges.kucoin.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.f.plan.econnoisseur.dto.HttpConsumer;
import com.github.f.plan.econnoisseur.exchanges.common.dto.*;
import com.github.f.plan.econnoisseur.exchanges.common.model.Currency;
import com.github.f.plan.econnoisseur.exchanges.kucoin.model.Market;
import com.github.f.plan.econnoisseur.exchanges.kucoin.util.SignUtil;
import com.github.f.plan.econnoisseur.exchanges.common.iface.IApi;
import com.github.f.plan.econnoisseur.exchanges.common.model.CurrencyPair;
import com.github.f.plan.econnoisseur.exchanges.common.model.OrderOperation;
import com.github.f.plan.econnoisseur.exchanges.kucoin.model.KuCoinApiPath;
import com.github.f.plan.econnoisseur.util.HttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import static com.github.f.plan.econnoisseur.exchanges.common.model.Code.OK;
import static com.github.f.plan.econnoisseur.exchanges.common.model.Code.SERVER;

/**
 * Copyright (C), 2018
 *
 * @author jimmy
 * @desc KuCoinApi
 * @date 2018/7/26
 */
public class KuCoinApi implements IApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(KuCoinApi.class);
    private String urlPrefix;
    private String apiKey;
    private String secretKey;

    public KuCoinApi(String urlPrefix, String apiKey, String secretKey) {
        this.urlPrefix = urlPrefix;
        this.apiKey = apiKey;
        this.secretKey = secretKey;
    }

    @Override public Ticker ticker(CurrencyPair pair) {
        Ticker ticker = new Ticker(SERVER);

        JSONObject param = new JSONObject()
            .fluentPut("symbol", Market.valueOf(pair));

        String response = doRequest(KuCoinApiPath.TICKER_PATH, param);
        handler(response, ticker, (jsonObject, dto) -> {
            dto.setAsk(jsonObject.getBigDecimal("sell"))
                .setBid(jsonObject.getBigDecimal("buy"))
                .setHigh(jsonObject.getBigDecimal("high"))
                .setLast(jsonObject.getBigDecimal("lastDealPrice"))
                .setLow(jsonObject.getBigDecimal("low"))
                .setVol(jsonObject.getBigDecimal("volValue"));
        });

        return ticker;
    }

    @Override public Balances balances() {
        Balances balances = new Balances(SERVER);
        JSONObject param = new JSONObject()
            .fluentPut("limit", 20);
        for (int i = 0; i < 10; i++) {
            param.fluentPut("page", i + 1);
            String response = doRequest(KuCoinApiPath.BALANCE_PATH, param);
            handler(response, balances, (jsonObject, dto) -> {
                Optional.ofNullable(jsonObject.getJSONArray("datas"))
                    .ifPresent(p->p.toJavaList(JSONObject.class)
                        .forEach(q -> {
                            if (!q.getString("balance").equals("0.0")) {
                                dto.setBalance(Currency.get(q.getString("coinType")), new Balance().setAvailable(q.getBigDecimal("balance")).setFrozen(q.getBigDecimal("freezeBalance")));
                            }
                        }));
            });
        }

        return balances;
    }

    @Override public Order limit(CurrencyPair pair, OrderOperation operation, BigDecimal price, BigDecimal amount) {
        Order order = new Order();
        JSONObject param = new JSONObject()
            .fluentPut("type", operation.name())
            .fluentPut("price", price)
            .fluentPut("amount", amount)
            .fluentPut("symbol", Market.valueOf(pair))
            ;

        String response = doRequest(KuCoinApiPath.TRADE_PATH, param);
        handler(response, order, (jsonObject, dto) -> {
            dto.setId(jsonObject.getString("orderOid"));
        });
        return order;
    }

    @Override public Order market(CurrencyPair pair, OrderOperation operation, BigDecimal amount) {
        return null;
    }

    @Override public Orders pandingOrders(CurrencyPair pair) {
        return null;
    }

    @Override public Orders finishedOrders(CurrencyPair pair) {
        return null;
    }

    @Override public Order getOrder(CurrencyPair pair, String orderId) {
        //由于该接口需要订单类型（买或卖），暂时使用不了
        return null;
    }

    @Override public Order cancelOrder(CurrencyPair pair, String orderId) {
        //由于该接口需要订单类型（买或卖），暂时使用不了
        return null;
    }

    private static <T extends BaseResp> void handler(String response, T model, HttpConsumer<JSONObject, T> action) {
        boolean bool = false;
        if (StringUtils.isNotBlank(response)) {
            try {
                JSONObject jsonObject = JSON.parseObject(response);
                if (!jsonObject.isEmpty() && jsonObject.getBoolean("success")) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (null != data) {
                        if (null != action) {
                            action.accept(data, model);
                        }
                        bool = true;
                    } else {
                        Thread.sleep(6000);
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
    private String doRequest(KuCoinApiPath apiPath, JSONObject params) {
        if (null == params) {
            params = new JSONObject();
        }
        String url = this.urlPrefix + apiPath.getPath();
        if (apiPath.isAuth()) {
            String nonce = String.valueOf(System.currentTimeMillis());

            Header kucoinapikey = new BasicHeader("KC-API-KEY", this.apiKey);
            Header kucoinsign = new BasicHeader("KC-API-NONCE", nonce);
            Header kucointimestamp = null;
            try {
                kucointimestamp = new BasicHeader("KC-API-SIGNATURE", new SignUtil(this.secretKey).getSign(params, apiPath.getEndpoint(), nonce));
                return HttpRequest.request(url, apiPath.getMethod(), params, kucoinapikey, kucoinsign, kucointimestamp);
            } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                LOGGER.info("调用出错");
                return null;
            }
        } else {
            return HttpRequest.request(url, apiPath.getMethod(), params);
        }
    }

}
