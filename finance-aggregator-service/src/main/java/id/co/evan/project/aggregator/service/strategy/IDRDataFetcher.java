package id.co.evan.project.aggregator.service.strategy;

import id.co.evan.project.aggregator.base.UnifiedFinanceResponse;
import reactor.core.publisher.Mono;

public interface IDRDataFetcher {
    Mono<UnifiedFinanceResponse> fetchData();

    String getResourceType();
}