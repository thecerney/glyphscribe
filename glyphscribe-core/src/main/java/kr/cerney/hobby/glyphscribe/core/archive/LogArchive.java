package kr.cerney.hobby.glyphscribe.core.archive;

import kr.cerney.hobby.glyphscribe.core.model.LogContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;

/**
 * 로그 컨텍스트를 메모리에 저장하는 저장소
 * coreId 기준으로 저장하며, 최대 N개까지만 유지
 *
 * @author 손석인 (Cerney)
 * @since 2025.08.07
 */
public class LogArchive {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogArchive.class);

    private static final int        DEFAULT_MAX_SIZE = 5000;
    private static final LogArchive INSTANCE         = new LogArchive(DEFAULT_MAX_SIZE);

    private final Map<String, LogContext> contextMap = new ConcurrentHashMap<>();
    private final Deque<String>           recentKeys = new ConcurrentLinkedDeque<>();
    private final int                     maxSize;

    // coreKey가 제거될 때 호출되는 리스너 (bridgeId로 관리되는 coreKey 상태 동기화 목적)
    private volatile Consumer<String> evictionListener;

    private LogArchive() {
        this(DEFAULT_MAX_SIZE);
    }

    public LogArchive(int maxSize) {
        this.maxSize = maxSize;
    }

    public static LogArchive getInstance() {
        return INSTANCE;
    }

    public static LogArchive getInstance(int maxSize) {
        return new LogArchive(maxSize);
    }

    public static LogArchive getInstance(LogArchive archive) {
        return new LogArchive(archive.maxSize);
    }

    /**
     * 외부에서 리스너 연결
     */
    public void setEvictionListener(Consumer<String> listener) {
        this.evictionListener = listener;
    }

    public void put(LogContext context) {
        String key = context.key();
        contextMap.put(key, context);
        recentKeys.addFirst(key);
        evictIfNeeded();
    }

    public Optional<LogContext> getByKey(String key) {
        return Optional.ofNullable(contextMap.get(key));
    }

    public List<LogContext> listRecent(int limit) {
        List<LogContext> result = new ArrayList<>();
        Iterator<String> it     = recentKeys.iterator();
        while (it.hasNext() && result.size() < limit) {
            String     k   = it.next();
            LogContext ctx = contextMap.get(k);
            if (ctx != null) {
                result.add(ctx);
            }
        }
        return result;
    }

    public boolean removeByCoreId(String key) {
        recentKeys.remove(key);
        boolean removed = contextMap.remove(key) != null;
        if (removed) {
            notifyEviction(key);
        }
        return removed;
    }

    public void clear() {
        recentKeys.forEach(this::notifyEviction);
        recentKeys.clear();
        contextMap.clear();
    }

    private void evictIfNeeded() {
        while (recentKeys.size() > maxSize) {
            String oldest = recentKeys.removeLast();
            contextMap.remove(oldest);
            notifyEviction(oldest);
        }
    }

    private void notifyEviction(String coreKey) {
        Consumer<String> listener = evictionListener;
        if (listener != null) {
            try {
                listener.accept(coreKey);
            } catch (Exception e) {
                LOGGER.warn("Eviction listener failed for key={}", coreKey, e);
            }
        }
    }
}
