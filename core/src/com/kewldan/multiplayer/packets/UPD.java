package com.kewldan.multiplayer.packets;

import com.kewldan.multiplayer.structures.Action;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class UPD extends SimplePacket {
    public int x, y, id, password;
    public List<Action> actions;

    public static final byte v = 0x2;

    public UPD(ByteBuffer src) {
        x = src.getInt(1);
        y = src.getInt(5);
        id = src.getInt(9);
        password = src.getInt(13);
        int actionsCount = src.getInt(17);
        actions = new ArrayList<>();
        int offset = 21;
        for (int i = 0; i < actionsCount; i++) {
            actions.add(new Action(
                    src.getInt(offset),
                    src.getInt(offset + 4),
                    src.get(offset + 8),
                    src.get(offset + 9)
            ));
            offset += 10;
        }
        type = v;
    }

    public UPD(int x, int y, int id, int password, List<Action> actions) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.password = password;
        this.actions = actions;
        type = v;
    }

    @Override
    public void getBytes(ByteBuffer src) {
        src.putInt(5, x);
        src.putInt(9, y);
        src.putInt(13, id);
        src.putInt(17, password);
        src.putInt(21, actions.size());
        int offset = 25;
        for (Action a : actions) {
            src.putInt(offset, a.x);
            src.putInt(offset + 4, a.y);
            src.put(offset + 8, a.type);
            src.put(offset + 9, a.data);
            offset += 10;
        }
    }

    @Override
    public int getLength() {
        return 20 + actions.size() * 10;
    }
}
