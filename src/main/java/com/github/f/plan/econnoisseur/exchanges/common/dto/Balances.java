package com.github.f.plan.econnoisseur.exchanges.common.dto;

import com.github.f.plan.econnoisseur.exchanges.common.model.Code;
import com.github.f.plan.econnoisseur.exchanges.common.model.Currency;

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
        Balance balance = this.balances.get(currency);
        if (null == balance) {
            balance = new Balance();
        }
        return balance;
    }

    public Balance setBalance(Currency currency, Balance balance) {
        return this.balances.put(currency, balance);
    }
}
