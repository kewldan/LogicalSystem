package com.kewldan.multiplayer.structures;

import java.util.ArrayList;

public class Player {
    public int x, y, id, password, version;
    public String nickname;
    public Thread thread;
    public ArrayList<Action> actions;

    public Player(int id, int password, int version, String nickname) {
        this.id = id;
        this.password = password;
        this.version = version;
        this.nickname = nickname;
        actions = new ArrayList<>();
    }

    public Player(int x, int y, String nickname) {
        this.x = x;
        this.y = y;
        this.nickname = nickname;
        actions = new ArrayList<>();
    }
}
