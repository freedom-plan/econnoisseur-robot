package com.github.f.plan.econnoisseur.exchanges.coinex.model;

import com.github.f.plan.econnoisseur.exchanges.common.model.CurrencyPair;

/**
 * Market
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月20日 20:57:00
 */
public class Market {
    public static String valueOf(CurrencyPair pair) {
        return new StringBuilder()
                .append(pair.getBase())
                .append(pair.getCounter()).toString();
    }
}
