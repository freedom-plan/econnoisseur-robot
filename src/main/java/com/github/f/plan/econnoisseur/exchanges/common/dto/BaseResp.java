package com.github.f.plan.econnoisseur.exchanges.common.dto;

import com.github.f.plan.econnoisseur.exchanges.common.model.Code;

/**
 * Base
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月29日 15:18:00
 */
public class BaseResp {
    protected Code code;

    public BaseResp(Code code) {
        this.code = code;
    }

    public Code getCode() {
        return code;
    }

    public BaseResp setCode(Code code) {
        this.code = code;
        return this;
    }
}
