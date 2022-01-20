package com.kewldan.tutorials;

import com.kewldan.logical.Config;
import com.kewldan.logical.LogicalSystem;

public class Text implements TutorialAsset {
    String text;
    int offset;

    public Text(String text, int offset) {
        this.text = text;
        this.offset = offset;
    }

    @Override
    public void render(LogicalSystem game, int scroll) {
        game.fontBatch.begin();
        game.fontSmall.draw(game.fontBatch, text, 25, Config.height - scroll - offset);
        game.fontBatch.end();
    }

    @Override
    public int getHeight() {
        return 45;
    }

    @Override
    public int getType() {
        return TutorialAssetType.Text;
    }
}
