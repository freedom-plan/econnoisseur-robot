package com.github.f.plan.econnoisseur.exchanges.kkcoin.model;

/**
 * KkcoinApiPath
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月20日 20:56:00
 */
public enum KkcoinApiPath {
    TICKER_PATH("ticker/", null, "GET"),
    BALANCE_PATH("balance/", "balance", "GET"), //  查询账户余额
    ORDER_STATE_PATH("order/", "order", "GET"), //  查询当前委托
    ORDERS_PATH("openorders/", "openorders", "GET"), //  查询委托状态
    TRADE_PATH("trade/", "trade", "POST"), // 委托交易
    CANCEL_TRADE("cancel/", "cancel", "POST"),// 取消委托
    ;

    private String path;
    private String tradeType;
    private String method;

    private KkcoinApiPath(String path, String tradeType, String method) {
        this.path = path;
        this.tradeType = tradeType;
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public String getTradeType() {
        return tradeType;
    }

    public String getMethod() {
        return method;
    }

}



