package com.useanvil.examples.entity.request;

import java.io.Serializable;

public class Rect implements Serializable {
    public int x;
    public int y;
    public int height;
    public int width;

    public Rect(int x, int y, int height, int width) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
    }
}
