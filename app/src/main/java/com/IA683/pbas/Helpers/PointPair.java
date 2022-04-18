package com.IA683.pbas.Helpers;

import java.io.Serializable;

public class PointPair implements Serializable {
    private int x;
    private int y;

    public PointPair() {
        this.x = -1;
        this.y = -1;
    }

    public PointPair(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void clear() {
        x = -1;
        y = -1;
    }

    public boolean isCleared() {
        if (x == -1 && y == -1)
            return true;
        else
            return false;
    }

    @Override
    public String toString() {
        return "PointPair{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
