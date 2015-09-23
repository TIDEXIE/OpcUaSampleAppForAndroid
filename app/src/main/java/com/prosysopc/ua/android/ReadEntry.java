package com.prosysopc.ua.android;

import java.io.Serializable;

/**
 * Created by haripriyasaranya on 10/09/15.
 */
public class ReadEntry implements Serializable {
    private String readKey;
    private String readValue;

    public String getReadKey() {
        return readKey;
    }

    public void setReadKey(String readKey) {
        this.readKey = readKey;
    }

    public String getReadValue() {
        return readValue;
    }

    public void setReadValue(String readValue) {
        this.readValue = readValue;
    }
}
