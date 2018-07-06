package com.github.f.plan.econnoisseur.dto;

/**
 * HttpConsumer
 *
 * @author Kevin Huang
 * @since version
 * 2018年07月05日 16:44:00
 */
@FunctionalInterface
public interface HttpConsumer<T, U> {
    void accept(T t, U u) throws Exception;
}