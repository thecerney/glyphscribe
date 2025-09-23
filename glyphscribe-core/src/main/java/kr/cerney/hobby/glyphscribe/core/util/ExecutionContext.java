package kr.cerney.hobby.glyphscribe.core.util;

import org.slf4j.MDC;

/**
 * 실행 범위에서 coreKey를 전달하기 위한 ThreadLocal + MDC 헬퍼.
 *
 * @author 손석인 (Cerney)
 * @since 2025.08.07
 */
public final class ExecutionContext {
    private static final ThreadLocal<String> CURRENT_KEY = new ThreadLocal<>();
    public static final String MDC_KEY = "glyphKey";

    private ExecutionContext() {}

    public static void setCurrentKey(String coreKey) {
        CURRENT_KEY.set(coreKey);
        if (coreKey != null) MDC.put(MDC_KEY, coreKey);
    }

    public static String getCurrentKey() {
        return CURRENT_KEY.get();
    }

    public static void clear() {
        CURRENT_KEY.remove();
        MDC.remove(MDC_KEY);
    }
}
