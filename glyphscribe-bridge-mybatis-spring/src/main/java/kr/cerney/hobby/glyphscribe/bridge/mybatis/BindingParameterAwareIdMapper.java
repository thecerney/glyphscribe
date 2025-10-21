package kr.cerney.hobby.glyphscribe.bridge.mybatis;

import kr.cerney.hobby.glyphscribe.core.api.IdMapper;
import kr.cerney.hobby.glyphscribe.core.util.LogKeyGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * 실행마다 coreKey를 생성하고, bridge-id 별로 최근 실행 목록을 유지한다.
 * <pre>
 * 역매핑(coreKey->bridgeId)도 함께 유지하여 정리와 추적에 사용
 * </pre>
 *
 * @author 손석인 (Cerney)
 * @since 2025.08.07
 */
public class BindingParameterAwareIdMapper implements IdMapper {
    private final static int MAX_KEYS_PER_BRIDGE = 100;
    // full SQL ID -> 최근 coreKey들
    private final Map<String, Deque<String>> bridgeToCore = new ConcurrentHashMap<>();
    // coreKey -> full SQL ID
    private final Map<String, String>        coreToBridge = new ConcurrentHashMap<>();
    // coreKey -> 생성 시각(최근 비교용)
    private final Map<String, Long>          keyTime      = new ConcurrentHashMap<>();
    private final ShortIdIndex shortIndex;

    public BindingParameterAwareIdMapper(ShortIdIndex shortIndex) {
        this.shortIndex = shortIndex;
    }

    @Override
    public String startExecution(String bridgeId) {
        String        coreKey = LogKeyGenerator.generate();
        Deque<String> dq      = bridgeToCore.computeIfAbsent(bridgeId, k -> new ConcurrentLinkedDeque<>());
        dq.addFirst(coreKey);
        coreToBridge.put(coreKey, bridgeId);
        keyTime.put(coreKey, System.nanoTime());

        // bridge당 보존 개수 제한
        while (dq.size() > MAX_KEYS_PER_BRIDGE) {
            String evicted = dq.removeLast();
            coreToBridge.remove(evicted);
            keyTime.remove(evicted);
        }

        return coreKey;
    }

    /**
     * 풀/숏 id 모두 허용 (가장 최근 실행 coreKey)
     */
    @Override
    public Optional<String> getLatestKey(String anyId) {
        Set<String> candidates = shortIndex.resolve(anyId);
        if (candidates.isEmpty()) {
            Deque<String> dq = bridgeToCore.get(anyId); // full id 가정
            return (dq == null || dq.isEmpty()) ? Optional.empty() : Optional.of(dq.getFirst());
        }
        String best   = null;
        long   bestTs = Long.MIN_VALUE;
        for (String fullId : candidates) {
            Deque<String> dq = bridgeToCore.get(fullId);
            if (dq == null || dq.isEmpty()) {
                continue;
            }
            String k  = dq.getFirst();
            long   ts = keyTime.getOrDefault(k, Long.MIN_VALUE);
            if (ts > bestTs) {
                bestTs = ts;
                best = k;
            }
        }
        return Optional.ofNullable(best);
    }

    @Override
    public Optional<String> resolveBridgeId(String coreKey) {
        return Optional.ofNullable(coreToBridge.get(coreKey));
    }

    @Override
    public Optional<String> peekLatestKey(String bridgeId) {
        Deque<String> dq = bridgeToCore.get(bridgeId);
        return (dq == null || dq.isEmpty()) ? Optional.empty() : Optional.of(dq.getFirst());
    }

    @Override
    public List<String> listKeys(String bridgeId) {
        Deque<String> dq = bridgeToCore.get(bridgeId);
        return (dq == null) ? Collections.emptyList() : new ArrayList<>(dq);
    }

    public Set<String> getAllBridgeIds() {
        return bridgeToCore.keySet();
    }

    public int totalKeyCount() {
        return bridgeToCore.values()
                .stream()
                .mapToInt(Deque::size)
                .sum();
    }

    @Override
    public void evictByCoreKey(String coreKey) {
        String bridgeId = coreToBridge.remove(coreKey);
        keyTime.remove(coreKey);
        if (bridgeId == null) {
            return;
        }

        Deque<String> dq = bridgeToCore.get(bridgeId);
        if (dq != null) {
            dq.remove(coreKey); // O(n)지만 최근 키 중심이라 보통 작음 - 최대치도 작아서 속도 문제 x
            if (dq.isEmpty()) {
                bridgeToCore.remove(bridgeId);
            }
        }
    }

    @Override
    public void evictByBridgeId(String bridgeId) {
        Deque<String> dq = bridgeToCore.remove(bridgeId);
        if (dq != null) {
            for (String k : dq) {
                coreToBridge.remove(k);
                keyTime.remove(k);
            }
        }
    }

    public void clear() {
        bridgeToCore.clear();
        coreToBridge.clear();
        keyTime.clear();
    }
}
