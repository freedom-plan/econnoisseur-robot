package com.github.f.plan.econnoisseur.exchanges.kkcoin.configuration;

import com.github.f.plan.econnoisseur.exchanges.kkcoin.service.KkcoinApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * KkcoinConfiguration
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月21日 09:32:00
 */
@Configuration
@ConditionalOnProperty(name = "econnoisseur.kkcoin.enabled", havingValue = "true")
@EnableConfigurationProperties(KkcoinProperties.class)
public class KkcoinConfiguration {
    @Autowired
    private KkcoinProperties kkcoinProperties;

    @Bean
    public KkcoinApi kkcoinApi() throws Exception {
        return new KkcoinApi(kkcoinProperties.getUrlPrefix(), kkcoinProperties.getApiKey(), kkcoinProperties.getPrivateKey());
    }
}
