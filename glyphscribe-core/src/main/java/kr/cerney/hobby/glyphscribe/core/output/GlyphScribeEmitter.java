package kr.cerney.hobby.glyphscribe.core.output;

import kr.cerney.hobby.glyphscribe.core.api.Formatter;
import kr.cerney.hobby.glyphscribe.core.api.IdMapper;
import kr.cerney.hobby.glyphscribe.core.api.LogPrinter;
import kr.cerney.hobby.glyphscribe.core.archive.LogArchive;
import kr.cerney.hobby.glyphscribe.core.config.FormatterConfig;
import kr.cerney.hobby.glyphscribe.core.format.DefaultFormatter;
import kr.cerney.hobby.glyphscribe.core.model.LogContext;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 싱글톤 기반 로그 출력기. 외부에서 bridge ID만 전달하면 로그를 문자열로 반환
 * 내부 자원은 고정되고, `IdMapper`만 외부에서 주입 가능
 *
 * @author 손석인 (Cerney)
 * @since 2025.08.07
 */
public final class GlyphScribeEmitter implements LogPrinter {
    private static final GlyphScribeEmitter INSTANCE = new GlyphScribeEmitter();

    private static final AtomicReference<IdMapper>  idMapperRef  = new AtomicReference<>();
    private static final AtomicReference<Formatter> formatterRef = new AtomicReference<>();

    private final LogArchive archive;
    private final Formatter  defaultFormatter;

    private GlyphScribeEmitter() {
        this.archive = LogArchive.getInstance();
        this.defaultFormatter = new DefaultFormatter(new FormatterConfig()); // fallback
    }

    public static GlyphScribeEmitter getInstance() {
        return INSTANCE;
    }

    public static void setIdMapper(IdMapper mapper) {
        if (mapper == null) {
            throw new IllegalArgumentException("IdMapper cannot be null");
        }
        idMapperRef.set(mapper);
    }

    /**
     * 외부(스프링)에서 주입되는 Formatter
     */
    public static void setFormatter(Formatter formatter) {
        if (formatter == null) {
            throw new IllegalArgumentException("Formatter cannot be null");
        }
        formatterRef.set(formatter);
    }

    private Formatter getEffectiveFormatter() {
        Formatter f = formatterRef.get();
        return (f != null) ? f : defaultFormatter;
    }

    public boolean isReady() {
        return idMapperRef.get() != null;
    }

    /**
     * coreKey로 직접 출력
     */
    public String printByCoreKey(String coreKey) {
        LogContext context = archive.getByKey(coreKey).orElse(null);
        if (context == null) {
            return "[GlyphScribeLogger] No log found for coreKey: " + coreKey;
        }
        try {
            return getEffectiveFormatter().format(context);
        } catch (Exception e) {
            return "[GlyphScribeLogger] Failed to format log for key: " + coreKey;
        }
    }

    /**
     * bridge-id 또는 short-id의 최근 실행 출력
     */
    public String printLatest(String bridgeId) {
        IdMapper idMapper = idMapperRef.get();
        if (idMapper == null) {
            return "[GlyphScribeLogger] IdMapper is not initialized. Call setIdMapper(...) first.";
        }
        if (bridgeId == null || bridgeId.isBlank()) {
            return "[GlyphScribeLogger] Invalid bridgeId: null or blank";
        }
        String key = idMapper.getLatestKey(bridgeId).orElse(null); // 숏ID/풀ID 모두 허용한다면 getLatestKey 사용
        if (key == null) {
            return "[GlyphScribeLogger] No recent execution for bridgeId: " + bridgeId;
        }
        return printByCoreKey(key);
    }

    @Override
    public String print(String bridgeId) {
        return System.lineSeparator() + printLatest(bridgeId) + System.lineSeparator();
    }
}
