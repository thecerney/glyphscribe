package kr.cerney.hobby.glyphscribe.core.model;

import java.time.Instant;
import java.util.List;

/**
 * 실제 실행 시점의 SQL, 바인딩 파라미터, 시간 정보
 *
 * @author 손석인 (Cerney)
 * @since 2025.08.07
 */
public record ExecutionSnapshot(
        String executedSql,
        List<ParamEntry> parameters,
        Instant startTime,
        long elapsedMillis,
        Throwable error
) {
    public boolean hasError() { return error != null; }
}
