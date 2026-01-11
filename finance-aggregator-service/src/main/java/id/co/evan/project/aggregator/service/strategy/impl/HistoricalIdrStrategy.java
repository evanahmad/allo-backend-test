package id.co.evan.project.aggregator.service.strategy.impl;

import id.co.evan.project.aggregator.base.UnifiedFinanceResponse;
import id.co.evan.project.aggregator.config.properties.FinanceProperties;
import id.co.evan.project.aggregator.service.strategy.IDRDataFetcher;
import id.co.evan.project.aggregator.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service(Constants.HISTORICAL_IDR_USD)
@RequiredArgsConstructor
public class HistoricalIdrStrategy implements IDRDataFetcher {

    private final WebClient webClient;

    private final FinanceProperties financeProperties;

    @Override
    public Mono<UnifiedFinanceResponse> fetchData() {
        var hist = financeProperties.resources().historical();

        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .pathSegment(hist.range())
                .queryParam("from", hist.from())
                .queryParam("to", hist.to())
                .build()
            )
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .map(res -> new UnifiedFinanceResponse(getResourceType(), List.of(res)));
    }

    @Override
    public String getResourceType() {
        return Constants.HISTORICAL_IDR_USD;
    }
}