package com.kewldan.logical;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;

public class Config {
    public static int width = 1280;
    public static int height = 720;

    public static int tps = 12;
    public static boolean vsync = true;
    public static boolean isDev = false;
    public static int lang = 0;

    public static final int minZoom = 8;
    public static final int maxZoom = 64;
    public static final int version = 32;
    public static final String versionCode = "";
    public static final String discord = "https://discord.gg/b9UwgQpUQM";
    public static final String itch = "https://kewldan.itch.io/logical-system";
    public static final boolean coopEnable = false;
    public static final int ups = 1;

    public static int l = 0;
    private static Preferences preferences;
    public static int langFirst;

    public static final Color DARK = new Color(0x2b2d32ff);
    public static final Color LIGHT = new Color(0x33353aff);


    public static final int MIN_NICKNAME_LENGTH = 1;

    public static void init() {
        preferences = Gdx.app.getPreferences("arrows");
    }

    public static void save() {
        preferences.putBoolean("vsync", vsync);
        preferences.putInteger("tps", tps);
        preferences.putBoolean("dev", isDev);
        preferences.putInteger("lang", lang);
        preferences.flush();
    }

    public static void load() {
        if (preferences.contains("min"))
            l = preferences.getInteger("min");
        vsync = preferences.getBoolean("vsync");
        tps = preferences.getInteger("tps");
        isDev = preferences.getBoolean("dev");
        lang = preferences.getInteger("lang");
        langFirst = lang;
    }

    public static boolean has() {
        return (preferences.contains("vsync") && preferences.contains("tps") && preferences.contains("dev") && preferences.contains("lang"));
    }

    public static void minute() {
        if (preferences.contains("min"))
            l = preferences.getInteger("min");
        l++;
        preferences.putInteger("min", l);
        preferences.flush();
    }
}
