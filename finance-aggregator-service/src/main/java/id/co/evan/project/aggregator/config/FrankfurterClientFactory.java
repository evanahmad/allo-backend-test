package id.co.evan.project.aggregator.config;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class FrankfurterClientFactory implements FactoryBean<WebClient> {

    @Value("${api.frankfurter.base-url}")
    private String baseUrl;

    @Override
    public WebClient getObject() {
        return WebClient.builder()
            .baseUrl(baseUrl)
            .filter((req, next) -> next.exchange(req))
            .build();
    }
    @Override
    public Class<?> getObjectType() {
        return WebClient.class;
    }
}