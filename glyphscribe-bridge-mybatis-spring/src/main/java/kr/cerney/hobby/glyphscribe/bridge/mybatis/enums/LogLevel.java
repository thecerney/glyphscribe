package kr.cerney.hobby.glyphscribe.bridge.mybatis.enums;

import java.util.Arrays;

/**
 * LogLevel Enum은 TRACE, DEBUG, INFO, WARN, ERROR 레벨을 정의합니다.
 * 대소문자를 구분하지 않고 문자열로부터 enum 상수를 찾을 수 있는 fromString() 메소드를 제공합니다.
 */
public enum LogLevel {
    TRACE,
    DEBUG,
    INFO,
    WARN,
    ERROR;

    /**
     * 대소문자 무시 처리
     *
     * @param text 변환할 문자열 (예: "info", "WARN", "Debug")
     * @return 로그 레벨 문자열 값 대문자
     * @throws IllegalArgumentException 엄한거 넣으면 오류
     */
    public static LogLevel fromString(String text) {
        if (text != null && !text.trim().isEmpty()) {
            for (LogLevel level : LogLevel.values()) {
                if (level.name().equalsIgnoreCase(text)) {
                    return level;
                }
            }
        }

        throw new IllegalArgumentException("'" + text + "' can not found in LogLevel. Possible: " + Arrays.toString(LogLevel.values()));
    }
}
