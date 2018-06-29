package com.github.kevin.econnoisseur.exchanges.coinex.model;

/**
 *
 * CoinexApiPath
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月20日 20:56:00
 */
public enum CoinexApiPath {
    TICKER_PATH("market/ticker", "GET"), // 行情URL
    DEPTH_PATH("market/depth", "GET"), // 市场深度URL
    TRADES_PATH("market/deals", "GET"), // 历史交易信息URL
    KLINE_PATH("market/kline", "GET"), // 历史交易信息URL
    BALANCE_PATH("balance/", "GET"), //  批量获取用户订单URL
    PENDING_ORDER_PATH("order/pending", "GET"), //  批量获取用户未执行的订单URL
    FINISHED_ORDER_PATH("order/finished", "GET"), //  批量获取用户执行的订单URL
    PUT_LIMIT_PATH("order/limit", "POST"), // 下限价单
    PUT_MARKET_PATH("order/market", "POST"), // 下限价单
    GET_ORDER_PATH("order", "GET"), //  获取订单状态
    CANCEL_ORDER_PATH("order/pending", "DELETE"); //  撤销订单URL

    private String path;
    private String method;

    private CoinexApiPath(String path, String method) {
        this.path = path;
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public CoinexApiPath setPath(String path) {
        this.path = path;
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



