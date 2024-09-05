package com.github.bestheroz.standard.common.util;

import lombok.experimental.UtilityClass;
import org.springframework.core.env.AbstractEnvironment;

@UtilityClass
public class EnvironmentUtils {
  public String getActivateProfile() {
    return System.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME);
  }

  public Boolean isLocal() {
    return getActivateProfile().equals("local");
  }
}
