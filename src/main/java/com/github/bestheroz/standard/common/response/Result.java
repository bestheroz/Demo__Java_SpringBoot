package com.github.bestheroz.standard.common.response;

import com.github.bestheroz.standard.common.exception.ExceptionCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Result {

  private Result() {}

  public static ResponseEntity<?> created() {
    return ResponseEntity.status(201).build();
  }

  public static <T> ResponseEntity<T> created(final T data) {
    return ResponseEntity.status(201).body(data);
  }

  public static ResponseEntity<?> ok() {
    return ResponseEntity.noContent().build();
  }

  public static <T> ResponseEntity<T> ok(final T data) {
    return ResponseEntity.ok(data);
  }

  public static ResponseEntity<ApiResult<?>> error() {
    return ResponseEntity.internalServerError()
        .body(ApiResult.of(ExceptionCode.UNKNOWN_SYSTEM_ERROR));
  }

  public static ResponseEntity<ApiResult<?>> unauthenticated() {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }
}
