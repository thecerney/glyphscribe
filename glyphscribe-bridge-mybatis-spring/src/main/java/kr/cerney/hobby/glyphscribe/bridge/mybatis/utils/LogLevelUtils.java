package kr.cerney.hobby.glyphscribe.bridge.mybatis.utils;

import kr.cerney.hobby.glyphscribe.bridge.mybatis.enums.LogLevel;
import org.slf4j.Logger;

/**
 * LogLevel enum에 따라 SLF4J 로거를 호출하는 유틸리티 클래스
 *
 * @author 손석인 (Cerney)
 * @since 2025.10.20
 */
public final class LogLevelUtils {
    private LogLevelUtils() {
        // Noop.
    }

    /**
     * 지정된 LogLevel 로 포맷을 활용하여 출력
     *
     * @param logger  사용할 SLF4J Logger 객체
     * @param level   적용할 LogLevel
     * @param format  로깅할 메시지 포맷 (예: "\n{}\n")
     * @param arg     메시지 포맷에 들어갈 인자
     */
    public static void log(Logger logger, LogLevel level, String format, Object arg) {
        LogLevel effectiveLevel = (level != null) ? level : LogLevel.DEBUG;

        switch (effectiveLevel) {
            case TRACE:
                if (logger.isTraceEnabled()) {
                    logger.trace(format, arg);
                }
                break;
            case INFO:
                if (logger.isInfoEnabled()) {
                    logger.info(format, arg);
                }
                break;
            case WARN:
                if (logger.isWarnEnabled()) {
                    logger.warn(format, arg);
                }
                break;
            case ERROR:
                if (logger.isErrorEnabled()) {
                    logger.error(format, arg);
                }
                break;
            case DEBUG:
            default:
                if (logger.isDebugEnabled()) {
                    logger.debug(format, arg);
                }
                break;
        }
    }

    /**
     * 지정된 LogLevel로 출력 (노 포맷)
     *
     * @param logger  사용할 SLF4J Logger 객체
     * @param level   적용할 LogLevel
     * @param message 로깅할 메시지
     */
    public static void log(Logger logger, LogLevel level, String message) {
        LogLevel effectiveLevel = (level != null) ? level : LogLevel.DEBUG;

        switch (effectiveLevel) {
            case TRACE:
                logger.trace(message);
                break;
            case INFO:
                logger.info(message);
                break;
            case WARN:
                logger.warn(message);
                break;
            case ERROR:
                logger.error(message);
                break;
            case DEBUG:
            default:
                logger.debug(message);
                break;
        }
    }
}
