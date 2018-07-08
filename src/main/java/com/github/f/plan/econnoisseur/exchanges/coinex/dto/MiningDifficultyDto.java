package com.github.f.plan.econnoisseur.exchanges.coinex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.f.plan.econnoisseur.exchanges.common.dto.MiningDifficulty;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * MiningDifficultyDto
 *
 * @author Kevin Huang
 * @since version
 * 2018年07月08日 02:07:00
 */
public class MiningDifficultyDto {
    private String difficulty;
    private String prediction;
    @JsonProperty("update_time")
    private Long updateTime;

    public BigDecimal getDifficulty() {
        return StringUtils.isBlank(difficulty) ? BigDecimal.ZERO : new BigDecimal(difficulty);
    }

    public MiningDifficultyDto setDifficulty(String difficulty) {
        this.difficulty = difficulty;
        return this;
    }

    public BigDecimal getPrediction() {
        return StringUtils.isBlank(prediction) ? BigDecimal.ZERO : new BigDecimal(prediction);
    }

    public MiningDifficultyDto setPrediction(String prediction) {
        this.prediction = prediction;
        return this;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public MiningDifficultyDto setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
        return this;
    }
}
