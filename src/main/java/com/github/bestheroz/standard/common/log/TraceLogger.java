package com.github.bestheroz.standard.common.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

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
      """
      execution(!private * com.github.bestheroz..*Controller.*(..)) ||
      execution(!private * com.github.bestheroz..*Service.*(..)) ||
      execution(!private * com.github.bestheroz..*Repository.*(..))
      """)
  public Object writeLog(final ProceedingJoinPoint pjp) throws Throwable {
    final Object retVal;

    final String signature =
        StringUtils.remove(
            pjp.getStaticPart().getSignature().toString(),
            pjp.getStaticPart().getSignature().getDeclaringType().getPackageName().concat("."));

    if (StringUtils.containsAny(signature, "HealthController", "HealthRepository")) {
      return pjp.proceed();
    }

    final StopWatch stopWatch = new StopWatch(signature);
    stopWatch.start();

    try {
      log.info(STR_START_EXECUTE_TIME, signature);

      retVal = pjp.proceed();

      stopWatch.stop();
      if (StringUtils.containsAny(
          signature, "Repository.", "RepositoryCustom.", ".domain.", "SpecificationExecutor.")) {
        log.info(STR_END_EXECUTE_TIME_FOR_REPOSITORY, signature, stopWatch.getTotalTimeSeconds());
      } else {
        final String str = objectMapper.writeValueAsString(retVal);
        log.info(
            STR_END_EXECUTE_TIME,
            signature,
            stopWatch.getTotalTimeSeconds(),
            StringUtils.abbreviate(
                StringUtils.defaultString(str, "null"),
                "--skip massive text-- total length : " + StringUtils.length(str),
                1000));
      }
    } catch (final Throwable e) {
      if (stopWatch.isRunning()) {
        stopWatch.stop();
      }
      log.info(STR_END_EXECUTE_TIME_FOR_EXCEPTION, signature, stopWatch.getTotalTimeSeconds());
      throw e;
    }
    return retVal;
  }
}
