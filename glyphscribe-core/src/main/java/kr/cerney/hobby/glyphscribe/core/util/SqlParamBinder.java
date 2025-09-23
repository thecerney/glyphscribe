package kr.cerney.hobby.glyphscribe.core.util;

import kr.cerney.hobby.glyphscribe.core.model.ParamEntry;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

public final class SqlParamBinder {
    private SqlParamBinder() {}

    /**
     * SQL의 ? 플레이스홀더를 params 순서대로 치환
     */
    public static String bind(String sql, List<ParamEntry> params) {
        if (sql == null || params == null || params.isEmpty()) {
            return sql;
        }

        StringBuilder        out = new StringBuilder(sql.length() + 64);
        Iterator<ParamEntry> it  = params.iterator();
        int                  i   = 0, question;
        while ((question = sql.indexOf('?', i)) >= 0 && it.hasNext()) {
            out.append(sql, i, question);
            out.append(asSqlLiteral(it.next().value()));
            i = question + 1;
        }
        out.append(sql.substring(i));
        return out.toString();
    }

    private static String asSqlLiteral(Object v) {
        if (v == null) {
            return "NULL";
        }
        if (v instanceof Number) {
            return v.toString();
        }
        if (v instanceof Boolean b) {
            return b ? "1" : "0";
        }
        if (v instanceof java.util.Date date) {
            // ISO8601로 통일; DB별 함수 사용은 포맷터 확장으로
            Instant ins = Instant.ofEpochMilli(date.getTime());
            return quote(ins.toString());
        }
        if (v instanceof Instant ins) {
            return quote(ins.toString());
        }
        if (v instanceof LocalDate ld) {
            return quote(ld.toString());
        }
        if (v instanceof LocalDateTime ldt) {
            return quote(ldt.toString());
        }
        // 그 외 문자열 취급
        return quote(v.toString());
    }

    private static String quote(String s) {
        // 단일 인용부호 이스케이프
        return "'" + s.replace("'", "''") + "'";
    }
}