package com.github.f.plan.econnoisseur.dto;

import com.github.f.plan.econnoisseur.exchanges.common.dto.MiningDifficulty;
import com.github.f.plan.econnoisseur.service.ClickFarmingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * MiningInfo
 *
 * @author Kevin Huang
 * @since version
 * 2018年07月08日 10:11:00
 */
public class MiningInfo {
    private static final Logger LOGGER = LoggerFactory.getLogger(MiningInfo.class);

    private BigDecimal difficulty;
    private BigDecimal prediction;
    private Long updateTime;

    private BigDecimal rate;
    private BigDecimal amount;

    public MiningInfo(MiningDifficulty miningDifficulty, BigDecimal rate) {
        this.difficulty = miningDifficulty.getDifficulty();
        this.prediction = miningDifficulty.getPrediction();
        this.updateTime = miningDifficulty.getUpdateTime();
        this.rate = rate;
        this.amount = this.getDifficulty().subtract(this.getPrediction()).multiply(this.getRate());
    }

    public MiningInfo reset(MiningDifficulty miningDifficulty) {
        if (miningDifficulty.getUpdateTime() > this.getUpdateTime()) {
            this.difficulty = miningDifficulty.getDifficulty();
            this.prediction = miningDifficulty.getPrediction();
            this.updateTime = miningDifficulty.getUpdateTime();
            this.amount = this.getDifficulty().subtract(this.getPrediction()).multiply(this.getRate());
            LOGGER.info("已更新 Mining Info，amount: {}", this.amount);
        }
        return this;
    }

    public synchronized MiningInfo subtract(BigDecimal amount) {
        this.amount = this.amount.subtract(amount);
        return this;
    }

    public BigDecimal getDifficulty() {
        return null == difficulty ? BigDecimal.ZERO : difficulty;
    }


    public BigDecimal getPrediction() {
        return null == prediction ? BigDecimal.ZERO : prediction;
    }

    public BigDecimal getRate() {
        return null == rate ? BigDecimal.ONE : rate;
    }

    public boolean isOverrun(BigDecimal amount) {
        return BigDecimal.ZERO.compareTo(this.getDifficulty().subtract(this.getPrediction())) > 0
                || BigDecimal.ZERO.compareTo(this.getAmount().subtract(amount)) > 0;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
