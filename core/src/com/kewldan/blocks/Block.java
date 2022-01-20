package com.kewldan.blocks;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class Block {
    public int igot;
    public boolean active;
    public BlockType blockType;
    public Rotate rotate;
    public Sprite passive, activeImg;
    public int x, y;

    public Block(BlockType blockType, Rotate rotate, int s) {
        this.blockType = blockType;
        init(s, rotate);
    }

    public Block(BlockType blockType, Rotate rotate, boolean active, int s) {
        this.active = active;
        this.blockType = blockType;
        this.rotate = rotate;
        init(s, rotate);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    private void init(int s, Rotate rotate) {
        passive = new Sprite(blockType.passive);
        activeImg = new Sprite(blockType.active);
        setRotate(rotate);
        setSize(s);
    }

    public void setRotate(Rotate nr) {
        rotate = nr;
        passive.setRotation(rotate.deg);
        activeImg.setRotation(rotate.deg);
    }

    void setSize(int s) {
        passive.setSize(s, s);
        activeImg.setSize(s, s);
        passive.setOrigin(passive.getWidth() / 2f, passive.getHeight() / 2f);
        activeImg.setOrigin(activeImg.getWidth() / 2f, activeImg.getHeight() / 2f);
    }

    @Override
    public String toString() {
        return "rotate=" + rotate + ", type=" + blockType + ", active=" + active;
    }
}
