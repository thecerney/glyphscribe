package kr.cerney.hobby.glyphscribe.bridge.mybatis;

import kr.cerney.hobby.glyphscribe.core.api.Formatter;
import kr.cerney.hobby.glyphscribe.core.api.IdMapper;
import kr.cerney.hobby.glyphscribe.core.archive.LogArchive;
import kr.cerney.hobby.glyphscribe.core.config.FormatterConfig;
import kr.cerney.hobby.glyphscribe.core.model.AssemblyState;
import kr.cerney.hobby.glyphscribe.core.model.ExecutionSnapshot;
import kr.cerney.hobby.glyphscribe.core.model.LogContext;
import kr.cerney.hobby.glyphscribe.core.model.Metadata;
import kr.cerney.hobby.glyphscribe.core.model.ParamEntry;
import kr.cerney.hobby.glyphscribe.core.model.ResultInfo;
import kr.cerney.hobby.glyphscribe.core.registry.SqlTemplateRegistries;
import kr.cerney.hobby.glyphscribe.core.registry.SqlTemplateRegistry;
import kr.cerney.hobby.glyphscribe.core.util.ExecutionContext;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * 실행 시점에 단 한 번 coreKey를 생성하고, PreArchive(bridge-id)를 결합하여 Archive(coreKey)에 기록.
 * MDC에도 coreKey를 넣어 로그 라인에 노출된다.
 *
 * @author 손석인
 * @since 2025.08.07
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class})
        , @Signature(type = StatementHandler.class, method = "update", args = {Statement.class})
})
public class MyBatisSqlLogInterceptor implements Interceptor {
    private static final String DELEGATE_MAPPED_STATEMENT = "delegate.mappedStatement";
    private static final Logger LOGGER                    = LoggerFactory.getLogger(MyBatisSqlLogInterceptor.class);

    private final LogArchive      archive;
    private final IdMapper        idMapper;
    private final Formatter       formatter;
    private final FormatterConfig formatterConfig;

    public MyBatisSqlLogInterceptor(LogArchive archive, IdMapper idMapper, Formatter formatter, FormatterConfig formatterConfig) {
        this.archive = archive;
        this.idMapper = idMapper;
        this.formatter = formatter;
        this.formatterConfig = formatterConfig;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler sh       = (StatementHandler) invocation.getTarget();
        BoundSql         boundSql = sh.getBoundSql();
        MappedStatement  ms       = (MappedStatement) SystemMetaObject.forObject(sh).getValue(DELEGATE_MAPPED_STATEMENT);

        String bridgeId = (ms != null ? ms.getId() : null);
        Object paramObj = boundSql.getParameterObject();

        long      startNano = System.nanoTime();
        Object    result    = null;
        Throwable error     = null;

        try {
            result = invocation.proceed();
            return result;
        } catch (Throwable t) {
            error = t;
            throw t;
        } finally {
            long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNano);

            // 실행 단 한 번 coreKey 생성 + 컨텍스트/MDC 세팅
            String coreKey = idMapper.startExecution(bridgeId);
            ExecutionContext.setCurrentKey(coreKey);

            try {
                List<ParamEntry> params = extractParams(boundSql, paramObj);

                SqlTemplateRegistry.SqlTemplate tpl    = SqlTemplateRegistries.getGlobal().get(bridgeId).orElse(null);
                String                          rawSql = tpl != null ? tpl.rawSql() : boundSql.getSql();
                String comment = tpl != null ? tpl.comment() : null;

                // 실행 SQL(바인드 포함 표시 등)은 boundSql.getSql() 그대로 저장(필요시 포맷터에서 주석 주입)
                String executedSql = boundSql.getSql();

                LogContext context = new LogContext(coreKey, new Metadata(bridgeId, rawSql, comment), new AssemblyState(rawSql), new ExecutionSnapshot(executedSql, params, Instant.now(), elapsedMillis, error), new ResultInfo(result));

                // 아카이브에 적재
                archive.put(context);

                // 자동 로그 출력 (옵션)
                if (formatterConfig != null && formatterConfig.isEnableAutoLogging()) {
                    try {
                        String logLine = formatter.format(context);
                        LOGGER.info("\n{}\n", logLine);
                    } catch (Exception fe) {
                        // 아무것도 안함.
                    }
                }
            } finally {
                // ThreadLocal & MDC 정리
                ExecutionContext.clear();
            }
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) { /* no-op */ }

    private List<ParamEntry> extractParams(BoundSql boundSql, Object parameterObject) {
        List<ParamEntry> paramEntries = new ArrayList<>();
        if (boundSql.getParameterMappings() != null && parameterObject != null) {
            MetaObject metaObject = SystemMetaObject.forObject(parameterObject);
            boundSql.getParameterMappings()
                    .forEach(mapping -> {
                        String property = mapping.getProperty();
                        Object value    = metaObject.hasGetter(property) ? metaObject.getValue(property) : null;
                        String type = (value != null ? value.getClass().getSimpleName() : "null");
                        paramEntries.add(new ParamEntry(value, type));
                    });
        }
        return paramEntries;
    }
}
