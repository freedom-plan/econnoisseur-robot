package com.github.f.plan.econnoisseur.exchanges.common.dto;

import com.github.f.plan.econnoisseur.exchanges.common.model.Code;

import java.math.BigDecimal;

/**
 * MiningDifficulty
 *
 * @author Kevin Huang
 * @since version
 * 2018年07月08日 02:01:00
 */
public class MiningDifficulty extends BaseResp {
    private BigDecimal difficulty;
    private BigDecimal prediction;
    private Long updateTime;

    public MiningDifficulty(Code code) {
        super(code);
    }

    public BigDecimal getDifficulty() {
        return null == difficulty ? BigDecimal.ZERO : difficulty;
    }

    public MiningDifficulty setDifficulty(BigDecimal difficulty) {
        this.difficulty = difficulty;
        return this;
    }

    public BigDecimal getPrediction() {
        return null == prediction ? BigDecimal.ZERO : prediction;
    }

    public MiningDifficulty setPrediction(BigDecimal prediction) {
        this.prediction = prediction;
        return this;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public MiningDifficulty setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
        return this;
    }
}
