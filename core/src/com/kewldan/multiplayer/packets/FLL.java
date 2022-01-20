package com.kewldan.multiplayer.packets;

import java.nio.ByteBuffer;

public class FLL extends SimplePacket {
    public int id, password;
    public static final byte v = 0x8;

    public FLL(ByteBuffer src) {
        id = src.getInt(1);
        password = src.getInt(5);
        type = v;
    }

    public FLL(int id, int password) {
        this.id = id;
        this.password = password;
        type = v;
    }

    @Override
    public void getBytes(ByteBuffer src) {
        src.putInt(5, id);
        src.putInt(9, password);
    }

    @Override
    public int getLength() {
        return 8;
    }
}
