package id.co.evan.project.aggregator.model.response;

import id.co.evan.project.aggregator.base.BaseResponse;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.util.Map;

@RecordBuilder
public record ExternalRateResponse(
    Double amount,
    String base,
    String date,
    Map<String, Double> rates
) { }
