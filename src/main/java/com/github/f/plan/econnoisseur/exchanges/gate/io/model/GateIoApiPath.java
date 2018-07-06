package com.github.f.plan.econnoisseur.exchanges.gate.io.model;

/**
 * GateIoApiPath
 *
 * @author Kevin Huang
 * @since version
 * 2018年07月05日 11:28:00
 */
public enum GateIoApiPath {
    TICKER_PATH("/api2/1/ticker", "pair", false, "GET"),

    BALANCE_PATH("/api2/1/private/balances", null, true, "GET"),

    BUY_PATH("/api2/1/private/buy", null, true, "POST"),
    SELL_PATH("/api2/1/private/sell", null, true, "POST"),
    CANCEL_ORDER_PATH("/api2/1/private/cancelOrder", null, true, "POST"),
    GET_ORDER_PATH("/api2/1/private/getOrder", null, true, "POST"),
    OPEN_ORDERS_PATH("/api2/1/private/openOrders", null, true, "POST"),
    MYTRADE_HISTORY_PATH("/api2/1/private/tradeHistory", null, true, "POST"),

    // 市场深度
    ORDERBOOK_PATH("/api2/1/orderBook", null, false, "GET");


    private String path;
    private String pathVar;
    private boolean auth;
    private String method;

    private GateIoApiPath(String path, String pathVar, boolean auth, String method) {
        this.path = path;
        this.pathVar = pathVar;
        this.auth = auth;
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public GateIoApiPath setPath(String path) {
        this.path = path;
        return this;
    }

    public String getPathVar() {
        return pathVar;
    }

    public GateIoApiPath setPathVar(String pathVar) {
        this.pathVar = pathVar;
        return this;
    }

    public boolean isAuth() {
        return auth;
    }

    public GateIoApiPath setAuth(boolean auth) {
        this.auth = auth;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public GateIoApiPath setMethod(String method) {
        this.method = method;
        return this;
    }
}
