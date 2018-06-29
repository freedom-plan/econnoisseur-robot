package com.github.kevin.econnoisseur.dto;

import com.github.kevin.econnoisseur.model.Code;
import com.github.kevin.econnoisseur.model.Currency;

import java.util.HashMap;
import java.util.Map;

/**
 * Balances
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月29日 16:45:00
 */
public class Balances extends BaseResp {
    private Map<Currency, Balance> balances = new HashMap<>();
    public Balances(Code code) {
        super(code);
    }

    public Balance getBalance(Currency currency) {
        return this.balances.get(currency);
    }

    public Balance setBalance(Currency currency, Balance balance) {
        return this.balances.put(currency, balance);
    }
}
