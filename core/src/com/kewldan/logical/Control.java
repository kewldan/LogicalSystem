package com.kewldan.logical;

import com.kewldan.control.keyDown;
import com.kewldan.control.touchDown;
import com.kewldan.control.touchDragged;
import com.kewldan.control.touchUp;
import com.kewldan.misc.GameScene;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

import java.io.IOException;

public class Control implements InputProcessor {
    LogicalSystem game;
    public static int pmouseX = 0, pmouseY = 0;
    public static boolean select, ctrl, r;


    Control(LogicalSystem game) {
        this.game = game;
    }

    public boolean keyDown(int keycode) {
        try {
            keyDown.on(game, keycode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.CONTROL_LEFT)
            ctrl = false;
        if (keycode == Input.Keys.R)
            r = false;
        return false;
    }

    public boolean keyTyped(char character) {
        return false;
    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        try {
            touchDown.on(game, screenX, screenY, pointer, button);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        touchUp.on(game, screenX, screenY, pointer, button);
        return false;
    }

    public boolean touchDragged(int xpos, int ypos, int pointer) {
        touchDragged.on(game, xpos, ypos, pointer);
        return false;
    }

    public boolean mouseMoved(int xpos, int ypos) {
        int x = Math.round(xpos * game.scaleFactorX);
        int y = Math.round(ypos * game.scaleFactorY);
        pmouseX = x;
        pmouseY = y;
        if (r) {
            int x1 = (int) Math.floor(((x - (float) game.offsetX) / game.zoom));
            int y1 = (int) Math.floor((((Config.height - y) - game.offsetY * game.scaleFactorY) / game.zoom));
            game.manager.remove(x1, y1, true);
        }
        return false;
    }

    public boolean scrolled(float amountX, float amountY) {
        if (game.scene == GameScene.Editor) {
            game.zoom += 2 * -amountY;
            game.zoom = Math.min(Math.max(Config.minZoom, game.zoom), Config.maxZoom);
            game.manager.updateZoom();
        } else if (game.scene == GameScene.SaveManager) {
            if(game.cache.size() >= 5) {
                game.savesOffset += amountY * 40;
                game.savesOffset = Math.min(Math.max(0, game.savesOffset), (game.cache.size() - 5) * 150 + 75);
            }
        }else if(game.scene == GameScene.Tutorial){
            game.master.scroll -= amountY * 30;
            game.master.scroll = Math.min(Math.max(game.master.scroll, game.master.getSelected().maxScroll), 0);
        }
        return false;
    }
}
