package com.github.bestheroz.standard.common.exception;

import lombok.Getter;

@Getter
public class AuthorityException403 extends RuntimeException {
  private final Object data;
  private final ExceptionCode exceptionCode;

  public AuthorityException403() {
    this.exceptionCode = ExceptionCode.INVALID_PARAMETER;
    this.data = null;
  }

  public AuthorityException403(final Object data) {
    this.exceptionCode = ExceptionCode.INVALID_PARAMETER;
    this.data = data;
  }

  public AuthorityException403(final ExceptionCode code, final Object data) {
    this.exceptionCode = code;
    this.data = data;
  }
}