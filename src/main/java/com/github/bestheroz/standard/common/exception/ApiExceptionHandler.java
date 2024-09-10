package com.github.bestheroz.standard.common.exception;

import com.github.bestheroz.standard.common.response.ApiResult;
import com.github.bestheroz.standard.common.response.Result;
import com.github.bestheroz.standard.common.util.LogUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

  // 아래서 놓친 예외가 있을때 이곳으로 확인하기 위해 존재한다.
  // 놓친 예외는 이곳에서 확인하여 추가해주면 된다.
  @ExceptionHandler({Throwable.class})
  public ResponseEntity<ApiResult<?>> exception(final Throwable e) {
    log.error(LogUtils.getStackTrace(e));
    return Result.error();
  }

  @ExceptionHandler({NoResourceFoundException.class})
  public ResponseEntity<ApiResult<?>> noResourceFoundException(final NoResourceFoundException e) {
    log.error(LogUtils.getStackTrace(e));
    return ResponseEntity.notFound().build();
  }

  @ExceptionHandler({RequestException400.class})
  public ResponseEntity<ApiResult<?>> requestException400(final RequestException400 e) {
    log.warn(LogUtils.getStackTrace(e));
    return ResponseEntity.badRequest().body(ApiResult.of(e.getExceptionCode(), e.getData()));
  }

  @ExceptionHandler({AuthenticationException401.class})
  public ResponseEntity<ApiResult<?>> authenticationException401(
      final AuthenticationException401 e) {
    log.warn(LogUtils.getStackTrace(e));
    ResponseEntity.BodyBuilder builder = ResponseEntity.status(HttpStatus.UNAUTHORIZED);
    if (e.getExceptionCode().equals(ExceptionCode.EXPIRED_TOKEN)) {
      builder.header("token", "must-renew");
    }
    return builder.body(ApiResult.of(e.getExceptionCode(), e.getData()));
  }

  @ExceptionHandler({AuthorityException403.class})
  public ResponseEntity<ApiResult<?>> authorityException403(final AuthorityException403 e) {
    log.warn(LogUtils.getStackTrace(e));
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(ApiResult.of(e.getExceptionCode(), e.getData()));
  }

  @ExceptionHandler({AuthorizationDeniedException.class, AccessDeniedException.class})
  public ResponseEntity<ApiResult<?>> authorizationDeniedException(final AccessDeniedException e) {
    log.warn(LogUtils.getStackTrace(e));
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(ApiResult.of(ExceptionCode.UNKNOWN_AUTHORITY));
  }

  @ExceptionHandler({SystemException500.class})
  public ResponseEntity<ApiResult<?>> systemException500(final SystemException500 e) {
    log.warn(LogUtils.getStackTrace(e));
    return ResponseEntity.internalServerError()
        .body(ApiResult.of(e.getExceptionCode(), e.getData()));
  }

  @ExceptionHandler({IllegalArgumentException.class})
  public ResponseEntity<ApiResult<?>> illegalArgumentException(final IllegalArgumentException e) {
    log.warn(LogUtils.getStackTrace(e));
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
        .body(ApiResult.of(ExceptionCode.INVALID_PARAMETER));
  }

  @ExceptionHandler({UsernameNotFoundException.class})
  public ResponseEntity<ApiResult<?>> usernameNotFoundException(final UsernameNotFoundException e) {
    return Result.unauthenticated();
  }

  @ExceptionHandler({
    BindException.class,
    MethodArgumentTypeMismatchException.class,
    MissingServletRequestParameterException.class
  })
  public ResponseEntity<ApiResult<?>> bindException(final Throwable e) {
    log.warn(LogUtils.getStackTrace(e));
    return ResponseEntity.badRequest().build();
  }

  @ExceptionHandler({
    HttpMediaTypeNotAcceptableException.class,
    HttpMediaTypeNotSupportedException.class,
    HttpRequestMethodNotSupportedException.class,
    HttpClientErrorException.class
  })
  public ResponseEntity<ApiResult<?>> httpMediaTypeNotAcceptableException(
      final Throwable e, final HttpServletResponse response) {
    if (StringUtils.equals(
        response.getHeader("refreshToken"), "must")) { // 데이터 수정시 가끔 이곳으로 넘어와 버리네..
      return Result.unauthenticated();
    }
    log.warn(LogUtils.getStackTrace(e));
    return ResponseEntity.badRequest().build();
  }

  @ExceptionHandler({DuplicateKeyException.class})
  public ResponseEntity<ApiResult<?>> duplicateKeyException(final DuplicateKeyException e) {
    log.warn(LogUtils.getStackTrace(e));
    return ResponseEntity.badRequest().build();
  }
}
