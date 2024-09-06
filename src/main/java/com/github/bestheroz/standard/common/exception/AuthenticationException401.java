package com.github.bestheroz.standard.common.exception;

import lombok.Getter;

@Getter
public class AuthenticationException401 extends RuntimeException {
  private final Object data;
  private final ExceptionCode exceptionCode;

  public AuthenticationException401() {
    this.exceptionCode = ExceptionCode.INVALID_PARAMETER;
    this.data = null;
  }

  public AuthenticationException401(final ExceptionCode code) {
    this.exceptionCode = code;
    this.data = null;
  }

  public AuthenticationException401(final Object data) {
    this.exceptionCode = ExceptionCode.INVALID_PARAMETER;
    this.data = data;
  }

  public AuthenticationException401(final ExceptionCode code, final Object data) {
    this.exceptionCode = code;
    this.data = data;
  }
}
