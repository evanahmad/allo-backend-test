package id.co.evan.project.aggregator.runner;

import id.co.evan.project.aggregator.service.DataStoreService;
import id.co.evan.project.aggregator.service.strategy.IDRDataFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j2
public class StartupDataLoader implements ApplicationRunner {

    private final List<IDRDataFetcher> strategies;
    private final DataStoreService dataStoreService;

    @Override
    public void run(ApplicationArguments args) {
        Flux.fromIterable(strategies)
            .flatMap(IDRDataFetcher::fetchData)
            .doOnNext(response -> dataStoreService.putData(response.resourceType(), response))
            .collectList()
            .doOnSuccess(ignored -> dataStoreService.finalizeStore())
            .block();
    }
}