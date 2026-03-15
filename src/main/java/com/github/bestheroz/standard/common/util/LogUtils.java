package com.github.bestheroz.standard.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LogUtils {
  public String getStackTrace(final Throwable e) {
    StringWriter sw = new StringWriter();
    e.printStackTrace(new PrintWriter(sw));
    return Arrays.stream(sw.toString().split("\\R"))
        .filter(item -> item.startsWith("\tat com.github.bestheroz") || !item.startsWith("\tat"))
        .collect(Collectors.joining("\n"));
  }
}
