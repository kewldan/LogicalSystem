package com.kewldan.multiplayer.packets;

import com.kewldan.multiplayer.structures.TickAction;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ResponseTCK extends SimplePacket {
    public List<TickAction> actions;

    public static final byte v = 0x7;

    public ResponseTCK(ByteBuffer src) {
        actions = new ArrayList<>();
        int count = src.getInt();
        for (int i = 0; i < count; i++) {
            actions.add(new TickAction(
                    src.getInt(),
                    src.getInt(),
                    src.get() == 1
            ));
        }
        type = v;
    }

    public ResponseTCK(List<TickAction> actions) {
        this.actions = actions;
        type = v;
    }

    @Override
    public void getBytes(ByteBuffer src) {
        src.putInt(actions.size());
        for (TickAction ta : actions) {
            src.putInt(ta.x);
            src.putInt(ta.y);
            src.put((byte) (ta.value ? 1 : 0));
        }
    }

    @Override
    public int getLength() {
        return actions.size() * 9 + 4;
    }
}
