package com.github.f.plan.econnoisseur.exchanges.common.dto;

import java.math.BigDecimal;

/**
 * Balance
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月29日 15:12:00
 */
public class Balance {
    private BigDecimal total;
    private BigDecimal available;
    private BigDecimal frozen;

    public BigDecimal getTotal() {
        if (null == total) {
            total = this.getAvailable().add(this.getFrozen());
        }
        return total;
    }

    public Balance setTotal(BigDecimal total) {
        this.total = total;
        return this;
    }

    public BigDecimal getAvailable() {
        return null == available ? BigDecimal.ZERO : available;
    }

    public Balance setAvailable(BigDecimal available) {
        this.available = available;
        return this;
    }

    public BigDecimal getFrozen() {
        return null == frozen ? BigDecimal.ZERO : frozen;
    }

    public Balance setFrozen(BigDecimal frozen) {
        this.frozen = frozen;
        return this;
    }
}
