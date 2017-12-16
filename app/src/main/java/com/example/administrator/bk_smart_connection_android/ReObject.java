package com.example.administrator.bk_smart_connection_android;

import java.util.List;

/**
 * Created by DOANBK on 12/11/2017.
 */

public class ReObject {
    private String document;
    private float score;

    public ReObject(String document, float score ) {
        this.document = document;
        this.score = score;

    }

    public String getDocument() {
        return document;
    }

    public float getScore() {
        return score;
    }

}
