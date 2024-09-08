package com.github.bestheroz.standard.common.exception;

import lombok.Getter;

@Getter
public class SystemException500 extends RuntimeException {
  private final Object data;
  private final ExceptionCode exceptionCode;

  public SystemException500() {
    this.exceptionCode = ExceptionCode.UNKNOWN_SYSTEM_ERROR;
    this.data = null;
  }

  public SystemException500(final ExceptionCode code) {
    this.exceptionCode = code;
    this.data = null;
  }

  public SystemException500(final Object data) {
    this.exceptionCode = ExceptionCode.UNKNOWN_SYSTEM_ERROR;
    this.data = data;
  }

  public SystemException500(final ExceptionCode code, final Object data) {
    this.exceptionCode = code;
    this.data = data;
  }
}
