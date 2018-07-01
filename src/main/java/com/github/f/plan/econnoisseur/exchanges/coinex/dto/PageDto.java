package com.github.f.plan.econnoisseur.exchanges.coinex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 *
 * PageDto
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月28日 11:14:00
 */
public class PageDto {
    private Integer count;
    @JsonProperty("curr_page")
    private Integer currPage;
    private List<OrderDto> data;
    @JsonProperty("has_next")
    private Boolean hasNext;

    public Integer getCount() {
        return count;
    }

    public PageDto setCount(Integer count) {
        this.count = count;
        return this;
    }

    public Integer getCurrPage() {
        return currPage;
    }

    public PageDto setCurrPage(Integer currPage) {
        this.currPage = currPage;
        return this;
    }

    public List<OrderDto> getData() {
        return data;
    }

    public PageDto setData(List<OrderDto> data) {
        this.data = data;
        return this;
    }

    public Boolean getHasNext() {
        return hasNext;
    }

    public PageDto setHasNext(Boolean hasNext) {
        this.hasNext = hasNext;
        return this;
    }
}
