package kr.cerney.hobby.glyphscribe.core.api;

import java.util.List;
import java.util.Optional;

/**
 * bridge-id(외부 식별자)와 coreKey(실행별 주키) 매핑자.
 *
 * @author 손석인 (Cerney)
 * @since 2025.08.07
 */
public interface IdMapper {
    /**
     * 실행 시작 시 호출
     * 주어진 bridgeId에 대한 새로운 coreKey를 생성하고 내부에 기록한다.
     */
    String startExecution(String bridgeId);

    /**
     * 가장 최근 실행 coreKey 조회
     * - id가 풀ID 인지 / 숏ID 인지 구분하여 처리 (숏 ID면 후보 fullId 중 가장 최근 실행 선택)
     */
    Optional<String> getLatestKey(String id);

    /**
     * full bridgeId 전제의 최근 실행 coreKey 조회
     */
    default Optional<String> peekLatestKey(String bridgeId) {
        return getLatestKey(bridgeId);
    }

    /**
     * coreKey -> 외부 시스템 ID(bridgeId) 역변환
     */
    default Optional<String> resolveBridgeId(String coreKey) {
        return Optional.empty();
    }

    /**
     * 해당 bridgeId의 모든 coreKey 목록 (최근 우선) 조회
     */
    default List<String> listKeys(String bridgeId) {
        return List.of();
    }

    /**
     * 정리용 (롤링 동기화)
     */
    default void evictByCoreKey(String coreKey) {}

    default void evictByBridgeId(String bridgeId) {}
}
