package com.kewldan.multiplayer.packets;

import java.nio.ByteBuffer;

public class ADD extends SimplePacket {
    public String username;
    public short version;

    public static final byte v = 0x1;

    public ADD(ByteBuffer src) {
        version = src.getShort(1);
        byte len = src.get(3);
        byte[] nick = new byte[len];
        for (int i = 0; i < len; i++) {
            nick[i] = src.get(4 + i);
        }
        username = new String(nick);
        type = v;
    }

    public ADD(String username, short version) {
        this.username = username;
        this.version = version;
        type = v;
    }

    @Override
    public void getBytes(ByteBuffer src) { //Start index is 1
        src.putShort(version);
        src.put((byte) username.length());
        src.put(username.getBytes(charset));
    }

    @Override
    public int getLength() {
        return 3 + username.length();
    }
}
