package kr.cerney.hobby.glyphscribe.bridge.mybatis.spring;

import kr.cerney.hobby.glyphscribe.core.format.CommentPattern;
import kr.cerney.hobby.glyphscribe.core.format.SqlCommentDefaults;
import org.springframework.core.convert.converter.Converter;

public class CommentPatternConverter implements Converter<String, CommentPattern> {
    @Override
    public CommentPattern convert(String source) {
        return SqlCommentDefaults.parse(source);
    }
}