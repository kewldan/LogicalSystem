package com.avenger.logical.desktop;

import com.kewldan.logical.Config;
import com.kewldan.logical.LogicalSystem;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.kewldan.misc.BytesCompressor;
import io.sentry.Sentry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DesktopLauncher {
    static Lwjgl3Application application;

    public static void main(String[] arg) {
        Sentry.init(options -> {
            options.setDsn("https://f7cc943294da4914a92a35cf6d5f984a@o954513.ingest.sentry.io/5903614");
            options.setTracesSampleRate(1.0);
        });
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(Config.width, Config.height);
        config.setTitle("Logical system");
        config.setResizable(true);
        config.setWindowIcon("icon.png");
        config.setInitialVisible(true);
        config.enableGLDebugOutput(Config.isDev, System.out);
        config.disableAudio(true);
        try {
            LogicalSystem.instance = new LogicalSystem();
            application = new Lwjgl3Application(LogicalSystem.instance, config);
        } catch (Exception e) {
            Sentry.captureException(e);
            e.printStackTrace();
        }
        System.exit(0);
    }
}
