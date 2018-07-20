package com.github.f.plan.econnoisseur.dto;

import com.github.f.plan.econnoisseur.exchanges.common.dto.MiningDifficulty;
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

    public synchronized MiningInfo reset(MiningDifficulty miningDifficulty, BigDecimal rate) {
        if (miningDifficulty.getUpdateTime() > this.getUpdateTime()) {
            this.difficulty = miningDifficulty.getDifficulty();
            this.prediction = miningDifficulty.getPrediction();
            this.rate = rate;

            if (this.getUpdateTime() / 3600 == miningDifficulty.getUpdateTime() / 3600) {
                LOGGER.info("已更新 Mining Info，amount: {}, 更新时间：{}", this.amount, this.updateTime);
            } else {
                this.amount = this.getDifficulty().subtract(this.getPrediction()).multiply(this.getRate());
                LOGGER.info("已重置 Mining Info，amount: {}, 更新时间：{}", this.amount, this.updateTime);
            }
            this.updateTime = miningDifficulty.getUpdateTime();
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
