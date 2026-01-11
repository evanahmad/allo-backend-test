package id.co.evan.project.aggregator.service;

import id.co.evan.project.aggregator.base.UnifiedFinanceResponseBuilder;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

class DataStoreServiceTest {

    private final DataStoreService dataStoreService = new DataStoreService();

    @Test
    void shouldStoreAndLockDataCorrectly() {
        var key = "test_resource";
        var response = UnifiedFinanceResponseBuilder.builder()
            .resourceType(key)
            .data(List.of("data"))
            .build();

        dataStoreService.putData(key, response);
        dataStoreService.finalizeStore();

        StepVerifier.create(dataStoreService.getData(key))
            .expectNext(response)
            .verifyComplete();

        StepVerifier.create(dataStoreService.getData("invalid_key"))
            .verifyComplete();
    }
}