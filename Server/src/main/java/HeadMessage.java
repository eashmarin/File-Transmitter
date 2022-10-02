import java.io.File;
import java.io.Serial;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class HeadMessage implements Serializable {
    @Serial
    private static final long serialVersionUID=1L;

    //private ByteBuffer buffer = ByteBuffer.wrap(new byte[4096 + 13]);
    private final int size;
    private final String fileName;

    public HeadMessage(String filePath, int size) {
        File file = new File(filePath);
        this.fileName = file.getName();
        this.size = size;
        /*buffer.position(0);
        buffer.put(file.getName().getBytes(StandardCharsets.UTF_8));

        buffer.position(4096);
        buffer.put(Integer.toString(size).getBytes(StandardCharsets.UTF_8));*/
    }

    public int getSize() {
        return size;
    }

    public String getFileName() {
        return fileName;
    }

    /*public byte[] toBytes() {
        return buffer.array();
    }*/

}
