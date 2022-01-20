package com.kewldan.render;

import com.kewldan.logical.Config;
import com.kewldan.logical.LogicalSystem;
import com.kewldan.misc.BaseScene;
import com.kewldan.misc.Button;
import com.badlogic.gdx.utils.ScreenUtils;

public class Options implements BaseScene {
    LogicalSystem game;

    @Override
    public void create(LogicalSystem game) {
        this.game = game;
    }

    @Override
    public void render() {
        ScreenUtils.clear(Config.DARK);

        game.batch.begin();
        for (Button btn1 : game.settings)
            btn1.drawBackground();
        game.batch.end();

        game.fontBatch.begin();
        for (Button btn1 : game.settings)
            btn1.drawForeground();
        if (Config.lang != Config.langFirst) {
            game.font.draw(game.fontBatch, game.packet.getString("settings.restart"), game.settings[2].x, game.settings[2].y - 150);
        }

        game.drawText(100 + game.btn.getWidth(), Config.height - 50 - game.btn.getHeight(), 280, 60, "TPS: " + Config.tps);
        String time = "";
        if (Config.l >= 60)
            time += (int) Math.floor((Config.l / 60f)) + " " + game.packet.getString("settings.hours") + " ";
        if (Config.l % 60 > 0)
            time += (Config.l % 60) + " " + game.packet.getString("settings.mins");
        game.drawText(0, 0, Config.width, 75, game.packet.getString("settings.time") + ": " + time);
        game.drawText(0, 0, Config.width, 200, game.packet.getString("pages.multiplayer") + ": " + game.packet.getString(Config.coopEnable ? "global.enable" : "global.disable"));

        game.font.draw(game.fontBatch, game.packet.getString("global.version") + ": " + game.getVersion(Config.version) + " " + Config.versionCode, 10, 50);
        game.font.draw(game.fontBatch, "Author is kewldan", Config.width - 300, 50);
        game.fontBatch.end();
    }

    @Override
    public void dispose() {

    }
}
