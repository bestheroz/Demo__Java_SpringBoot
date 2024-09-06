package com.github.bestheroz.standard.common.authenticate;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class CurrentUserAspect {

  public CurrentUserAspect() {
    log.info("CurrentUserAspect is initialized");
  }

  @Around(
      "execution(* com.github.bestheroz..*(.., @com.github.bestheroz.standard.common.authenticate.CurrentUser (*), ..))")
  public Object checkCurrentUser(ProceedingJoinPoint joinPoint) throws Throwable {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new AuthenticationException("Authentication is required") {};
    }
    return joinPoint.proceed();
  }
}
