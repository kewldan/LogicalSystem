package com.kewldan.tutorials;

import com.kewldan.logical.LogicalSystem;

public interface TutorialAsset {
    void render(LogicalSystem game, int scroll);
    int getHeight();
    int getType();
}
