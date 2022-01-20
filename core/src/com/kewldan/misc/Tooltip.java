package com.kewldan.misc;

import com.kewldan.logical.LogicalSystem;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Tooltip {
    int x, y, w, h;
    String text;
    LogicalSystem game;

    public Tooltip(int x, int y, int w, int h, String text, LogicalSystem game) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.text = text;
        this.game = game;
    }

    public void drawShape() {
        if (collide())
            roundedRect(game.shapes, game.getMouseX() + 15, game.getMouseY() - 50, game.getStringWidth(text) + 10, h, Math.min(w, h) / 4f);
    }

    public void drawText() {
        if (collide())
            game.font.draw(game.fontBatch, text, game.getMouseX() + 20, game.getMouseY() - 10);
    }

    boolean collide() {
        return game.getMouseX() > x && game.getMouseX() < x + w && game.getMouseY() > y && game.getMouseY() < y + h;
    }

    static void roundedRect(ShapeRenderer shapes, float x, float y, float width, float height, float radius) {
        shapes.rect(x + radius, y + radius, width - 2f * radius, height - 2f * radius);
        shapes.rect(x + radius, y, width - 2f * radius, radius);
        shapes.rect(x + width - radius, y + radius, radius, height - 2f * radius);
        shapes.rect(x + radius, y + height - radius, width - 2f * radius, radius);
        shapes.rect(x, y + radius, radius, height - 2f * radius);
        shapes.arc(x + radius, y + radius, radius, 180f, 90f);
        shapes.arc(x + width - radius, y + radius, radius, 270f, 90f);
        shapes.arc(x + width - radius, y + height - radius, radius, 0f, 90f);
        shapes.arc(x + radius, y + height - radius, radius, 90f, 90f);
    }
}
