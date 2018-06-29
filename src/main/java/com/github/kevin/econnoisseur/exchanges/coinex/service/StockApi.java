//package com.github.kevin.econnoisseur.exchanges.coinex.service;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.github.kevin.econnoisseur.exchanges.coinex.dto.OrderDto;
//import com.github.kevin.econnoisseur.exchanges.coinex.dto.PageDto;
//import com.github.kevin.econnoisseur.exchanges.coinex.dto.ResponseDto;
//import com.github.kevin.econnoisseur.exchanges.coinex.dto.TradesDto;
//import com.github.kevin.econnoisseur.exchanges.coinex.model.ApiPath;
//import com.github.kevin.econnoisseur.exchanges.coinex.model.MARKET;
//import com.github.kevin.econnoisseur.model.OrderType;
//import com.github.kevin.econnoisseur.util.JacksonUtil;
//
//import java.math.BigDecimal;
//import java.util.HashMap;
//import java.util.List;
//
//import static com.github.kevin.econnoisseur.exchanges.coinex.model.ApiPath.*;
//
//
//public class StockApi {
//    private String urlPrefix;
//    private String secretKey;
//    private String accessId;
//
//    public StockApi(String urlPrefix, String accessId, String secretKey) {
//        this.urlPrefix = urlPrefix;
//        this.accessId = accessId;
//        this.secretKey = secretKey;
//    }
//
//    public String depth(MARKET market, String merge, Integer limit) {
//
//        HashMap<String, Object> param = new HashMap<>();
//        param.put("market", market.toString());
//        param.put("merge", merge);
//        param.put("limit", String.valueOf(limit));
//        return doRequest(DEPTH_PATH, param);
//    }
//
//    public ResponseDto<List<TradesDto>> trades(MARKET market) {
//        HashMap<String, Object> param = new HashMap<>();
//        param.put("market", market.toString());
//        String response = doRequest(TRADES_PATH, param);
//        return JacksonUtil.toObject(response, new TypeReference<ResponseDto<List<TradesDto>>>(){});
//    }
//
//    public String kline(MARKET market, String type) {
//        HashMap<String, Object> param = new HashMap<>();
//        param.put("market", market.toString());
//        param.put("type", type);
//        return doRequest(KLINE_PATH, param);
//    }
//
//    public ResponseDto<PageDto> pendingOrder(MARKET market, int page, int size) {
//        return orders(PENDING_ORDER_PATH, market, page, size);
//    }
//
//    /**
//     *
//     * @param market
//     * @param page 1
//     * @param size Amount per page(1-100)
//     * @return
//     */
//    public ResponseDto<PageDto> finishedOrder(MARKET market, int page, int size) {
//        return orders(FINISHED_ORDER_PATH, market, page, size);
//    }
//
//    public ResponseDto<PageDto> orders(ApiPath apiPath, MARKET market, int page, int size) {
//        HashMap<String, Object> param = new HashMap<>();
//        param.put("market", market.toString());
//        param.put("page", page);
//        param.put("limit", size);
//        String response = doRequest(apiPath, param);
//        return JacksonUtil.toObject(response, new TypeReference<ResponseDto<PageDto>>(){});
//    }
//
//    public ResponseDto<OrderDto> putLimitOrder(MARKET market, OrderType type, BigDecimal amount, BigDecimal price) {
//        HashMap<String, Object> param = new HashMap<>();
//        param.put("market", market.toString());
//        param.put("type", type.toString());
//        param.put("amount", String.valueOf(amount));
//        param.put("price", String.valueOf(price));
//        String response = doRequest(PUT_LIMIT_PATH, param);
//        return JacksonUtil.toObject(response, new TypeReference<ResponseDto<OrderDto>>(){});
//    }
//
//    public String putMarketOrder(MARKET market, OrderType type, BigDecimal amount) {
//        HashMap<String, Object> param = new HashMap<>();
//        param.put("market", market.toString());
//        param.put("type", type.toString());
//        param.put("amount", String.valueOf(amount));
//        return doRequest(PUT_MARKET_PATH, param);
//    }
//
//    public ResponseDto<OrderDto> getOrder(MARKET market, Long orderId) {
//        HashMap<String, Object> param = new HashMap<>();
//        param.put("market", market.toString());
//        param.put("id", orderId);
//        String response = doRequest(GET_ORDER_PATH, param);
//        return JacksonUtil.toObject(response, new TypeReference<ResponseDto<OrderDto>>(){});
//    }
//
//    public String cancelOrder(MARKET market, Long orderId) {
//        HashMap<String, Object> param = new HashMap<>();
//        param.put("market", market.toString());
//        param.put("id", orderId);
//        return doRequest(CANCEL_ORDER_PATH, param);
//    }
//
//}