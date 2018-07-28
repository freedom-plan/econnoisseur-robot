package com.github.f.plan.econnoisseur.exchanges.kucoin.model;

import com.github.f.plan.econnoisseur.exchanges.common.model.CurrencyPair;

/**
 * Copyright (C), 2018
 *
 * @author jimmy
 * @desc Market
 * @date 2018/7/26
 */
public class Market {
    public static String valueOf(CurrencyPair pair) {
        return new StringBuilder()
                .append(pair.getBase())
                .append("-")
                .append(pair.getCounter()).toString();
    }
}
