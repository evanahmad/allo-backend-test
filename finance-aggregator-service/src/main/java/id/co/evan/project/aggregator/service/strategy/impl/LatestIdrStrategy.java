package id.co.evan.project.aggregator.service.strategy.impl;

import id.co.evan.project.aggregator.base.UnifiedFinanceResponse;
import id.co.evan.project.aggregator.model.response.ExternalRateResponse;
import id.co.evan.project.aggregator.service.strategy.IDRDataFetcher;
import id.co.evan.project.aggregator.util.SpreadFactorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LatestIdrStrategy implements IDRDataFetcher {

    private final WebClient webClient;
    private final SpreadFactorUtil spreadFactorUtil;

    @Value("${github.username}")
    private String githubUsername;

    @Override
    public Mono<UnifiedFinanceResponse> fetchData() {
        return webClient.get()
            .uri("/latest?base=IDR")
            .retrieve()
            .bodyToMono(ExternalRateResponse.class)
            .map(response -> {
                double spreadFactor = spreadFactorUtil.calculateSpreadFactor(githubUsername);
                double rateUsd = response.rates().get("USD");

                double buySpread = spreadFactorUtil.calculateBuySpread(rateUsd, spreadFactor);

                var detail = Map.of(
                    "currency", "USD",
                    "rate", rateUsd,
                    "USD_BuySpread_IDR", buySpread
                );

                return new UnifiedFinanceResponse(getResourceType(), List.of(detail));
            });
    }

    @Override
    public String getResourceType() {
        return "latest_idr_rates";
    }
}