package com.github.bestheroz.standard.common.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
      """
      execution(!private * com.github.bestheroz..*Controller.*(..)) ||
      execution(!private * com.github.bestheroz..*Service.*(..)) ||
      execution(!private * com.github.bestheroz..*Repository.*(..))
      """)
  public Object writeLog(final ProceedingJoinPoint pjp) throws Throwable {
    final Object retVal;

    final String packagePrefix =
        pjp.getStaticPart().getSignature().getDeclaringType().getPackageName() + ".";
    final String signature =
        pjp.getStaticPart().getSignature().toString().replace(packagePrefix, "");

    if (signature.contains("HealthController") || signature.contains("HealthRepository")) {
      return pjp.proceed();
    }

    final long startNanos = System.nanoTime();

    try {
      log.info(STR_START_EXECUTE_TIME, signature);

      retVal = pjp.proceed();

      final long elapsedMs = (System.nanoTime() - startNanos) / 1_000_000;
      if (signature.contains("Repository.")
          || signature.contains("RepositoryCustom.")
          || signature.contains(".domain.")
          || signature.contains("SpecificationExecutor.")) {
        log.info(STR_END_EXECUTE_TIME_FOR_REPOSITORY, signature, elapsedMs);
      } else {
        final String str = objectMapper.writeValueAsString(retVal);
        final String display = Objects.requireNonNullElse(str, "null");
        log.info(
            STR_END_EXECUTE_TIME,
            signature,
            elapsedMs,
            display.length() > 1000
                ? "--skip massive text-- total length : " + display.length()
                : display);
      }
    } catch (final Throwable e) {
      final long elapsedMs = (System.nanoTime() - startNanos) / 1_000_000;
      log.info(STR_END_EXECUTE_TIME_FOR_EXCEPTION, signature, elapsedMs);
      throw e;
    }
    return retVal;
  }
}
