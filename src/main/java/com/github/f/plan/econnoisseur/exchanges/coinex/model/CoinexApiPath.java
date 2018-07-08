package com.github.f.plan.econnoisseur.exchanges.coinex.model;

/**
 *
 * CoinexApiPath
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月20日 20:56:00
 */
public enum CoinexApiPath {
    TICKER_PATH("/v1/market/ticker", false, "GET"), // 行情URL
    DEPTH_PATH("/v1/market/depth", false, "GET"), // 市场深度URL
    TRADES_PATH("/v1/market/deals", false, "GET"), // 历史交易信息URL
    KLINE_PATH("/v1/market/kline", false, "GET"), // 历史交易信息URL

    BALANCE_PATH("/v1/balance/", true, "GET"), //  批量获取用户订单URL
    PENDING_ORDER_PATH("/v1/order/pending", true, "GET"), //  批量获取用户未执行的订单URL
    FINISHED_ORDER_PATH("/v1/order/finished", true, "GET"), //  批量获取用户执行的订单URL
    PUT_LIMIT_PATH("/v1/order/limit", true, "POST"), // 下限价单
    PUT_MARKET_PATH("/v1/order/market", true, "POST"), // 下限价单
    GET_ORDER_PATH("/v1/order", true, "GET"), //  获取订单状态
    CANCEL_ORDER_PATH("/v1/order/pending", true, "DELETE"), //  撤销订单URL

    MINING_DIFFICULTY_PATH("/v1/order/mining/difficulty", true, "GET"); //  撤销订单URL

    private String path;
    private boolean auth;
    private String method;

    private CoinexApiPath(String path, boolean auth, String method) {
        this.path = path;
        this.auth = auth;
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public CoinexApiPath setPath(String path) {
        this.path = path;
        return this;
    }

    public boolean isAuth() {
        return auth;
    }

    public CoinexApiPath setAuth(boolean auth) {
        this.auth = auth;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public CoinexApiPath setMethod(String method) {
        this.method = method;
        return this;
    }
}



