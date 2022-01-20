package com.kewldan.render;

import com.kewldan.blocks.Block;
import com.kewldan.blocks.BlockType;
import com.kewldan.logical.Config;
import com.kewldan.logical.LogicalSystem;
import com.kewldan.misc.BaseScene;
import com.kewldan.multiplayer.Client;
import com.kewldan.multiplayer.Host;
import com.kewldan.multiplayer.structures.Player;
import com.kewldan.misc.Coords;
import com.kewldan.misc.MultiplayerStatus;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class Editor implements BaseScene {
    int zoom, ox, oy;
    LogicalSystem game;
    Texture newIcon, empty, saveload;
    ShaderProgram quadsShader;
    SpriteBatch quadRenderer;

    @Override
    public void create(LogicalSystem game) {
        this.game = game;

        quadsShader = new ShaderProgram(Gdx.files.internal("Shaders/quads.vert"), Gdx.files.internal("Shaders/quads.frag"));

        quadRenderer = new SpriteBatch(1, quadsShader);
        quadRenderer.setProjectionMatrix(game.camera.combined);

        zoom = quadsShader.getUniformLocation("zoom");
        ox = quadsShader.getUniformLocation("ox");
        oy = quadsShader.getUniformLocation("oy");

        empty = new Texture("Images/Other/empty.png");
        newIcon = new Texture("Images/Icons/new.png");
        saveload = new Texture("Images/Icons/saving.png");
    }

    @Override
    public void render() {
        ScreenUtils.clear(1, 1, 1, 1f);

        quadRenderer.begin();
        quadRenderer.draw(empty, 0, 0, Config.width, Config.height);
        quadRenderer.end();
        quadsShader.setUniformf(zoom, game.zoom / game.scaleFactorX);
        quadsShader.setUniformf(ox, game.offsetX / game.scaleFactorX);
        quadsShader.setUniformf(oy, game.offsetY);

        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);

        game.shapes.begin(ShapeRenderer.ShapeType.Line);
        game.shapes.setColor(1, 1, 1, 1f);
        game.shapes.rect(game.selX, (Config.height - game.selY), game.selW, -game.selH);
        game.shapes.set(ShapeRenderer.ShapeType.Filled);

        if (game.gui) {
            for (int i = 0; i < (BlockType.values()).length; i++) {
                if (BlockType.values()[i] == game.selectedType) {
                    game.shapes.setColor(0.2588f, 0.96f, 0.584f, 0.25f);
                } else {
                    game.shapes.setColor(1, 1, 1, 0.25f);
                }
                if (i < 14) {
                    roundedRect(game, (25 + i * 80), 25, 64, 64f, 15f);
                } else {
                    roundedRect(game, (25 + (i - 14) * 80), 128, 64f, 64f, 15f);
                }
            }
        }
        game.shapes.end();
        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);

        game.batch.begin(); //DRAW BLOCKS
        for (Block b : game.manager.blocks.values()) {
            int i = b.x * game.zoom + game.offsetX;
            int j = b.y * game.zoom + Math.round(game.offsetY * game.scaleFactorY);
            if (i < -game.zoom || i > Config.width + game.zoom || j < -game.zoom || j > Config.height + game.zoom)
                continue;

            boolean n = b.active || b.blockType == BlockType.ALL;
            if (b.blockType == BlockType.NOT)
                n = !n;
            if (n) {
                b.activeImg.setPosition(i, j);
                b.activeImg.draw(game.batch);
            } else {
                b.passive.setPosition(i, j);
                b.passive.draw(game.batch);
            }
        }
        if (game.gui) {
            for (int i = 0; i < (BlockType.values()).length; i++) {
                BlockType.values()[i].passive.draw(game.batch);
            }
        }
        game.batch.end();

        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
        game.shapes.begin(ShapeRenderer.ShapeType.Filled);
        game.shapes.setColor(1, 1, 0, 0.25f);
        if (Client.isConnected() && Client.players != null) {
            for (Player p : Client.players) {
                try {
                    game.shapes.circle(game.offsetX - p.x + Config.width / 2f, game.offsetY - p.y + Config.height / 2f, 50f);
                } catch (Exception ignored) {

                }
            }
        }

        game.shapes.setColor(new Color(0, 0, 1, 0.2f));
        if (game.coords.size() > 0) {
            for (Coords coord : game.coords) {
                if (game.manager.blocks.containsKey(game.manager.getId(coord.x, coord.y))) {
                    int i = coord.x * game.zoom + game.offsetX;
                    int j = coord.y * game.zoom + Math.round(game.offsetY * game.scaleFactorY);
                    game.shapes.rect(i, j, game.zoom, game.zoom);
                }
            }
        }

        game.shapes.setColor(new Color(0.4f, 0.4f, 0.4f, 0.5f));
        roundedRect(game, Config.width - 64, Config.height / 2f - 96, 128, 192, 12);
        game.shapes.end();
        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);

        game.batch.begin();
        game.batch.draw(saveload, Config.width - 64, Config.height / 2f - 96, 64, 128);
        game.batch.draw(newIcon, Config.width - 64, Config.height / 2f + 32, 64, 64);
        game.batch.end();

        game.fontBatch.begin();
        if (game.gui) {
            //DRAW TEXT
            for (int i = 0; i < (BlockType.values()).length; i++) {
                if (i < 14) {
                    game.font.draw(game.fontBatch, BlockType.values()[i].toSelect + "", (75 + i * 80), 90);
                } else {
                    game.font.draw(game.fontBatch, BlockType.values()[i].toSelect + "", (75 + (i - 14) * 80), 195);
                }
            }
        }

        game.font.draw(game.fontBatch, game.typeStr, 180, Config.height - 30);
        game.font.draw(game.fontBatch, game.rotateStr, (Config.width - 140), (Config.height - 30));

        if (game.coords.size() > 0) {
            game.font.draw(game.fontBatch, game.coords.size() + " " + game.packet.getString("playing.selected"), (Config.width / 2f), (Config.height - 35));
        }

        if (game.debug) {
            int x = Math.round(Gdx.input.getX() * game.scaleFactorX);
            int y = Math.round(Gdx.input.getY() * game.scaleFactorY);
            int x1 = (int) Math.floor(((x - (float) game.offsetX) / game.zoom));
            int y1 = (int) Math.floor((((Config.height - y) - game.offsetY * game.scaleFactorY) / game.zoom));
            Block b = game.manager.getBlock(x1, y1);
            game.font.draw(game.fontBatch, game.offsetX + " | " + game.offsetY + " | " + game.zoom + " | " + x1 + ", " + y1 + (b != null ? (" | " + b.toString()) : ""), 20, 50);
        }

        if (Config.isDev) {
            game.font.draw(game.fontBatch, Gdx.graphics.getFramesPerSecond() + " FPS", 20, (Config.height - 30));
            game.font.draw(game.fontBatch, game.ctps + " TPS", 20, (Config.height - 75));
            game.font.draw(game.fontBatch, game.batch.renderCalls + " calls", 20, (Config.height - 120));
            game.font.draw(game.fontBatch, game.profiler.getVertexCount().count + " vertices", 20, (Config.height - 165));
            if (Config.coopEnable) {
                game.font.draw(game.fontBatch, game.ups + " UPS", 20, (Config.height - 210));
                game.font.draw(game.fontBatch, game.mStatus == MultiplayerStatus.Client ? "CLIENT: " + Client.address : "HOST: " + Host.players.size() + " connections", 20, (Config.height - 255));
            }
        }

        if (Client.isConnected() && Client.players != null) {
            for (Player p : Client.players) {
                try {
                    int x = game.offsetX - p.x + Config.width / 2;
                    int y = game.offsetY - p.y + Config.height / 2;
                    game.drawText(x - 50, y - 50, 100, 100, p.nickname);
                } catch (Exception ignored) {

                }
            }
        }
        game.fontBatch.end();
        game.profiler.reset();
    }

    @Override
    public void dispose() {
        quadRenderer.dispose();
        quadsShader.dispose();
    }

    static void roundedRect(LogicalSystem game, float x, float y, float width, float height, float radius) {
        game.shapes.rect(x + radius, y + radius, width - 2f * radius, height - 2f * radius);
        game.shapes.rect(x + radius, y, width - 2f * radius, radius);
        game.shapes.rect(x + width - radius, y + radius, radius, height - 2f * radius);
        game.shapes.rect(x + radius, y + height - radius, width - 2f * radius, radius);
        game.shapes.rect(x, y + radius, radius, height - 2f * radius);
        game.shapes.arc(x + radius, y + radius, radius, 180f, 90f);
        game.shapes.arc(x + width - radius, y + radius, radius, 270f, 90f);
        game.shapes.arc(x + width - radius, y + height - radius, radius, 0f, 90f);
        game.shapes.arc(x + radius, y + height - radius, radius, 90f, 90f);
    }
}
