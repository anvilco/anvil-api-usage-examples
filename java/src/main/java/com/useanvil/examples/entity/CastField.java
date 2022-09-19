package com.useanvil.examples.entity;

import java.io.Serializable;

public class CastField implements Serializable {
    public String id;
    public String type;
    public Rect rect;
    public int pageNum;

    public CastField(String id, String type, Rect rect, int pageNum) {
        this.id = id;
        this.type = type;
        this.rect = rect;
        this.pageNum = pageNum;
    }
}


