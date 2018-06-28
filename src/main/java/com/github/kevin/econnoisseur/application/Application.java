package com.github.kevin.econnoisseur.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 启动类.
 *
 * @author Kevin Huang
 * @since v0.0.1
 */
@SpringBootApplication
@ComponentScan("com.github.kevin.econnoisseur")
@EnableAutoConfiguration
public class Application {
    /**
     *  Spring Boot 启动方法.
     * @param args 输入参数
     */
    public static void main(final String[] args) {
        new SpringApplication(Application.class).run(args);
    }
}
