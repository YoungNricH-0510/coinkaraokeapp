package com.holy.coinkaraoke.models;

public class Mention {

    private final String message;
    private final String writer;

    public Mention(String writer, String message) {
        this.message = message;
        this.writer = writer;
    }

    public String getMessage() {
        return message;
    }

    public String getWriter() {
        return writer;
    }
}
