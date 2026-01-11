package id.co.evan.project.aggregator.service.strategy.impl;

import id.co.evan.project.aggregator.base.UnifiedFinanceResponse;
import id.co.evan.project.aggregator.service.strategy.IDRDataFetcher;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HistoricalIdrStrategy implements IDRDataFetcher {

    private final WebClient webClient;

    @Override
    public Mono<UnifiedFinanceResponse> fetchData() {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .pathSegment("2024-01-04..2024-01-05")
                .queryParam("from", "IDR")
                .queryParam("to", "USD")
                .build()
            )
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .map(res -> new UnifiedFinanceResponse(getResourceType(), List.of(res)));
    }

    @Override
    public String getResourceType() {
        return "historical_idr_usd";
    }
}