package com.github.f.plan.econnoisseur.exchanges.common.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Currency
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月29日 14:05:00
 */
public enum Currency {
    ETH,
    BTC,
    BBN,
    KK,
    NANO,
    IOST,
    CET,
    CARD,
    ;

    @Override
    public String toString() {
        return this.name();
    }

    private static final Map<String, Currency> map = new HashMap<>();

    public static Currency get(String currency) {
        if (map.isEmpty()) {
            synchronized (map) {
                if (map.isEmpty()) {
                    for (Currency key : Currency.values()) {
                        map.put(key.name(), key);
                    }
                }
            }
        }

        return map.get(null != currency ? currency.toUpperCase() : null);
    }
}