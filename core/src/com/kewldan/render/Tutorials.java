package com.kewldan.render;

import com.kewldan.logical.Config;
import com.kewldan.logical.LogicalSystem;
import com.kewldan.misc.BaseScene;
import com.badlogic.gdx.utils.ScreenUtils;

public class Tutorials implements BaseScene {
    LogicalSystem game;

    @Override
    public void create(LogicalSystem game) {
        this.game = game;
    }

    @Override
    public void render() {
        ScreenUtils.clear(Config.LIGHT);
        game.master.drawSelected();
    }

    @Override
    public void dispose() {

    }
}
