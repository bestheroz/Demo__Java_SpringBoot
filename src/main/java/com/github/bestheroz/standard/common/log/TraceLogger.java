package com.github.bestheroz.standard.common.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class TraceLogger {
  private static final String STR_START_EXECUTE_TIME = "{} START .......";
  private static final String STR_END_EXECUTE_TIME = "{} E N D [{}ms] - return: {}";
  private static final String STR_END_EXECUTE_TIME_FOR_REPOSITORY = "{} E N D [{}ms]";
  private static final String STR_END_EXECUTE_TIME_FOR_EXCEPTION = "{} THROW [{}ms]";
  private final ObjectMapper objectMapper;

  @Around(
      "execution(!private * com.github.bestheroz..*Controller.*(..)) || execution(!private * com.github.bestheroz..*Service.*(..)) "
          + "|| execution(!private * com.github.bestheroz..*Repository.*(..))")
  public Object writeLog(final ProceedingJoinPoint pjp) throws Throwable {
    final Object retVal;

    final String signature =
        StringUtils.remove(
            pjp.getStaticPart().getSignature().toString(),
            pjp.getStaticPart().getSignature().getDeclaringType().getPackageName().concat("."));
    final StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    try {
      if (!StringUtils.containsAny(signature, "HealthController", "HealthRepository")) {
        log.info(STR_START_EXECUTE_TIME, signature);
      }

      retVal = pjp.proceed();

      stopWatch.stop();
      if (StringUtils.containsAny(signature, "Repository.", "RepositoryCustom.", ".domain.")) {
        if (!StringUtils.contains(signature, "HealthRepository")) {
          log.info(STR_END_EXECUTE_TIME_FOR_REPOSITORY, signature, stopWatch.getTime());
        }
      } else {
        if (!StringUtils.contains(signature, "HealthController")) {
          final String str = objectMapper.writeValueAsString(retVal);
          log.info(
              STR_END_EXECUTE_TIME,
              signature,
              stopWatch.getTime(),
              StringUtils.abbreviate(
                  StringUtils.defaultString(str, "null"),
                  "--skip massive text-- total length : " + StringUtils.length(str),
                  1000));
        }
      }
    } catch (final Throwable e) {
      if (stopWatch.isStarted()) {
        stopWatch.stop();
      }
      log.info(STR_END_EXECUTE_TIME_FOR_EXCEPTION, signature, stopWatch.getTime());
      throw e;
    }
    return retVal;
  }
}
