package dev.gotenberg;

import org.springframework.boot.autoconfigure.AutoConfiguration;
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
@EnableConfigurationProperties(GotenbergProperties.class)
@ConditionalOnClass(RestClient.class)
@ConditionalOnProperty("gotenberg.base-url")
class GotenbergAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    GotenbergClient gotenbergClient(RestClient.Builder builder, GotenbergProperties properties) {
        RestClient restClient = builder.baseUrl(properties.baseUrl()).build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
        return factory.createClient(GotenbergClient.class);
    }
}
