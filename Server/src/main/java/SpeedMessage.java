import java.io.Serial;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class SpeedMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 3L;

    private final String speedType;
    private final double speedValue;

    public SpeedMessage(byte[] content) {
        byte[] speedTypeInBytes = Arrays.copyOfRange(content, 0, 10);
        speedType = new String(speedTypeInBytes, StandardCharsets.UTF_8);
        byte[] speedValueInBytes = Arrays.copyOfRange(content, 10, content.length);
        speedValue = Double.parseDouble(new String(speedValueInBytes, StandardCharsets.UTF_8));
    }

    public String getSpeedType() {
        return speedType;
    }

    public double getSpeedValue() {
        return speedValue;
    }
}
