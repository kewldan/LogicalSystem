package com.kewldan.control;

import com.kewldan.blocks.BlockType;
import com.kewldan.blocks.Rotate;
import com.kewldan.logical.Config;
import com.kewldan.logical.LogicalSystem;
import com.kewldan.multiplayer.Client;
import com.kewldan.misc.BytesCompressor;
import com.kewldan.misc.Coords;
import com.kewldan.misc.GameScene;
import com.kewldan.misc.LSStatus;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.kewldan.logical.Control;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.Deflater;

public class keyDown {
    private static Pixmap getScreenshot(int w, int h){
        final Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, w, h);
        return pixmap;
    }

    public static void on(LogicalSystem game, int keycode) throws IOException {
        if (keycode == Input.Keys.CONTROL_LEFT)
            Control.ctrl = true;
        int x = Math.round(Gdx.input.getX() * game.scaleFactorX);
        int y = Math.round(Gdx.input.getY() * game.scaleFactorY);
        if (game.ls == LSStatus.None) {
            if (keycode == Input.Keys.F11) {
                if (Gdx.graphics.isFullscreen()) {
                    Gdx.graphics.setWindowedMode(Config.width, Config.height);
                    Gdx.graphics.setUndecorated(false);
                } else {
                    Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                    Gdx.graphics.setUndecorated(true);
                }
                return;
            }
            if (keycode == Input.Keys.ESCAPE) {
                if (game.scene == GameScene.Editor) {
                    game.scene = GameScene.Menu;
                } else {
                    if (Config.coopEnable) {
                        if (game.mStatus == null) {
                            game.goSinglePlayer("Dungeon master");
                        } else {
                            game.scene = GameScene.Editor;
                        }
                    } else {
                        game.scene = GameScene.Editor;
                    }
                }
                return;
            }
            if (game.scene == GameScene.Editor) {
                if(!Control.ctrl) {
                    for (BlockType bt : BlockType.values()) {
                        if (bt.toSelect == Character.toUpperCase(Input.Keys.toString(keycode).charAt(0))) {
                            game.selectedType = bt;
                            game.typeStr = game.packet.getString("elements." + game.selectedType.toString());
                            return;
                        }
                    }
                }
                if (keycode == Input.Keys.R) {
                    Control.r = true;
                    int x1 = (int) Math.floor(((x - (float) game.offsetX) / game.zoom));
                    int y1 = (int) Math.floor((((Config.height - y) - game.offsetY * game.scaleFactorY) / game.zoom));
                    game.manager.remove(x1, y1, true);
                } else if (keycode == Input.Keys.T) {
                    game.selectedRotate = Rotate.getId(game.selectedRotate.deg + 90);
                    game.rotateStr = game.packet.getString("rotate." + game.selectedRotate.toString());
                } else if (keycode == Input.Keys.F) {
                    game.offsetY = 0;
                    game.offsetX = 0;
                    game.zoom = 10;
                    game.manager.updateZoom();
                } else if (keycode == Input.Keys.F1) {
                    game.gui = !game.gui;
                } else if (keycode == Input.Keys.F2) {
                    game.debug = !game.debug;
                } else if (keycode == Input.Keys.F10) {
                    game.bench.printTable();
                } else if (keycode == Input.Keys.F4) {
                    if (Config.coopEnable)
                        Client.Sync();
                } else if (keycode == Input.Keys.SPACE) {
                    game.pause = !game.pause;
                } else if (Control.ctrl && (keycode == Input.Keys.C || keycode == Input.Keys.X)) {
                    int x1 = (int) Math.floor(((x - (float) game.offsetX) / game.zoom));
                    int y1 = (int) Math.floor((((Config.height - y) - game.offsetY * game.scaleFactorY) / game.zoom));
                    Gdx.app.getClipboard().setContents(
                            BytesCompressor.encode(
                                    BytesCompressor.compress(
                                            game.manager.getShortBlockBytes(game.coords, x1, y1).array()
                                    )
                            )
                    );
                    if(keycode == Input.Keys.X){
                        for(Coords c : game.coords){
                            game.manager.blocks.remove(game.manager.getId(c.x, c.y));
                        }
                    }
                } else if (Control.ctrl && keycode == Input.Keys.V) {
                    try {
                        int x1 = (int) Math.floor(((x - (float) game.offsetX) / game.zoom));
                        int y1 = (int) Math.floor((((Config.height - y) - game.offsetY * game.scaleFactorY) / game.zoom));
                        byte[] decoded = BytesCompressor.decode(Gdx.app.getClipboard().getContents());
                        if (decoded != null) {
                            byte[] decompressed = BytesCompressor.decompress(decoded);
                            if (decompressed != null) {
                                ByteBuffer buffer = ByteBuffer.wrap(decompressed);
                                game.manager.loadShortBlockBytes(buffer, x1, y1);
                            }
                        }
                    } catch (Exception ignored) {
                    }
                } else if (Control.ctrl && keycode == Input.Keys.S) {
                    game.scene = GameScene.Menu;
                    game.ls = LSStatus.Save;
                }else if(keycode == Input.Keys.F12){
                     FileHandle handle = Gdx.files.absolute(game.getScreenshotPath() + File.separator + System.currentTimeMillis() + ".png");
                     PixmapIO.writePNG(handle, getScreenshot(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), Deflater.DEFAULT_COMPRESSION, true);
                }
            }
        } else {
            if (keycode == Input.Keys.ENTER) {
                if (!game.tempString.isEmpty()) {
                    try {
                        game.manager.saveFullBytes(game.tempString);
                        if (Config.coopEnable)
                            game.goSinglePlayer("Dungeon master");
                        else
                            game.scene = GameScene.Editor;
                    } catch (IOException ignored) {
                        game.sendMsg(game.packet.getString("ERROR"));
                    }
                } else {
                    game.sendMsg(game.packet.getString("saving.close"));
                }
                game.ls = LSStatus.None;
                game.tempString = "";
            }
            if (keycode == Input.Keys.BACKSPACE && !game.tempString.isEmpty())
                game.tempString = game.tempString.substring(0, game.tempString.length() - 1);
            if ((keycode >= Input.Keys.A && keycode <= Input.Keys.Z) || (keycode >= Input.Keys.NUM_0 && keycode <= Input.Keys.NUM_9))
                if (game.tempString.length() < 10) {
                    game.tempString += Input.Keys.toString(keycode);
                }
        }
    }
}
