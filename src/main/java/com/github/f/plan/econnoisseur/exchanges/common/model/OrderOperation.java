package com.github.f.plan.econnoisseur.exchanges.common.model;

import java.util.HashMap;
import java.util.Map;

/**
 * OrderOperation
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月29日 15:40:00
 */
public enum OrderOperation {
    SELL,
    BUY;

    private static final Map<String, OrderOperation> map = new HashMap<>();

    public static OrderOperation get(String operation) {
        if (map.isEmpty()) {
            synchronized (map) {
                if (map.isEmpty()) {
                    for (OrderOperation key : OrderOperation.values()) {
                        map.put(key.name(), key);
                    }
                }
            }
        }
        return map.get(null != operation ? operation.toUpperCase() : null);
    }
}
