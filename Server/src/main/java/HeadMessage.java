import java.io.File;
import java.io.Serial;
import java.io.Serializable;

public class HeadMessage implements Serializable {
    @Serial
    private static final long serialVersionUID=1L;

    private final int size;
    private final String fileName;

    public HeadMessage(String filePath, int size) {
        File file = new File(filePath);
        this.fileName = file.getName();
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public String getFileName() {
        return fileName;
    }

}
