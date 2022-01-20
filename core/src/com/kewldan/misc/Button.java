package com.kewldan.misc;

import com.kewldan.logical.LogicalSystem;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class Button {
    public int x, y, w, h;
    public String text;
    LogicalSystem game;
    IHasAction action;

    public Button(int x, int y, String text, LogicalSystem game, IHasAction a) {
        this.x = x;
        this.y = y;
        this.w = game.btn.getWidth();
        this.h = game.btn.getHeight();
        this.text = text;
        this.game = game;
        this.action = a;
    }

    public Button(int x, int y, String text, int w, int h, LogicalSystem game, IHasAction a) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.text = text;
        this.game = game;
        this.action = a;
    }

    private boolean on(int x1, int y1) {
        return x1 > x && x1 < x + w && y1 > y && y1 < y + h;
    }

    public void collide(int x1, int y1) throws Exception {
        if (on(x1, y1))
            action.callback(x1, y1);
    }

    public void drawBackground() {
        game.batch.draw(game.btn, x, y, w, h);
    }

    public void drawForeground() {
        drawText(x, y, w, h, text);
    }

    void drawText(int x, int y, int w, int h, String text) {
        GlyphLayout layout = new GlyphLayout(game.font, text);
        float fontX = x + (w - layout.width) / 2.0F;
        float fontY = y + (h + layout.height) / 2.0F;
        game.font.draw(game.fontBatch, text, fontX, fontY);
    }
}
