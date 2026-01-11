package id.co.evan.project.aggregator.service.strategy.impl;

import id.co.evan.project.aggregator.model.response.ExternalRateResponse;
import id.co.evan.project.aggregator.util.SpreadFactorUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LatestIdrStrategyTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private SpreadFactorUtil spreadFactorUtil;

    @InjectMocks
    private LatestIdrStrategy strategy;

    @BeforeEach
    void setUp() {
        // Inject value @Value dari application.yml secara manual untuk test
        ReflectionTestUtils.setField(strategy, "githubUsername", "evanahmad");
    }

    @Test
    void shouldFetchAndTransformDataCorrectly() {
        // 1. Mock Data Response dari Frankfurter
        ExternalRateResponse mockResponse = new ExternalRateResponse(
            1.0, "IDR", "2026-01-11", Map.of("USD", 0.000063)
        );

        // 2. Mock Fluent API WebClient (Mocking Chain)
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ExternalRateResponse.class)).thenReturn(Mono.just(mockResponse));

        // 3. Mock Util
        when(spreadFactorUtil.calculateSpreadFactor("evanahmad")).thenReturn(0.00933);
        when(spreadFactorUtil.calculateBuySpread(0.000063, 0.00933)).thenReturn(16021.11);

        // 4. Execution & Verification menggunakan StepVerifier
        StepVerifier.create(strategy.fetchData())
            .assertNext(response -> {
                assert response.resourceType().equals("latest_idr_rates");
                Map<String, Object> detail = (Map<String, Object>) response.data().get(0);
                assert detail.get("currency").equals("USD");
                assert (Double) detail.get("USD_BuySpread_IDR") == 16021.11;
            })
            .verifyComplete();
    }
}