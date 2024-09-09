package com.github.bestheroz.standard.common.response;

import com.github.bestheroz.standard.common.exception.ExceptionCode;

public record ApiResult<T>(String code, String message, T data) {
  public static ApiResult<?> of(final ExceptionCode exceptionCode) {
    return ApiResult.of(exceptionCode, null);
  }

  public static <T> ApiResult<T> of(final ExceptionCode exceptionCode, final T data) {
    return new ApiResult<T>(exceptionCode.name(), exceptionCode.getMessage(), data);
  }
}
