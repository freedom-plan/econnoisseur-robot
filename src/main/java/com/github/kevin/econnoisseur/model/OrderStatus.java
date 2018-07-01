package com.github.kevin.econnoisseur.model;

/**
 * OrderStatus
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月30日 08:04:00
 */
public enum OrderStatus {
    NEW,                    // new order
    FILLED,                 // filled
    PARTIALLY_FILLED,       // partially filled
    CANCELED,               // canceled
    ;
}
