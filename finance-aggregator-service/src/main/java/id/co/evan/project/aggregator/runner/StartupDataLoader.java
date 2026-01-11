package id.co.evan.project.aggregator.runner;

import id.co.evan.project.aggregator.base.UnifiedFinanceResponse;
import id.co.evan.project.aggregator.service.DataStoreService;
import id.co.evan.project.aggregator.service.strategy.IDRDataFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j2
public class StartupDataLoader implements ApplicationRunner {

    private static final int RETRY_COUNT = 3;
    private static final Duration RETRY_DELAY = Duration.ofSeconds(2);

    private final List<IDRDataFetcher> strategies;
    private final DataStoreService dataStoreService;

    @Override
    public void run(ApplicationArguments args) {
        Flux.fromIterable(strategies)
            .flatMap(strategy ->
                strategy.fetchData()
                    .retryWhen(Retry.fixedDelay(RETRY_COUNT, RETRY_DELAY))
                    .doOnError(e -> log.error("Fetching Failed {}: {}", strategy.getResourceType(), e.getMessage()))
                    .onErrorResume(e -> Mono.empty())
            )
            .doOnNext(response -> {
                dataStoreService.putData(response.resourceType(), response);
                log.info("Success Fetch Resouce: {}", response.resourceType());
            })
            .collectList()
            .doOnSuccess(success -> dataStoreService.finalizeStore())
            .block();
    }
}