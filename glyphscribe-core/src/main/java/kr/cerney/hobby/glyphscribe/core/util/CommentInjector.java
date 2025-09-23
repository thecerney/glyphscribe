package kr.cerney.hobby.glyphscribe.core.util;

/**
 * SQL 내부에 주석을 삽입하는 유틸리티
 *
 * @author 손석인 (Cerney)
 * @since 2025.08.08
 */
public final class CommentInjector {
    private CommentInjector() {
    }

    /**
     * SQL 문장 내부에 주석을 삽입
     *
     * @param sql     원본 SQL
     * @param comment 삽입할 주석 (/* ~ * / 형태 포함)
     * @return 주석이 삽입된 SQL
     */
    public static String insertComment(String sql, String comment) {
        if (sql == null || comment == null || comment.isBlank()) {
            return sql;
        }

        String trimmed   = sql.stripLeading();
        int    insertPos = findFirstWhitespaceIndex(trimmed);

        if (insertPos > 0) {
            return trimmed.substring(0, insertPos) + " " + comment + System.lineSeparator() + trimmed.substring(insertPos).stripLeading();
        }

        // 삽입 위치가 없으면 fallback: 맨 앞에 삽입
        return comment + System.lineSeparator() + sql;
    }

    private static int findFirstWhitespaceIndex(String sql) {
        for (int i = 0; i < sql.length(); i++) {
            if (Character.isWhitespace(sql.charAt(i))) {
                return i;
            }
        }
        return -1;
    }
}
