package kr.cerney.hobby.glyphscribe.bridge.mybatis.spring;

import kr.cerney.hobby.glyphscribe.core.format.CommentPattern;
import kr.cerney.hobby.glyphscribe.core.format.SqlCommentDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public final class BridgeSettingsResolver {
    private static final Logger  LOGGER               = LoggerFactory.getLogger(BridgeSettingsResolver.class);
    private static final String  COMMENT_PATTERNS_KEY = "glyphscribe.bridge.comment-patterns";
    private static final Pattern LIST_DELIMITERS      = Pattern.compile("\\r?\\n|,");
    private static final Pattern PAIR_DELIMITER       = Pattern.compile("\\|");

    private BridgeSettingsResolver() {}

    /**
     * 예시 프로퍼티:
     * <pre>
     * glyphscribe.bridge.comment-patterns[0]=<!--|-->
     * glyphscribe.bridge.comment-patterns[1]=/*|*&#47;
     *  (&#47; = '/' 즉, JavaDoc 주석 종료 방지)
     * </pre>
     */
    public static List<CommentPattern> resolveCommentPatterns(Environment env) {
        List<String> specs = readCommentPatternSpecs(env);

        List<CommentPattern> parsed = specs.stream()
                .map(BridgeSettingsResolver::parseCommentPattern)
                .flatMap(Optional::stream)
                .distinct()
                .toList();

        return parsed.isEmpty()
               ? List.copyOf(SqlCommentDefaults.defaults())
               : List.copyOf(parsed);
    }

    /**
     * 인덱스 기반 목록을 우선 읽고, 값이 없으면 단일 프로퍼티 (줄바꿈/쉼표 구분)로 수집
     */
    private static List<String> readCommentPatternSpecs(Environment env) {
        List<String> specs = (env == null) ? List.of() : readIndexedProperties(env, COMMENT_PATTERNS_KEY);
        return specs.isEmpty() ? readDelimitedProperty(env, COMMENT_PATTERNS_KEY) : specs;
    }

    /**
     * key[0], key[1], ... 연속 구간 수집
     */
    private static List<String> readIndexedProperties(Environment env, String key) {
        List<String> out = new ArrayList<>();
        for (int i = 0; ; i++) {
            String value = env.getProperty(key + "[" + i + "]");
            if (value == null) {
                break;
            }
            String trimmed = value.trim();
            if (!trimmed.isEmpty()) {
                out.add(trimmed);
            }
        }
        return out;
    }

    /**
     * 단일 프로퍼티를 줄바꿈 또는 쉼표로 분리해 수집
     */
    private static List<String> readDelimitedProperty(Environment env, String key) {
        String raw = env.getProperty(key);
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        String[]     tokens = LIST_DELIMITERS.split(raw, -1);
        List<String> out    = new ArrayList<>(tokens.length);
        for (String token : tokens) {
            String value = token.trim();
            if (!value.isEmpty()) {
                out.add(value);
            }
        }
        return out;
    }

    /**
     * "start|end" 형태 파싱
     * 유효하지 않으면 empty
     */
    private static Optional<CommentPattern> parseCommentPattern(String pattern) {
        if (pattern == null) {
            return Optional.empty();
        }
        String value = pattern.trim();
        if (value.isEmpty()) {
            return Optional.empty();
        }
        String[] parts = PAIR_DELIMITER.split(value, 2);
        if (parts.length != 2) {
            LOGGER.debug("Invalid comment pattern spec: '{}'", value);
            return Optional.empty();
        }
        String start = parts[0].trim();
        String end   = parts[1].trim();
        if (start.isEmpty() || end.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new CommentPattern(start, end));
    }
}