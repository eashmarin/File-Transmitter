import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Message {
    private final String filePath;
    private final int size;
    private final File file;

    public Message(String filePath, int size) {
        this.filePath = filePath;
        this.size = size;
        this.file = new File(filePath);
    }

    public String getFilePath() {
        return filePath;
    }

    public int getSize() {
        return size;
    }

    public String getText() {
        String content = "";
        try {
            content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getName() + "\n" + size + "\n" + content;
    }
}
