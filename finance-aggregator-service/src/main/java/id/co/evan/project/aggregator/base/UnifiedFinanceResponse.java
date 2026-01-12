package id.co.evan.project.aggregator.base;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.util.List;
import java.util.Map;

@RecordBuilder
public record UnifiedFinanceResponse(
    String resourceType,
    List<Map<String, Object>> data
) implements BaseResponse { }