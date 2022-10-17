package main.java;

import java.io.Serial;
import java.io.Serializable;

public class StatusMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 4L;

    private final String status;

    public StatusMessage(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
