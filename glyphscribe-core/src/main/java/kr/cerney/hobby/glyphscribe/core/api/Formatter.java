package kr.cerney.hobby.glyphscribe.core.api;

import kr.cerney.hobby.glyphscribe.core.model.LogContext;

/**
 * 로그 출력을 문자열로 포맷팅하는 인터페이스
 *
 * @author 손석인 (Cerney)
 * @since 2025.08.07
 */
public interface Formatter {

    /**
     * 로그 컨텍스트를 사람이 읽을 수 있는 문자열로 변환한다.
     */
    String format(LogContext context);
}
