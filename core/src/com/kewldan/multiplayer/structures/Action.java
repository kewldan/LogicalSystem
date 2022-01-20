package com.kewldan.multiplayer.structures;

import com.kewldan.blocks.BlockType;
import com.kewldan.blocks.Rotate;

public class Action {
    public int x, y;
    public byte type, data;

    public Action(int x, int y, byte type, byte data) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.data = data;
    }

    public static Action create(int x, int y, BlockType type, Rotate rotate) {
        int t = 0b00000001; //Write first bit
        t |= rotate.id << 2; //Write 2 bits of rotate
        return new Action(x, y, (byte) type.id, (byte) t);
    }

    public static Action remove(int x, int y) {
        return new Action(x, y, (byte) 0, (byte) 0); //Write all bits 0
    }

    public static Action rotate(int x, int y, Rotate need) {
        int data = 0b00000010; //Write first bit
        data |= need.id << 2; //Write 2 bits of rotate
        return new Action(x, y, (byte) 0, (byte) data);
    }
}