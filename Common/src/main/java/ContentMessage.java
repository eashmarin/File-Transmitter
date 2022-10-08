package main.java;

import java.io.Serial;
import java.io.Serializable;

public class ContentMessage implements Serializable {
    @Serial
    private static final long serialVersionUID=2L;

    private final byte[] content;

    public ContentMessage(byte[] content) {
        this.content = content;
    }

    public byte[] getContent() {
        return content;
    }
}
