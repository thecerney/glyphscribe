package kr.cerney.hobby.glyphscribe.core.consumer;

import kr.cerney.hobby.glyphscribe.core.api.LogConsumer;
import kr.cerney.hobby.glyphscribe.core.api.Formatter;
import kr.cerney.hobby.glyphscribe.core.model.LogContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SLF4J Logger 기반 로그 소비자
 *
 * @author 손석인
 * @since 2025.08.07
 */
public class Slf4jLogConsumer implements LogConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(Slf4jLogConsumer.class);

    private final Formatter formatter;

    public Slf4jLogConsumer(Formatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public void consume(LogContext context) {
        LOGGER.info(formatter.format(context));
    }
}
