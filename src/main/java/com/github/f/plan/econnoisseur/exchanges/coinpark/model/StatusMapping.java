package com.github.f.plan.econnoisseur.exchanges.coinpark.model;

import com.github.f.plan.econnoisseur.exchanges.common.model.OrderStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (C), 2018
 *
 * @author jimmy
 * @desc StatusMapping
 * @date 2018/7/23
 */
public class StatusMapping {
    public static Map<String, OrderStatus> statusMap = new HashMap<>();

    static {
        statusMap.put("1", OrderStatus.NEW);
        statusMap.put("2", OrderStatus.PARTIALLY_FILLED);
        statusMap.put("3", OrderStatus.FILLED);
        statusMap.put("4", OrderStatus.CANCELED);
        statusMap.put("5", OrderStatus.CANCELED);
        statusMap.put("6", OrderStatus.CANCELED);
    }
}
