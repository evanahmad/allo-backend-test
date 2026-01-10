package id.co.evan.project.aggregator.model.response;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.util.Map;

@RecordBuilder
public record CurrencyNameResponse(
    Map<String, String> currencies
) { }
