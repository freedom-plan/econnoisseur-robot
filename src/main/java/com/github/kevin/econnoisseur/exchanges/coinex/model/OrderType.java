package com.github.kevin.econnoisseur.exchanges.coinex.model;

/**
 *
 * OrderType
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月20日 22:04:00
 */
public enum OrderType {
    ORDER_TYPE_SELL("sell"),
    ORDER_TYPE_BUY("buy");

    private String typeName;

    private OrderType(String typeName) {
        this.typeName = typeName;
    }

    public String toString() {
        return this.typeName;
    }
}
