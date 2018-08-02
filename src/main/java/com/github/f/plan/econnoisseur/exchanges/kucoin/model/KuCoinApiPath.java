package com.github.f.plan.econnoisseur.exchanges.kucoin.model;

/**
 * Copyright (C), 2018
 *
 * @author jimmy
 * @desc KuCoinApiPath
 * @date 2018/7/26
 */
public enum KuCoinApiPath {
    /**
     * 获取盘口价格
     */
    TICKER_PATH("/v1/open/tick/", null, false, "GET"),
    /**
     * 查询账户余额
     */
    BALANCE_PATH("/v1/account/balances", "/v1/account/balances", true, "GET"),
    BALANCE_SINGLE_PATH("/v1/account/balances", "/v1/account/balances", true, "GET"),
    /**
     * 查询订单详情
     */
    ORDER_STATE_PATH("/v1/order/detail", "/v1/order/detail", true, "GET"),
//    ORDERS_PATH("openorders/", "openorders", "GET"), //  查询委托状态
    /**
     * 委托交易
     */
    TRADE_PATH("/v1/order", "/v1/order", true, "POST"),
    /**
     * 取消订单
     */
    CANCEL_TRADE("/v1/order/cancel-all", "/v1/order/cancel-all", true, "POST"),// 取消委托
    ;

    private String path;
    private String endpoint;
    private boolean auth;
    private String method;

    private KuCoinApiPath(String path, String endpoint, boolean auth, String method) {
        this.path = path;
        this.endpoint = endpoint;
        this.auth = auth;
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public boolean isAuth() {
        return auth;
    }

    public String getMethod() {
        return method;
    }

}



