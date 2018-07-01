package com.github.kevin.econnoisseur.exchanges.kkcoin.model;

import com.github.kevin.econnoisseur.model.CurrencyPair;

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
                .append("_")
                .append(pair.getCounter()).toString();
    }
}
