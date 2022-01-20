package com.kewldan.tutorials;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.kewldan.logical.Config;
import com.kewldan.logical.LogicalSystem;

public class Header implements TutorialAsset {
    String text;
    int width = -1;
    int offset;

    public Header(String text, int offset) {
        this.text = text;
        this.offset = offset;
    }

    @Override
    public void render(LogicalSystem game, int scroll) {
        if (width == -1)
            width = (int) game.getStringWidth(text);
        game.fontBatch.begin();
        game.font.draw(game.fontBatch, text, (Config.width - width) / 2f, Config.height - scroll - offset - 50);
        game.fontBatch.end();
        if (!game.shapes.isDrawing())
            game.shapes.begin(ShapeRenderer.ShapeType.Line);
        game.shapes.setColor(Color.LIGHT_GRAY);
        game.shapes.line(0, Config.height - getHeight() - scroll + 50, Config.width, Config.height - getHeight() - scroll + 50);
    }

    @Override
    public int getHeight() {
        return 160;
    }

    @Override
    public int getType() {
        return TutorialAssetType.Heading;
    }
}
