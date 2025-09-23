package kr.cerney.hobby.glyphscribe.core.format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SqlCommentDefaults {
    private SqlCommentDefaults() {}

    public static List<CommentPattern> defaults() {
        List<CommentPattern> commentPatterns = new ArrayList<>();
        commentPatterns.add(new CommentPattern("/*", "*/"));
        commentPatterns.add(new CommentPattern("<!--", "-->"));
        return Collections.unmodifiableList(commentPatterns);
    }

    /**
     * null 또는 빈 리스트면 defaults() 반환, 아니면 그대로 반환
     */
    public static List<CommentPattern> orDefaults(List<CommentPattern> defaultPattern) {
        return (defaultPattern == null || defaultPattern.isEmpty()) ? defaults() : defaultPattern;
    }

    /**
     * "start|end" 문자를 코어 타입으로 파싱
     */
    public static CommentPattern parse(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        String[] parts = text.split("\\|", 2);
        if (parts.length != 2) {
            return null;
        }
        String s = parts[0].trim(), e = parts[1].trim();
        if (s.isEmpty() || e.isEmpty()) {
            return null;
        }
        return new CommentPattern(s, e);
    }
}