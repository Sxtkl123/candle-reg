package ink.candle.candleReg.utils;

import com.google.common.base.CaseFormat;

public class StringUtil {
    public static String toSnakeCase(String str) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, str);
    }
}
