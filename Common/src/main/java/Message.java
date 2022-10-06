package main.java;

import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID=2L;

    private final byte[] content;

    public Message(byte[] content) {
        this.content = content;
    }

    public Message(String content) {
        this.content = content.getBytes(StandardCharsets.UTF_8);
    }

    public byte[] getContent() {
        return content;
    }
}
