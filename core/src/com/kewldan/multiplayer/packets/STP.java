package com.kewldan.multiplayer.packets;

import java.nio.ByteBuffer;

public class STP extends SimplePacket {
    public int id, password;
    public static final byte v = 0x3;

    public STP(ByteBuffer src) {
        id = src.getInt();
        password = src.getInt();
        type = v;
    }

    public STP(int id, int password) {
        this.id = id;
        this.password = password;
        type = v;
    }

    @Override
    public void getBytes(ByteBuffer src) {
        src.putInt(id);
        src.putInt(password);
    }

    @Override
    public int getLength() {
        return 8;
    }
}
