package main.java;

import java.io.Serial;
import java.io.Serializable;

public class SpeedMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 3L;

    private final String speedType;
    private final double speedValue;

    public SpeedMessage(String speedType, double speedValue) {
        this.speedType = speedType;
        this.speedValue = speedValue;
    }

    public String getSpeedType() {
        return speedType;
    }

    public double getSpeedValue() {
        return speedValue;
    }
}
