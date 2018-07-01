package com.github.kevin.econnoisseur.exchanges.coinex.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.kevin.econnoisseur.dto.*;
import com.github.kevin.econnoisseur.exchanges.coinex.dto.*;
import com.github.kevin.econnoisseur.exchanges.coinex.model.CoinexApiPath;
import com.github.kevin.econnoisseur.exchanges.coinex.model.Market;
import com.github.kevin.econnoisseur.exchanges.coinex.util.MD5Util;
import com.github.kevin.econnoisseur.model.Code;
import com.github.kevin.econnoisseur.model.Currency;
import com.github.kevin.econnoisseur.model.CurrencyPair;
import com.github.kevin.econnoisseur.model.OrderOperation;
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

import static com.github.kevin.econnoisseur.exchanges.coinex.model.CoinexApiPath.*;

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
    public Order limit(CurrencyPair pair, OrderOperation operation, BigDecimal price, BigDecimal amount) {
        Order order = null;
        HashMap<String, Object> param = new HashMap<>();
        param.put("market", Market.valueOf(pair));
        param.put("type", operation.name().toLowerCase());
        param.put("amount", String.valueOf(amount));
        param.put("price", String.valueOf(price));
        String response = doRequest(PUT_LIMIT_PATH, param);
        ResponseDto<OrderDto> dto = JacksonUtil.toObject(response, new TypeReference<ResponseDto<OrderDto>>(){});
        if (checkResponse(dto)) {
            order = OrderDto.convert(dto.getData());
        } else {
            order = new Order(Code.SERVER);
        }
        return order;
    }

    @Override
    public Order market(CurrencyPair pair, OrderOperation operation, BigDecimal amount) {
        Order order = null;
        HashMap<String, Object> param = new HashMap<>();
        param.put("market", Market.valueOf(pair));
        param.put("type", operation.name().toLowerCase());
        param.put("amount", String.valueOf(amount));
        String response = doRequest(PUT_MARKET_PATH, param);
        ResponseDto<OrderDto> dto = JacksonUtil.toObject(response, new TypeReference<ResponseDto<OrderDto>>(){});
        if (checkResponse(dto)) {
            order = OrderDto.convert(dto.getData());
        } else {
            order = new Order(Code.SERVER);
        }
        return order;
    }

    @Override
    public Orders pandingOrders(CurrencyPair pair) {
        return orders(PENDING_ORDER_PATH, pair);
    }

    @Override
    public Orders finishedOrders(CurrencyPair pair) {
        return orders(FINISHED_ORDER_PATH, pair);
    }

    @Override
    public Order getOrder(CurrencyPair pair, String orderId) {
        return order(GET_ORDER_PATH, pair, orderId);
    }

    @Override
    public Order cancelOrder(CurrencyPair pair, String orderId) {
        return order(CANCEL_ORDER_PATH, pair, orderId);
    }

    public Order order(CoinexApiPath apiPath, CurrencyPair pair, String orderId) {
        Order order = null;
        HashMap<String, Object> param = new HashMap<>();
        param.put("market", Market.valueOf(pair));
        param.put("id", Long.valueOf(orderId));
        String response = doRequest(apiPath, param);
        ResponseDto<OrderDto> dto = JacksonUtil.toObject(response, new TypeReference<ResponseDto<OrderDto>>(){});
        if (checkResponse(dto)) {
            order = OrderDto.convert(dto.getData());
        } else {
            order = new Order(Code.SERVER);
        }
        return order;
    }

    private ResponseDto<PageDto> pageableOrders(CoinexApiPath apiPath, CurrencyPair pair, int page, int size) {
        HashMap<String, Object> param = new HashMap<>();
        param.put("market", Market.valueOf(pair));
        param.put("page", page);
        param.put("limit", size);
        String response = doRequest(apiPath, param);
        return JacksonUtil.toObject(response, new TypeReference<ResponseDto<PageDto>>(){});
    }

    private Orders orders(CoinexApiPath apiPath, CurrencyPair pair) {
        Orders orders = new Orders(Code.SERVER);
        int pageNum = 1;
        PageDto page;

        do {
            page = null;
            ResponseDto<PageDto> dto = pageableOrders(apiPath, pair, pageNum, 100);
            if (checkResponse(dto) && null != dto.getData()) {
                page = dto.getData();
                List<OrderDto> list = page.getData();
                if (!CollectionUtils.isEmpty(list)) {
                    Order tmp = null;
                    for (OrderDto item : list) {
                        tmp = OrderDto.convert(item);
                        if (null != tmp) {
                            orders.addOrder(tmp);
                        }
                    }
                }
            }
            pageNum++;
        } while (null != page && page.getHasNext());

        return orders;
    }

    public String depth(CurrencyPair pair, String merge, Integer limit) {
        HashMap<String, Object> param = new HashMap<>();
        param.put("market", Market.valueOf(pair));
        param.put("merge", merge);
        param.put("limit", String.valueOf(limit));
        return doRequest(DEPTH_PATH, param);
    }

    public ResponseDto<List<TradesDto>> trades(CurrencyPair pair) {
        HashMap<String, Object> param = new HashMap<>();
        param.put("market", Market.valueOf(pair));
        String response = doRequest(TRADES_PATH, param);
        return JacksonUtil.toObject(response, new TypeReference<ResponseDto<List<TradesDto>>>(){});
    }

    public String kline(CurrencyPair pair, String type) {
        HashMap<String, Object> param = new HashMap<>();
        param.put("market", Market.valueOf(pair));
        param.put("type", type);
        return doRequest(KLINE_PATH, param);
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
