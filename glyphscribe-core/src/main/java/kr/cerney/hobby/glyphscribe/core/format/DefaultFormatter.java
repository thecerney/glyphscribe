package kr.cerney.hobby.glyphscribe.core.format;

import kr.cerney.hobby.glyphscribe.core.api.Formatter;
import kr.cerney.hobby.glyphscribe.core.config.FormatterConfig;
import kr.cerney.hobby.glyphscribe.core.model.LogContext;
import kr.cerney.hobby.glyphscribe.core.model.ParamEntry;
import kr.cerney.hobby.glyphscribe.core.util.CommentInjector;
import kr.cerney.hobby.glyphscribe.core.util.SqlParamBinder;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;

/**
 * FormatterConfig 설정에 따라 로그를 구성하는 기본 포맷터
 *
 * @author 손석인 (Cerney)
 * @since 2025.08.07
 */
public class DefaultFormatter implements Formatter {
    private final FormatterConfig   config;
    private final DateTimeFormatter timeFormatter;
    private       StringJoiner      sj;
    private       LogContext        ctx;

    public DefaultFormatter(FormatterConfig config) {
        this.config = config;
        this.timeFormatter = DateTimeFormatter.ofPattern(config.getTimestampFormat()).withZone(ZoneId.systemDefault());
    }

    @Override
    public String format(LogContext ctx) {
        this.sj = new StringJoiner(System.lineSeparator());
        this.ctx = ctx;

        if (config.isShowSeparator()) {
            addSeparator();
        }
        if (config.isShowSqlId()) {
            addSqlId();
        }
        if (config.isShowStartTime()) {
            addStartTime();
        }
        if (config.isShowElapsedTime()) {
            addElapsedTime();
        }
        if (config.isShowComment() && !config.isInsertCommentIntoSql()) {
            addCommentLine();
        }
        if (config.isShowRawSql()) {
            addRawSql();
        }
        if (config.isShowAssembledSql()) {
            addAssembledSql();
        }
        if (config.isShowExecutedSql()) {
            addExecutedSql();
        }
        if (config.isShowParams()) {
            addParams();
        }
        if (config.isShowSeparator()) {
            addSeparator();
        }

        return sj.toString();
    }

    private void addSeparator() {
        sj.add(config.getSeparator());
    }

    private void addSqlId() {
        sj.add("[SQL ID] " + ctx.metadata().bridgeId());
    }

    private void addStartTime() {
        sj.add("[Start Time] " + timeFormatter.format(ctx.execution().startTime()));
    }

    private void addElapsedTime() {
        sj.add("[Elapsed] " + ctx.execution().elapsedMillis() + " ms");
    }

    private void addCommentLine() {
        String comment = ctx.metadata().comment();
        if (comment != null && !comment.isBlank()) {
            sj.add(comment).add(System.lineSeparator());
        }
    }

    private void addRawSql() {
        sj.add("[Raw SQL]");
        sj.add(ctx.metadata().rawSql());
    }

    private void addAssembledSql() {
        sj.add("[Assembled SQL]");
        sj.add(ctx.assembly().assembledSql());
    }

    private void addExecutedSql() {
        String sql = ctx.execution().executedSql();

        // 1) 원본 주석을 실행 SQL에 삽입(설정이 켜져 있을 때만)
        if (config.isShowComment() && config.isInsertCommentIntoSql()) {
            String comment = ctx.metadata().comment();
            if (comment != null && !comment.isBlank()) {
                sql = CommentInjector.insertComment(sql, comment);
            }
        }

        // 2) 파라미터 바인딩: ? → 값
        sql = SqlParamBinder.bind(sql, ctx.execution().parameters());

        sj.add("[Executed SQL]");
        sj.add(sql);
    }

    private void addParams() {
        sj.add("[Parameters]");
        for (ParamEntry param : ctx.execution().parameters()) {
            sj.add(" - (" + param.type() + ") " + param.value());
        }
    }
}
