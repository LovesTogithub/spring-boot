package com.application.util;


import org.apache.commons.lang3.StringUtils;

public class CheckBlankUtil {
    /**
     * 不是空值 (null) 也不是空串
     *
     * @param input
     * @return
     */
    public static boolean hasNonNullAndNonEmptyValue(Object input) {
        return input != null && (!(input instanceof CharSequence) || StringUtils.isNotBlank((CharSequence) input));
    }
}
