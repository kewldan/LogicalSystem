package com.kewldan.control;

import com.kewldan.logical.Config;
import com.kewldan.logical.LogicalSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.kewldan.logical.Control;

public class touchDragged {
    public static void on(LogicalSystem game, int xpos, int ypos, int pointer) {
        int x = Math.round(xpos * game.scaleFactorX);
        int y = Math.round(ypos * game.scaleFactorY);
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            if (!Control.select) {
                game.offsetX -= Control.pmouseX - x;
                game.offsetY += (Control.pmouseY - y) / game.scaleFactorY;
            } else {
                game.selW = x - game.selX;
                game.selH = y - game.selY;
            }
        } else if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            int x1 = (int) Math.floor(((x - (float) game.offsetX) / game.zoom));
            int y1 = (int) Math.floor((((Config.height - y) - game.offsetY * game.scaleFactorY) / game.zoom));
            game.manager.create(x1, y1, game.selectedType, game.selectedRotate, true);
        }
        Control.pmouseX = x;
        Control.pmouseY = y;
    }
}
