package com.github.bestheroz.standard.common.util;

import lombok.experimental.UtilityClass;
import org.springframework.core.env.AbstractEnvironment;

@UtilityClass
public class EnvironmentUtils {
  public String getActivateProfile() {
    String property = System.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME);
    return property == null ? "local" : property;
  }

  public Boolean isLocal() {
    return getActivateProfile().equals("local");
  }
}
