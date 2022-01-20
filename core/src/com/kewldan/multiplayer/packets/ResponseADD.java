package com.kewldan.multiplayer.packets;


import java.nio.ByteBuffer;

public class ResponseADD extends SimplePacket {
    public int id, password;

    public static final byte v = 0x5;

    public ResponseADD(ByteBuffer src) {
        id = src.getInt(1);
        password = src.getInt(5);
        type = v;
    }

    public ResponseADD(int id, int password) {
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
