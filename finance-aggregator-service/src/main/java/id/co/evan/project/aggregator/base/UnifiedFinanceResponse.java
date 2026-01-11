package id.co.evan.project.aggregator.base;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.util.List;

@RecordBuilder
public record UnifiedFinanceResponse(
    String resourceType,
    List<?> data
) implements BaseResponse { }