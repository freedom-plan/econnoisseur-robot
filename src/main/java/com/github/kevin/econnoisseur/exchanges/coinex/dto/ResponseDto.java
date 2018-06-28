package com.github.kevin.econnoisseur.exchanges.coinex.dto;

/**
 *
 * ResponseDto
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月21日 12:55:00
 */
public class ResponseDto<T> {
    private Integer code;
    private String message;
    private T data;

    public Integer getCode() {
        return code;
    }

    public ResponseDto setCode(Integer code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ResponseDto<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public ResponseDto<T> setData(T data) {
        this.data = data;
        return this;
    }
}
