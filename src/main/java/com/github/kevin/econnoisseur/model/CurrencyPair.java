package com.github.kevin.econnoisseur.model;

import static com.github.kevin.econnoisseur.model.Currency.BBN;
import static com.github.kevin.econnoisseur.model.Currency.ETH;
import static com.github.kevin.econnoisseur.model.Currency.IOST;

/**
 * CurrencyPair
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月29日 14:08:00
 */
public enum CurrencyPair {
    BBN_ETH(BBN, ETH),
    IOST_ETH(IOST, ETH)
    ;

    private Currency base;
    private Currency counter;

    CurrencyPair(Currency base, Currency counter) {
        this.base = base;
        this.counter = counter;
    }

    public Currency getBase() {
        return base;
    }

    public Currency getCounter() {
        return counter;
    }
}
