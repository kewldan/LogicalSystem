package com.kewldan.render;

import com.kewldan.logical.Config;
import com.kewldan.logical.LogicalSystem;
import com.kewldan.misc.BaseScene;
import com.kewldan.misc.SaveInfo;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

import java.io.File;

public class SaveManager implements BaseScene {
    LogicalSystem game;

    @Override
    public void create(LogicalSystem game) {
        this.game = game;
        File fol = new File(game.getSchemasPath());
        if (!fol.exists())
            fol.mkdirs();
    }

    @Override
    public void render() {
        ScreenUtils.clear(Config.LIGHT);
        game.shapes.begin(ShapeRenderer.ShapeType.Filled);
        game.shapes.setColor(Config.DARK);
        for (int i = 0; i < game.cache.size(); i++) {
            game.shapes.rect(50f, Config.height - 160 - i * 150 + game.savesOffset, (Config.width - 100), 100f);
        }
        game.shapes.end();

        game.fontBatch.begin();
        int i = 0;
        for (SaveInfo si : game.cache) {
            if (si != null) {
                int y = Config.height - 100 - i * 150 + game.savesOffset;
                game.font.draw(game.fontBatch, si.toString(), 60, y);
                if (si.versionCode > Config.version) {
                    game.drawText(Config.width - 100 - game.btn.getWidth(), y + 20, game.btn.getWidth(), game.btn.getHeight(), game.packet.getString("saves.outdated"));
                }
                i++;
            }
        }
        game.fontBatch.end();

        game.batch.begin();
        i = 1;
        for (SaveInfo si : game.cache) {
            if (si != null) {
                if (si.versionCode <= Config.version) {
                    game.batch.draw(game.btn, Config.width - 75 - game.btn.getWidth(), Config.height - (i * 150) - game.btn.getHeight() / 2f + game.savesOffset + 40);
                }
                i++;
            }
        }
        game.batch.end();

        game.fontBatch.begin();
        i = 1;
        for (SaveInfo si : game.cache) {
            if (si != null) {
                if (si.versionCode <= Config.version) {
                    game.drawText(Config.width - 75 - game.btn.getWidth(), (int) (Config.height - (i * 150) - game.btn.getHeight() / 2f + game.savesOffset + 40), game.btn.getWidth(), game.btn.getHeight(),  game.packet.getString("global.play"));
                }
                i++;
            }
        }

        game.fontBatch.end();
    }

    @Override
    public void dispose() {

    }
}
