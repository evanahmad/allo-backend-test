package id.co.evan.project.aggregator.service.strategy.impl;

import id.co.evan.project.aggregator.base.UnifiedFinanceResponse;
import id.co.evan.project.aggregator.service.strategy.IDRDataFetcher;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SupportedCurrenciesStrategy implements IDRDataFetcher {
    private final WebClient webClient;

    @Override
    public Mono<UnifiedFinanceResponse> fetchData() {
        return webClient.get()
            .uri("/currencies")
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
            .map(res -> {
                var list = res.entrySet().stream()
                    .map(e -> Map.of("code", e.getKey(), "name", e.getValue()))
                    .toList();
                return new UnifiedFinanceResponse(getResourceType(), list);
            });
    }

    @Override
    public String getResourceType() {
        return "supported_currencies";
    }
}
