package com.github.kevin.econnoisseur.exchanges.kkcoin.model;

/**
 *
 * KKCoinApiPath
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月20日 20:56:00
 */
public enum KKCoinApiPath {
    BALANCE_PATH("balance/", "GET"), //  查询账户余额
    ORDER_STATE_PATH("order/", "GET"), //  查询当前委托
    ORDERS_PATH("openorders/", "GET"), //  查询委托状态
    TRADE_PATH("trade/", "POST"), // 委托交易
    CANCEL_TRADE("cancel/", "POST"),// 取消委托
    TICKER_PATH("ticker/", "GET"),// 取消委托
    ;

    private String path;
    private String method;

    private KKCoinApiPath(String path, String method) {
        this.path = path;
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public KKCoinApiPath setPath(String path) {
        this.path = path;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public KKCoinApiPath setMethod(String method) {
        this.method = method;
        return this;
    }
}



