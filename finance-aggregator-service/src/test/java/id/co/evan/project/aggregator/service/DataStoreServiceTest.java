package id.co.evan.project.aggregator.service;

import id.co.evan.project.aggregator.base.UnifiedFinanceResponseBuilder;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        assertTrue(dataStoreService.isReady());

        StepVerifier.create(dataStoreService.getData(key))
            .expectNext(response)
            .verifyComplete();

        StepVerifier.create(dataStoreService.getData("invalid_key"))
            .verifyComplete();
    }

    @Test
    void shouldRejectWriteAfterSealed_andIgnoreDoubleFinalize() {
        var response = UnifiedFinanceResponseBuilder.builder()
            .resourceType("key")
            .data(List.of("data"))
            .build();

        dataStoreService.finalizeStore();

        assertThrows(IllegalStateException.class,
            () -> dataStoreService.putData("key", response)
        );

        assertDoesNotThrow(dataStoreService::finalizeStore);
    }

}