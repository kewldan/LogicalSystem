package com.kewldan.multiplayer.packets;

import com.kewldan.multiplayer.structures.Action;
import com.kewldan.multiplayer.structures.Player;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ResponseUPD extends SimplePacket {
    public List<Player> players;
    public List<Action> actions;

    public static final byte v = 0x6;

    public ResponseUPD(ByteBuffer src) {
        players = new ArrayList<>();
        actions = new ArrayList<>();

        int playersCount = src.getInt(1);
        int actionsCount = src.getInt(5);
        players = new ArrayList<>();
        actions = new ArrayList<>();
        int offset = 9;
        for (int i = 0; i < playersCount; i++) {
            int x = src.getInt(offset);
            int y = src.getInt(offset + 4);
            byte length = src.get(offset + 8);
            byte[] nick = new byte[length];
            for (int i1 = 0; i1 < length; i1++) {
                nick[i1] = src.get(offset + 9 + i1);
            }
            players.add(new Player(
                    x,
                    y,
                    new String(nick)
            ));
            offset += length + 9;
        }

        for (int i = 0; i < actionsCount; i++) {
            int x = src.getInt(offset);
            int y = src.getInt(offset + 4);
            byte type = src.get(offset + 8);
            byte data = src.get(offset + 9);
            actions.add(new Action(x, y, type, data));
            offset += 10;
        }
        type = v;
    }

    public ResponseUPD(List<Player> players, List<Action> actions) {
        this.players = players;
        this.actions = actions;
        type = v;
    }

    @Override
    public void getBytes(ByteBuffer src) {
        src.putInt(5, players.size());
        src.putInt(9, actions.size());
        int offset = 13;
        for (Player p : players) {
            src.putInt(offset, p.x);
            src.putInt(offset + 4, p.y);
            src.put(offset + 8, (byte) p.nickname.length());
            offset += 9;
            byte[] bytes = p.nickname.getBytes(charset);
            for (int i = 0; i < bytes.length; i++) {
                src.put(offset, bytes[i]);
                offset++;
            }
        }

        for (Action action : actions) {
            src.putInt(offset, action.x);
            src.putInt(offset + 4, action.y);
            src.put(offset + 8, action.type);
            src.put(offset + 9, action.data);
            offset += 10;
        }
    }

    @Override
    public int getLength() {
        int pw = 0;
        for (Player p : players) {
            pw += 9 + p.nickname.length();
        }
        return 8 + pw + actions.size() * 10;
    }
}
