package com.kewldan.control;

import com.kewldan.blocks.Block;
import com.kewldan.blocks.BlockType;
import com.kewldan.blocks.Rotate;
import com.kewldan.logical.Config;
import com.kewldan.logical.LogicalSystem;
import com.kewldan.misc.Button;
import com.kewldan.misc.GameScene;
import com.kewldan.misc.LSStatus;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.io.IOException;

import static com.kewldan.logical.Control.ctrl;
import static com.kewldan.logical.Control.select;

public class touchDown {
    public static void on(LogicalSystem game, int screenX, int screenY, int pointer, int button) throws Exception {
        int x = Math.round(screenX * game.scaleFactorX);
        int y = Math.round(screenY * game.scaleFactorY);
        if (game.scene.equals(GameScene.Editor)) {
            if (button == Input.Buttons.RIGHT) {
                int x1 = (int) Math.floor(((x - (float) game.offsetX) / game.zoom));
                int y1 = (int) Math.floor((((Config.height - y) - game.offsetY * game.scaleFactorY) / game.zoom));
                Block b = game.manager.getBlock(x1, y1);
                if (b != null) {
                    if (!b.blockType.equals(BlockType.BTN)) {
                        game.manager.rotate(b.x, b.y, Rotate.getId(b.rotate.deg + 90), true);
                    } else {
                        b.active = !b.active;
                    }
                } else {
                    game.manager.create(x1, y1, game.selectedType, game.selectedRotate, true);
                }
            } else if (button == Input.Buttons.LEFT && ctrl) {
                select = true;
                game.selX = x;
                game.selY = y;
            } else if (button == Input.Buttons.LEFT && x > Config.width - 64) {
                if(y > Config.height / 2f + 32  && y < Config.height / 2f + 96){
                    game.updateCache();
                    game.scene = GameScene.SaveManager;
                }else if(y > Config.height / 2f - 32 && y < Config.height / 2f + 32){
                    game.sendMsg(game.packet.getString("saving.save"));
                    game.ls = LSStatus.Save;
                    game.scene = GameScene.Menu;
                }else if(y > Config.height / 2f - 96 && y < Config.height / 2f - 32){
                    game.manager.blocks.clear();
                }
            }
            if (button == Input.Buttons.LEFT && game.gui) {
                for (int i = 0; i < (BlockType.values()).length; i++) {
                    if (i < 14) {
                        if (x > 25 + i * 80 && x < 25 + i * 80 + 64 && Config.height - y > 25 && Config.height - y < 25 + 64) {
                            game.selectedType = BlockType.values()[i];
                            game.typeStr = game.packet.getString("elements." + game.selectedType.toString());
                        }
                    } else {
                        if (x > (25 + (i - 14) * 80) && x < (25 + (i - 14) * 80) + 64 && Config.height - y > 128 && Config.height - y < 192) {
                            game.selectedType = BlockType.values()[i];
                            game.typeStr = game.packet.getString("elements." + game.selectedType.toString());
                        }
                    }
                }
            }
        } else if (game.scene.equals(GameScene.Menu)) {
            for (Button btn : game.menu)
                btn.collide(x, Config.height - y);
            if (x > 8 && x < 56 && Config.height - y > 48 && Config.height - y < 96 && button == Input.Buttons.LEFT) {
                game.scene = GameScene.Options;
            } else if (x > 8 && x < 56 && Config.height - y > 128 && Config.height - y < 128 + 48 && button == Input.Buttons.LEFT) {
                Gdx.net.openURI(Config.discord);
            } else if (x > 8 && x < 56 && Config.height - y > 208 && Config.height - y < 208 + 48 && button == Input.Buttons.LEFT) {
                Gdx.net.openURI(Config.itch);
            }
        } else if (game.scene.equals(GameScene.Options)) {
            for (Button btn : game.settings)
                btn.collide(x, Config.height - y);
        } else if (game.scene.equals(GameScene.SaveManager)) {
            y = Config.height - y;
            if (x > Config.width - 75 - game.btn.getWidth() && x < Config.width - 75) {
                for (int i = 0; i < game.cache.size(); i++) {
                    if (y > Config.height - (i * 150) - game.btn.getHeight() / 2f + game.savesOffset + 40 && y < Config.height - (i * 150) - game.btn.getHeight() / 2f + game.savesOffset + 40 + game.btn.getHeight()) {
                        try {
                            game.manager.blocks.clear();
                            game.manager.loadFullBytes((game.cache.get(i - 1)).path);
                            if (Config.coopEnable)
                                game.goSinglePlayer("Dungeon master");
                            else
                                game.scene = GameScene.Editor;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
