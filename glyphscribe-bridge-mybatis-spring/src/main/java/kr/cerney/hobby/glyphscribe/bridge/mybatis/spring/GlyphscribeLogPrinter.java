package kr.cerney.hobby.glyphscribe.bridge.mybatis.spring;

import kr.cerney.hobby.glyphscribe.bridge.mybatis.enums.LogLevel;
import kr.cerney.hobby.glyphscribe.bridge.mybatis.utils.LogLevelUtils;
import kr.cerney.hobby.glyphscribe.core.api.Formatter;
import kr.cerney.hobby.glyphscribe.core.api.IdMapper;
import kr.cerney.hobby.glyphscribe.core.api.LogPrinter;
import kr.cerney.hobby.glyphscribe.core.output.GlyphScribeEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Spring Bean wrapper for {@link GlyphScribeEmitter}
 * <pre>
 * SLF4J를 통한 로그 출력용 Bean.
 * a {@link LogPrinter} bean named {@code logPrinter}
 * </pre>
 *
 * @author 손석인 (Cerney)
 * @since 2025.10.18
 */
public class GlyphscribeLogPrinter implements LogPrinter {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlyphscribeAutoConfiguration.class);

    private final GlyphScribeEmitter delegate;
    private final LogLevel           logLevel;

    public GlyphscribeLogPrinter(IdMapper idMapper, Formatter formatter, LogLevel logLevel) {
        this(GlyphScribeEmitter.getInstance(), idMapper, formatter, logLevel);
    }

    GlyphscribeLogPrinter(GlyphScribeEmitter delegate, IdMapper idMapper, Formatter formatter, LogLevel logLevel) {
        this.delegate = delegate;
        this.logLevel = (logLevel != null) ? logLevel : LogLevel.DEBUG; // 설정이 없는 경우 DEBUG
        configure(delegate, idMapper, formatter);
    }

    private static void configure(GlyphScribeEmitter emitter, IdMapper idMapper, Formatter formatter) {
        GlyphScribeEmitter.setIdMapper(idMapper);
        GlyphScribeEmitter.setFormatter(formatter);
    }

    public boolean isReady() {
        return delegate.isReady();
    }

    public void printLatest(String bridgeId) {
        String sql = delegate.printLatest(bridgeId);
        LogLevelUtils.log(LOGGER, this.logLevel, sql);
    }

    public void printByCoreKey(String coreKey) {
        String sql = delegate.printByCoreKey(coreKey);
        LogLevelUtils.log(LOGGER, this.logLevel, sql);
    }

    public void print(String bridgeId) {
        String sql = delegate.query(bridgeId);
        LogLevelUtils.log(LOGGER, this.logLevel, sql);
    }

    @Override
    public String query(String bridgeId) {
        return delegate.query(bridgeId);
    }
}
