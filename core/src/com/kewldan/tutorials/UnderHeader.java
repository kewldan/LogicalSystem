package com.kewldan.tutorials;

import com.kewldan.logical.Config;
import com.kewldan.logical.LogicalSystem;

public class UnderHeader implements TutorialAsset {
    String text;
    int offset;
    int width = -1;

    public UnderHeader(String text, int offset) {
        this.text = text;
        this.offset = offset;
    }

    @Override
    public void render(LogicalSystem game, int scroll) {
        if(width == -1)
            width = (int) game.getStringWidth(text);
        game.fontBatch.begin();
        game.font.draw(game.fontBatch, text, ((Config.width/1.5f) - width) / 2f, Config.height - scroll - offset - 50);
        game.fontBatch.end();
    }

    @Override
    public int getHeight() {
        return 120;
    }

    @Override
    public int getType() {
        return TutorialAssetType.UnderHeading;
    }
}
