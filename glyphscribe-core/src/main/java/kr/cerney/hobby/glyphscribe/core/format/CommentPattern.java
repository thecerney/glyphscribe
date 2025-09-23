package kr.cerney.hobby.glyphscribe.core.format;

import java.util.Objects;

public final class CommentPattern {
    private final String start;
    private final String end;

    public CommentPattern(String start, String end) {
        this.start = Objects.requireNonNull(start).trim();
        this.end = Objects.requireNonNull(end).trim();
        if (this.start.isEmpty() || this.end.isEmpty()) {
            throw new IllegalArgumentException("CommentPattern must have non-blank start/end");
        }
    }

    public String start() {
        return start;
    }

    public String end() {
        return end;
    }
}