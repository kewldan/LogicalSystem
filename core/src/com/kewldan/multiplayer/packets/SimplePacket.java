package com.kewldan.multiplayer.packets;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class SimplePacket {
    byte type;

    public static final Charset charset = StandardCharsets.UTF_8;

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    abstract void getBytes(ByteBuffer src);

    abstract int getLength();
}
