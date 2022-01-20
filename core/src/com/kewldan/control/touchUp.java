package com.kewldan.control;

import com.kewldan.blocks.Block;
import com.kewldan.logical.Config;
import com.kewldan.logical.LogicalSystem;
import com.kewldan.misc.Coords;
import com.kewldan.logical.Control;

public class touchUp {
    public static void on(LogicalSystem game, int screenX, int screenY, int pointer, int button) {
        if (Control.select) {
            game.coords.clear();
            for (Block blc : game.manager.blocks.values()) {
                int x = blc.x * game.zoom + game.offsetX;
                int y = Config.height - (blc.y * game.zoom + Math.round(game.offsetY * game.scaleFactorY));
                if (x > game.selX && x < game.selX + game.selW && y > game.selY && y < game.selY + game.selH)
                    game.coords.add(new Coords(blc.x, blc.y));
            }
            game.selX = 0;
            game.selY = 0;
            game.selW = 0;
            game.selH = 0;
            Control.select = false;
        }
    }
}
