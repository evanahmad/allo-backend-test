package id.co.evan.project.aggregator.service.strategy.impl;

import id.co.evan.project.aggregator.base.UnifiedFinanceResponse;
import id.co.evan.project.aggregator.config.properties.FinanceProperties;
import id.co.evan.project.aggregator.config.properties.GithubProperties;
import id.co.evan.project.aggregator.model.response.ExternalRateResponse;
import id.co.evan.project.aggregator.service.strategy.IDRDataFetcher;
import id.co.evan.project.aggregator.util.Constants;
import id.co.evan.project.aggregator.util.SpreadFactorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(Constants.LATEST_IDR_RATES)
@RequiredArgsConstructor
public class LatestIdrStrategy implements IDRDataFetcher {

    private final WebClient webClient;
    private final SpreadFactorUtil spreadFactorUtil;

    private final FinanceProperties financeProperties;
    private final GithubProperties githubUsername;

    @Override
    public Mono<UnifiedFinanceResponse> fetchData() {
        var base = financeProperties.resources().latest().base();
        var target = financeProperties.resources().latest().currency();

        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path(Constants.LATEST)
                .queryParam("base", base)
                .build()
            )
            .retrieve()
            .bodyToMono(ExternalRateResponse.class)
            .handle((response, sink) -> {
                var spreadFactor = spreadFactorUtil.calculateSpreadFactor(githubUsername.username());
                var rateTarget = response.rates().get(target);
                if (rateTarget == null) {
                    sink.error(new IllegalStateException("Missing rate for currency: " + target));
                    return;
                }

                var buySpread = spreadFactorUtil.calculateBuySpread(rateTarget, spreadFactor);


                Map<String, Object> detail = new HashMap<>();
                detail.put(Constants.CURRENCY, target);
                detail.put(Constants.RATE, rateTarget);
                detail.put(Constants.USD_BUYSPREAD_IDR, buySpread);

                sink.next(new UnifiedFinanceResponse(getResourceType(), List.of(detail)));
            });
    }

    @Override
    public String getResourceType() {
        return Constants.LATEST_IDR_RATES;
    }
}