package kr.cerney.hobby.glyphscribe.bridge.mybatis.constants;

import kr.cerney.hobby.glyphscribe.bridge.mybatis.enums.LogLevel;
import kr.cerney.hobby.glyphscribe.core.format.CommentPattern;
import kr.cerney.hobby.glyphscribe.core.format.SqlCommentDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Objects;

/**
 * 스프링 설정 프로퍼티
 *
 * @author 손석인
 * @since 2025.08.07
 */
@ConfigurationProperties(prefix = "glyphscribe.bridge")
public class BridgeProperties {
    /**
     * 스프링이 바인딩할 대상 (없거나 빈이면 core 기본값 사용)
     */
    private List<CommentPattern> commentPatterns;
    private boolean      injectInterceptor = true;
    private LogLevel     logLevel;
    private List<String> allowedPackages   = List.of();

    private static String normalizePackage(String value) {
        String normalized = value;
        while (normalized.endsWith(".")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    public List<CommentPattern> getCommentPatterns() {
        return SqlCommentDefaults.orDefaults(commentPatterns);
    }

    public void setCommentPatterns(List<CommentPattern> commentPatterns) {
        this.commentPatterns = commentPatterns;
    }

    public boolean isInjectInterceptor() {
        return injectInterceptor;
    }

    public void setInjectInterceptor(boolean injectInterceptor) {
        this.injectInterceptor = injectInterceptor;
    }

    public List<String> getAllowedPackages() {
        return allowedPackages;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public void setAllowedPackages(List<String> allowedPackages) {
        if (allowedPackages == null || allowedPackages.isEmpty()) {
            this.allowedPackages = List.of();
            return;
        }

        this.allowedPackages = allowedPackages.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .map(BridgeProperties::normalizePackage)
                .distinct()
                .toList();
    }
}
