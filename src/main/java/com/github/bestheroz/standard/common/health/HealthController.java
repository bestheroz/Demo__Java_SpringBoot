package com.github.bestheroz.standard.common.health;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/health")
@RequiredArgsConstructor
public class HealthController {

  private final HealthRepository healthRepository;

  @GetMapping("liveness")
  public String liveness() {
    return "liveness";
  }

  @GetMapping("readiness")
  public String readiness() {
    this.healthRepository.selectNow();
    return "readiness";
  }
}
