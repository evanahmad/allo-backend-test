package id.co.evan.project.aggregator.model.response;

import id.co.evan.project.aggregator.base.BaseResponse;

public record ErrorResponse(
    String message,
    String errorCode
) implements BaseResponse { }
