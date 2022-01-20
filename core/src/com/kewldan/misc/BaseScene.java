package com.kewldan.misc;

import com.kewldan.logical.LogicalSystem;

public interface BaseScene {
    void create(LogicalSystem game);
    void render();
    void dispose();
}
