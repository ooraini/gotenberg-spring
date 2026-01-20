package dev.gotenberg;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class GotenbergContainerTest {

    @Container
    public static final GenericContainer<?> GOTENBERG = new GenericContainer<>("gotenberg/gotenberg:8")
            .withExposedPorts(3000)
            .withReuse(true);

    protected static GotenbergClient gotenbergClient;

    @BeforeAll
    public static void setUpClient() {
        String baseUrl = String.format("http://%s:%d", 
            GOTENBERG.getHost(), 
            GOTENBERG.getFirstMappedPort());
        
        RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
        
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();
        
        gotenbergClient = factory.createClient(GotenbergClient.class);
    }
}
