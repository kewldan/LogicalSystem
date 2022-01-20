package com.kewldan.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kewldan.logical.Config;
import com.kewldan.logical.LogicalSystem;
import com.kewldan.misc.BaseScene;
import com.kewldan.misc.Button;
import com.kewldan.misc.LSStatus;
import com.kewldan.misc.Tooltip;

public class Menu implements BaseScene {
    Tooltip[] tooltips;
    LogicalSystem game;
    Texture settingsIcon, discordIcon, itchIcon, logo;

    @Override
    public void create(LogicalSystem game) {
        this.game = game;
        tooltips = new Tooltip[3];
        tooltips[0] = new Tooltip(8, 48, 48, 48, game.packet.getString("menu.settings"), game);
        tooltips[1] = new Tooltip(8, 128, 48, 55, game.packet.getString("menu.discord"), game);
        tooltips[2] = new Tooltip(8, 208, 48, 43, "Itch", game);

        logo = new Texture("Images/Other/logo.png");
        settingsIcon = new Texture("Images/Icons/settings.png");
        discordIcon = new Texture("Images/Icons/discord.png");
        itchIcon = new Texture("Images/Icons/itch.png");
        discordIcon.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        itchIcon.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    @Override
    public void render() {
        ScreenUtils.clear(Config.LIGHT);

        game.shapes.begin(ShapeRenderer.ShapeType.Filled);
        game.shapes.setColor(Config.DARK);
        game.shapes.rect(0, 0, 64, Config.height);
        if (game.ls != LSStatus.None) {
            game.shapes.setColor(Color.DARK_GRAY);
            game.shapes.rect((Config.width / 2f - 150), (Config.height / 2f - 30), 300f, 60f);
            if (System.currentTimeMillis() % 1500 < 750) {
                game.shapes.set(ShapeRenderer.ShapeType.Line);
                game.shapes.setColor(Color.WHITE);
                float x = (Config.width / 2f - 150) + 15 + game.getStringWidth(game.tempString);
                game.shapes.line(x, (Config.height / 2f - 30) + 5, x, (Config.height / 2f - 30) + 50);
            }
        }
        game.shapes.end();

        game.batch.begin();
        game.batch.draw(logo, 85, (Config.height - 120 - 30), 625, 81);
        for (Button btn1 : game.menu) {
            btn1.drawBackground();
        }
        game.batch.draw(settingsIcon, 8, 48, 48, 48);
        game.batch.draw(discordIcon, 8, 128, 48, 55);
        game.batch.draw(itchIcon, 8, 208, 48, 43);
        game.batch.end();

        game.shapes.begin(ShapeRenderer.ShapeType.Filled);
        game.shapes.setColor(Color.DARK_GRAY);
        for (Tooltip tooltip : tooltips)
            tooltip.drawShape();
        game.shapes.end();

        game.fontBatch.begin();
        for (Button btn1 : game.menu) {
            btn1.drawForeground();
        }
        if (game.ls != LSStatus.None) {
            game.font.draw(game.fontBatch, game.tempString, Config.width / 2f - 150 + 15, Config.height / 2f + 10);
            game.drawText(0, 0, Config.width, 50, game.packet.getString("menu.scheme"));
        }
        if (System.currentTimeMillis() < game.lastMsg) {
            game.font.setColor(Color.GREEN);
            game.drawText(0, Config.height - 60, Config.width, 60, game.msg);
            game.font.setColor(Color.WHITE);
        }
        for (Tooltip tooltip : tooltips)
            tooltip.drawText();
        game.fontBatch.end();
    }

    @Override
    public void dispose() {

    }
}
