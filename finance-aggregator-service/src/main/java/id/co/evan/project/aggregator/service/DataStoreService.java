package id.co.evan.project.aggregator.service;

import id.co.evan.project.aggregator.base.UnifiedFinanceResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DataStoreService {
    private final Map<String, UnifiedFinanceResponse> storage = new ConcurrentHashMap<>();
    private Map<String, UnifiedFinanceResponse> immutableStorage;

    public void putData(String resourceType, UnifiedFinanceResponse data) {
        storage.put(resourceType, data);
    }

    public void finalizeStore() {
        this.immutableStorage = Map.copyOf(storage);
    }

    public Mono<UnifiedFinanceResponse> getData(String resourceType) {
        return Mono.justOrEmpty(immutableStorage)
            .flatMap(map -> Mono.justOrEmpty(map.get(resourceType)));
    }
}