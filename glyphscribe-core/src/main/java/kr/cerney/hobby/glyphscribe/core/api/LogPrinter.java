package kr.cerney.hobby.glyphscribe.core.api;

/**
 * bridgeId를 기반으로 로그를 조회하여 문자열로 출력하는 인터페이스
 * 실패 시 예외를 던지지 않고 사용자 메시지를 반환한다.
 *
 * @author 손석인 (Cerney)
 * @since 2025.08.07
 */
public interface LogPrinter {

    /**
     * 로그를 출력 문자열로 반환한다. 실패 시 메시지를 반환한다.
     */
    String print(String bridgeId);
}