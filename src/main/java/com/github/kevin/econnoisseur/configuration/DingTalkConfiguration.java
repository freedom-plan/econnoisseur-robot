package com.github.kevin.econnoisseur.configuration;

import com.github.kevin.econnoisseur.service.DingTalkService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * DingTalkConfiguration
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月28日 20:50:00
 */
@Configuration
@ConfigurationProperties(prefix = "ding.talk")
@ConditionalOnProperty(prefix = "ding.talk", name = "enabled", havingValue = "true", matchIfMissing = false)
public class DingTalkConfiguration {
    private String path;

    @Bean
    public DingTalkService dingTalkService(){
        return new DingTalkService(path);
    }

    public String getPath() {
        return path;
    }

    public DingTalkConfiguration setPath(String path) {
        this.path = path;
        return this;
    }
}
