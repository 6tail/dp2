package com.dp2.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Double数据修复
 *
 * @author 6tail
 */
public class DoubleFixUtil {
  public static String fix(double d) {
    String v = String.valueOf(d);
    if (v.contains("E")) {
      v = new BigDecimal(d).toString();
    }
    int dotIndex = v.indexOf(".");
    if (dotIndex < 0) {
      return v;
    }
    int repeatIndex = v.indexOf("0000000");
    if (repeatIndex < 0) {
      repeatIndex = v.indexOf("9999999");
    }
    String ret = v;
    if (repeatIndex > -1) {
      int scale = repeatIndex - dotIndex - 1;
      if (scale > -1) {
        ret = new BigDecimal(v).setScale(scale, RoundingMode.HALF_UP).toString();
      }
    }
    if (ret.endsWith(".0")) {
      ret = ret.substring(0, ret.lastIndexOf("."));
    }
    return ret;
  }
}
