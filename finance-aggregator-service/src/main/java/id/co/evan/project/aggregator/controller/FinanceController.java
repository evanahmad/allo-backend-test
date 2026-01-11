package id.co.evan.project.aggregator.controller;

import id.co.evan.project.aggregator.base.UnifiedFinanceResponse;
import id.co.evan.project.aggregator.service.DataStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/finance/data")
@RequiredArgsConstructor
public class FinanceController {
    private final DataStoreService dataStoreService;

    @GetMapping("/{resourceType}")
    public Mono<ResponseEntity<UnifiedFinanceResponse>> getFinanceData(@PathVariable String resourceType) {
        return dataStoreService.getData(resourceType)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}