package com.github.kevin.econnoisseur.model;

import java.util.HashMap;
import java.util.Map;

/**
 * OrderType
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月20日 22:04:00
 */
public enum OrderType {
    LIMIT,
    MARKET;

    private static final Map<String, OrderType> map = new HashMap<>();

    public static OrderType get(String type) {
        if (map.isEmpty()) {
            synchronized (map) {
                if (map.isEmpty()) {
                    for (OrderType key : OrderType.values()) {
                        map.put(key.name(), key);
                    }
                }
            }
        }
        return map.get(null != type ? type.toUpperCase() : null);
    }
}
