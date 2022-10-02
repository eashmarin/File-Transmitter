import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

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
