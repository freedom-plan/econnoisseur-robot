package com.github.kevin.econnoisseur.exchanges.coinex.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.kevin.econnoisseur.dto.*;
import com.github.kevin.econnoisseur.exchanges.coinex.dto.BalanceDto;
import com.github.kevin.econnoisseur.exchanges.coinex.dto.ResponseDto;
import com.github.kevin.econnoisseur.exchanges.coinex.dto.TickerDto;
import com.github.kevin.econnoisseur.exchanges.coinex.model.CoinexApiPath;
import com.github.kevin.econnoisseur.exchanges.coinex.model.Market;
import com.github.kevin.econnoisseur.exchanges.coinex.util.MD5Util;
import com.github.kevin.econnoisseur.model.*;
import com.github.kevin.econnoisseur.service.IApi;
import com.github.kevin.econnoisseur.util.HttpRequest;
import com.github.kevin.econnoisseur.util.JacksonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.kevin.econnoisseur.exchanges.coinex.model.CoinexApiPath.BALANCE_PATH;
import static com.github.kevin.econnoisseur.exchanges.coinex.model.CoinexApiPath.TICKER_PATH;

/**
 * CoinexApi
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月29日 16:19:00
 */
public class CoinexApi implements IApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(CoinexApi.class);

    private String urlPrefix;
    private String secretKey;
    private String accessId;

    public CoinexApi(String urlPrefix, String accessId, String secretKey) {
        this.urlPrefix = urlPrefix;
        this.accessId = accessId;
        this.secretKey = secretKey;
    }

    @Override
    public Ticker ticker(CurrencyPair pair) {
        Ticker ticker = new Ticker(Code.SERVER);

        HashMap<String, Object> param = new HashMap<>();
        param.put("market", Market.valueOf(pair));
        String response = doRequest(TICKER_PATH, param);
        ResponseDto<TickerDto> dto = JacksonUtil.toObject(response, new TypeReference<ResponseDto<TickerDto>>(){});

        if (checkResponse(dto) && null != dto.getData() && null != dto.getData().getTicker()) {
            ticker = dto.getData().getTicker().convert();
        }
        return ticker;
    }

    @Override
    public Balances balances() {
        Balances balances = new Balances(Code.SERVER);

        String response = doRequest(BALANCE_PATH, null);
        ResponseDto<Map<String, BalanceDto>> dto = JacksonUtil.toObject(response, new TypeReference<ResponseDto<Map<String, BalanceDto>>>(){});

        if (checkResponse(dto) && !CollectionUtils.isEmpty(dto.getData())) {
            balances.setCode(Code.OK);
            dto.getData().forEach((key, value) -> {
                Currency currency = Currency.get(key);
                if (null == currency) {
                    LOGGER.info("未找到{}对应的枚举", key);
                } else {
                    BigDecimal total = value.getAvailable().add(value.getFrozen());
                    Balance balance = new Balance()
                            .setAvailable(value.getAvailable())
                            .setFrozen(value.getFrozen())
                            .setTotal(total);

                    balances.setBalance(currency, balance);
                }
            });
        }

        return balances;
    }

    @Override
    public Order trade(CurrencyPair pair, OrderType type, OrderOperation operation, BigDecimal price, BigDecimal amount) {
        return null;
    }

    @Override
    public Orders pandingOrders(CurrencyPair pair) {
        return null;
    }

    @Override
    public Orders finishedOrders(CurrencyPair pair) {
        return null;
    }

    @Override
    public Order getOrder(CurrencyPair pair, Long orderId) {
        return null;
    }

    @Override
    public Order cancelOrder(CurrencyPair pair, Long orderId) {
        return null;
    }

    /**
     * 检查返回值
     * @param responseDto 返回值
     * @return
     */
    private static boolean checkResponse(ResponseDto<?> responseDto) {
        boolean status = false;
        if (null != responseDto && 0 == responseDto.getCode() && null != responseDto.getData()) {
            status = true;
        }
        return status;
    }

    private String doRequest(CoinexApiPath apiPath, Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put("access_id", this.accessId);
        params.put("tonce", System.currentTimeMillis());

        String authorization = MD5Util.buildMysignV1(params, this.secretKey);
        return this.request(urlPrefix, apiPath, params, authorization);
    }

    private String request(String urlPrefix, CoinexApiPath apiPath, Map<String, Object> params, String authorization) {
        String url = urlPrefix + apiPath.getPath();
        HttpRequestBase http;
        switch (apiPath.getMethod()) {
            case "POST":
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new StringEntity(JacksonUtil.toJson(params), HttpRequest.DEFAULT_CHARSET));
                http = httpPost;
                break;
            case "DELETE":
                HttpDelete httpDelete = new HttpDelete(HttpRequest.generateURL(url, params));
                http = httpDelete;
                break;
            default: // "GET"
                HttpGet httpGet = new HttpGet(HttpRequest.generateURL(url, params));
                http = httpGet;
                break;
        }

        http.setConfig(HttpRequest.CONFIG);
        http.setHeader("Content-Type", "application/json");
        if (StringUtils.isNotBlank(authorization)) {
            http.setHeader("authorization", authorization);
        }
        return HttpRequest.execute(http);
    }
}
