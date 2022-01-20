package com.kewldan.multiplayer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class Utils {
    public static ByteBuffer waitForBytes(InputStream is) throws IOException {
        int data1;
        byte[] lenBytes = new byte[4];
        for (int i = 0; i < 4; ) {
            data1 = is.read();
            if (data1 != -1) {
                lenBytes[i] = (byte) data1;
                i++;
            }
        }
        int len = (lenBytes[0] & 0xFF) << 24 | (lenBytes[1] & 0xFF) << 16 | (lenBytes[2] & 0xFF) << 8 | (lenBytes[3] & 0xFF);
        ByteBuffer buffer = ByteBuffer.allocate(len);
        for (int i = 0; i < len; i++) {
            data1 = is.read();
            if (data1 == -1) break;
            buffer.put((byte) data1);
        }
        return buffer;
    }
}
