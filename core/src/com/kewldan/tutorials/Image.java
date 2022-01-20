package com.kewldan.tutorials;

import com.badlogic.gdx.graphics.Texture;
import com.kewldan.logical.LogicalSystem;

public class Image implements TutorialAsset {
    Texture image;
    int x, y, w, h, offset;

    public Image(String path, int x, int y, int offset) {
        image = new Texture(path);
        this.x = x;
        this.y = y;
        this.w = image.getWidth();
        this.h = image.getHeight();
        this.offset = offset;
    }

    public Image(String path, int x, int y, int w, int h, int offset) {
        image = new Texture(path);
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.offset = offset;
    }

    @Override
    public void render(LogicalSystem game, int scroll) {
        game.batch.begin();
        game.batch.draw(image, x, y - scroll, w, h);
        game.batch.end();
    }

    @Override
    public int getHeight() {
        return h;
    }

    @Override
    public int getType() {
        return TutorialAssetType.Image;
    }
}
