package com.kewldan.localization;

import com.badlogic.gdx.Gdx;

public class Language {
    static LanguagePacket[] packets;

    public static final String[] availableLangs = new String[]{"en", "ru"};

    public static void loadFiles() {
        packets = new LanguagePacket[availableLangs.length];
        for (int i = 0; i < availableLangs.length; i++) {
            packets[i] = new LanguagePacket(Gdx.files.internal("Lang/" + availableLangs[i] + ".lang"), availableLangs[i]);
        }
    }

    public static LanguagePacket getByName(int i) {
        return packets[i];
    }
}
