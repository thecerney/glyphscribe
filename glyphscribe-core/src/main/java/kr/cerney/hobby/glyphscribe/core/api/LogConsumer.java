package kr.cerney.hobby.glyphscribe.core.api;

import kr.cerney.hobby.glyphscribe.core.model.LogContext;

/**
 * 로그 소비 전략 인터페이스
 * 로그가 수집된 후 출력/저장 등 후처리를 담당한다.
 *
 * @author 손석인
 * @since 2025.08.07
 */
@FunctionalInterface
public interface LogConsumer {
    /**
     * 로그를 수신하여 처리
     *
     * @param context 수집된 로그 컨텍스트
     */
    void consume(LogContext context);
}
