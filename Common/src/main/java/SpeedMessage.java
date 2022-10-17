package main.java;

import java.io.Serial;
import java.io.Serializable;

public class SpeedMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 3L;

    private final double instantSpeed;
    private final double sessionSpeed;

    public SpeedMessage(double instantSpeed, double sessionSpeed) {
        this.instantSpeed = instantSpeed;
        this.sessionSpeed = sessionSpeed;
    }

    public double getInstantSpeed() {
        return instantSpeed;
    }

    public double getSessionSpeed() {
        return sessionSpeed;
    }
}
