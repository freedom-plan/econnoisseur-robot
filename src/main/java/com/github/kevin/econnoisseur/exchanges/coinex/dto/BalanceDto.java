package com.github.kevin.econnoisseur.exchanges.coinex.dto;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 *
 * BalanceDto
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月27日 10:36:00
 */
public class BalanceDto {
    private String available;
    private String frozen;

    public BigDecimal getAvailable() {
        if (StringUtils.isBlank(available)) {
            return null;
        }
        return new BigDecimal(available);
    }

    public BalanceDto setAvailable(String available) {
        this.available = available;
        return this;
    }

    public BigDecimal getFrozen() {
        if (StringUtils.isBlank(frozen)) {
            return null;
        }
        return new BigDecimal(frozen);
    }

    public BalanceDto setFrozen(String frozen) {
        this.frozen = frozen;
        return this;
    }
}
