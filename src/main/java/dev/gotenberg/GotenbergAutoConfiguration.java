package dev.gotenberg;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.restclient.autoconfigure.RestClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;


@AutoConfiguration(after = RestClientAutoConfiguration.class)
@ConditionalOnClass(RestClient.class)
@EnableConfigurationProperties(GotenbergProperties.class)
class GotenbergAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(GotenbergConnectionDetails.class)
    @ConditionalOnProperty("gotenberg.base-url")
    PropertiesGotenbergConnectionDetails gotenbergConnectionDetails(GotenbergProperties gotenbergProperties) {
        return new PropertiesGotenbergConnectionDetails(gotenbergProperties.baseUrl());
    }

    @Bean
    @ConditionalOnBean(GotenbergConnectionDetails.class)
    @ConditionalOnMissingBean
    GotenbergClient gotenbergClient(RestClient.Builder builder, GotenbergConnectionDetails gotenbergConnectionDetails) {
        RestClient restClient = builder.baseUrl(gotenbergConnectionDetails.baseUrl()).build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
        return factory.createClient(GotenbergClient.class);
    }
}
