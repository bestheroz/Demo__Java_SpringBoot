package com.github.bestheroz.standard.common.exception;

import lombok.Getter;

@Getter
public class RequestException400 extends RuntimeException {
  private final Object data;
  private final ExceptionCode exceptionCode;

  public RequestException400() {
    this.exceptionCode = ExceptionCode.INVALID_PARAMETER;
    this.data = null;
  }

  public RequestException400(final ExceptionCode code) {
    this.exceptionCode = code;
    this.data = null;
  }

  public RequestException400(final Object data) {
    this.exceptionCode = ExceptionCode.INVALID_PARAMETER;
    this.data = data;
  }

  public RequestException400(final ExceptionCode code, final Object data) {
    this.exceptionCode = code;
    this.data = data;
  }
}
