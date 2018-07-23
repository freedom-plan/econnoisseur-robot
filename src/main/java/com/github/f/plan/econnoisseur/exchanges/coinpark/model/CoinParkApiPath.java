package com.github.f.plan.econnoisseur.exchanges.coinpark.model;

/**
 * KkcoinApiPath
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月20日 20:56:00
 */
public enum CoinParkApiPath {
    /**
     * 查询市场ticker
     */
    TICKER_PATH("/v1/mdata", "ticker", false, "GET"),
    /**
     * 查询账户资产
     */
    BALANCE_PATH("/v1/transfer", "transfer/assets", true, "POST"),
    /**
     * 委托单详情
     */
    ORDERS_DETAIL_PATH("/v1/orderpending", "orderpending/order", true, "POST"),
    /**
     * 下单
     */
    TRADE_PATH("/v1/orderpending", "orderpending/trade", true, "POST"),
    /**
     * 撤单
     */
    CANCEL_TRADE("/v1/orderpending", "orderpending/cancelTrade", true, "POST"),
    /**
     * 当前委托
     */
    PENDING_LIST_TRADE("/v1/orderpending", "orderpending/orderPendingList", true, "POST"),
    ;

    private String path;
    private String cmd;
    private boolean auth;
    private String method;

    private CoinParkApiPath(String path, String cmd, boolean auth, String method) {
        this.path = path;
        this.cmd = cmd;
        this.auth = auth;
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public String getCmd() {
        return cmd;
    }

    public String getMethod() {
        return method;
    }

    public CoinParkApiPath setPath(String path) {
        this.path = path;
        return this;
    }

    public CoinParkApiPath setCmd(String cmd) {
        this.cmd = cmd;
        return this;
    }

    public boolean isAuth() {
        return auth;
    }

    public CoinParkApiPath setAuth(boolean auth) {
        this.auth = auth;
        return this;
    }

    public CoinParkApiPath setMethod(String method) {
        this.method = method;
        return this;
    }
}



