package com.kewldan.multiplayer.packets;

import java.nio.ByteBuffer;

public class ERR extends SimplePacket {
    public String text;

    public static final byte v = 0x4;

    public ERR(ByteBuffer src) {
        byte[] b = new byte[text.length()];
        src.get(b, 2, b.length);
        text = new String(b);
        type = v;
    }

    public ERR(String text) {
        this.text = text;
        type = v;
    }

    @Override
    public void getBytes(ByteBuffer src) {
        src.put((byte) text.length());
        src.put(text.getBytes(charset));
    }

    @Override
    public int getLength() {
        return 1 + text.length();
    }
}
