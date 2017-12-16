package com.example.administrator.bk_smart_connection_android;

/**
 * Created by DOANBK on 12/11/2017.
 */

public class AnalyzedObject {
    private String name;
    private String type;
    private float frequency;

    public AnalyzedObject(String name, String type, float frequency) {
        this.name = name;
        this.type = type;
        this.frequency = frequency;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public float getFrequency() {
        return frequency;
    }
}
