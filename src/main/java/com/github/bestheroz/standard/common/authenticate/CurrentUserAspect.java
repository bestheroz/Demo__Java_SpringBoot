package com.github.bestheroz.standard.common.authenticate;

import com.github.bestheroz.standard.common.exception.AuthenticationException401;
import com.github.bestheroz.standard.common.exception.ExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class CurrentUserAspect {

  @Around(
      "execution(* com.github.bestheroz..*(.., @com.github.bestheroz.standard.common.authenticate.CurrentUser (*), ..))")
  public Object checkCurrentUser(ProceedingJoinPoint joinPoint) throws Throwable {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!authentication.isAuthenticated() || authentication.getPrincipal() == null) {
      throw new AuthenticationException401(ExceptionCode.EXPIRED_TOKEN);
    }
    return joinPoint.proceed();
  }
}
