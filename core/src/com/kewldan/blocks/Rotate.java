package com.kewldan.blocks;

public enum Rotate {
    UP(0, 0),
    RIGHT(270, 1),
    DOWN(180, 2),
    LEFT(90, 3);

    public int deg;
    public int id;

    Rotate(int deg, int id) {
        this.deg = deg;
        this.id = id;
    }

    public static Rotate getId(int id) {
        id %= 360;
        if (id < 0)
            id = 360 + id;
        for (Rotate val : values()) {
            if (val.deg == id)
                return val;
        }
        return null;
    }

    public static Rotate getId2(int id) {
        for (Rotate val : values()) {
            if (val.id == id)
                return val;
        }
        return null;
    }
}
