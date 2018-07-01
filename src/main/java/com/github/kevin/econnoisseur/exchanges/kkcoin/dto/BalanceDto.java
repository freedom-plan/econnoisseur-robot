package com.github.kevin.econnoisseur.exchanges.kkcoin.dto;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * BalanceDto
 *
 * @author jimmy
 * @desc BalanceDto
 * @date 2018/6/29
 */
public class BalanceDto {
    private String assetSymbol;
    private String bal;
    private String availableBal;
    private String frozenBal;

    public String getAssetSymbol() {
        return assetSymbol;
    }

    public void setAssetSymbol(String assetSymbol) {
        this.assetSymbol = assetSymbol;
    }

    public BigDecimal getBal() {
        if (StringUtils.isBlank(bal)) {
            return null;
        }
        return new BigDecimal(bal);
    }

    public void setBal(String bal) {
        this.bal = bal;
    }

    public BigDecimal getAvailableBal() {
        if (StringUtils.isBlank(availableBal)) {
            return null;
        }
        return new BigDecimal(availableBal);
    }

    public void setAvailableBal(String availableBal) {
        this.availableBal = availableBal;
    }

    public BigDecimal getFrozenBal() {
        if (StringUtils.isBlank(frozenBal)) {
            return null;
        }
        return new BigDecimal(frozenBal);
    }

    public void setFrozenBal(String frozenBal) {
        this.frozenBal = frozenBal;
    }

}
