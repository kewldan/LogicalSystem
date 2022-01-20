package com.kewldan.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public enum BlockType {
    WIRE("wire", 0),
    TRIPLE_WIRE("trwire", 1),
    LONG_WIRE("lwire", 2),
    AND("and", 3),
    NOT("not", 4),
    ALL("all", 5),
    XOR("xor", 6),
    BTN("all", 7),
    THIRD_WIRE("llwire", 8),
    LIGHT("light", 9, 'L'),
    TIMER("timer", 10, 'P'),
    DOUBLE_WIRE("dbwire", 11, 'Q'),
    DOUBLE_WIRE_FLIP("dbwire2", 12, 'W'),
    DOUBLE_WIRE_STRAIGHT("dbwire3", 13, 'E'),
    NAND("nand", 14, 'N'),
    NXOR("nxor", 15, 'X');

    public Sprite passive, active;
    public char toSelect;
    public int id;
    static TextureAtlas atlas;

    BlockType(String name, int i) {
        fillSprites(name, i);
        this.toSelect = Character.forDigit(i + 1, 10);
        this.id = i;
    }

    BlockType(String name, int i, char sel) {
        fillSprites(name, i);
        this.toSelect = sel;
        this.id = i;
    }

    void fillSprites(String name, int i) {
        passive = getAtlas().createSprite(name);
        passive.setSize(48, 48);
        if (i < 14) {
            passive.setPosition(33 + i * 80, 33);
        } else {
            passive.setPosition(33 + (i - 14) * 80, 136);
        }
        active = getAtlas().createSprite(name + '1');
    }

    public static BlockType getID(int i) {
        i %= (values()).length;
        for (BlockType val : values()) {
            if (val.id == i)
                return val;
        }
        return null;
    }

    public static TextureAtlas getAtlas() {
        if (atlas == null) {
            atlas = new TextureAtlas(Gdx.files.internal("Images/Elements/Arrows.atlas"));
        }
        return atlas;
    }
}
