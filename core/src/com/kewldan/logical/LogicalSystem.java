package com.kewldan.logical;

import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.kewldan.blocks.BlockManager;
import com.kewldan.blocks.BlockType;
import com.kewldan.blocks.Rotate;
import com.kewldan.localization.Language;
import com.kewldan.localization.LanguagePacket;
import com.kewldan.multiplayer.Client;
import com.kewldan.multiplayer.Host;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.kewldan.benchmark.Benchmark;
import com.kewldan.misc.*;
import com.kewldan.render.*;
import com.kewldan.tutorials.TutorialMaster;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.text.CharacterIterator;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Date;

public class LogicalSystem extends ApplicationAdapter implements ActionListener {
    public static LogicalSystem instance;
    public ShapeRenderer shapes;
    public OrthographicCamera camera;
    public SpriteBatch batch, fontBatch;
    public BitmapFont font, fontSmall;
    InputMultiplexer inputMultiplexer;
    public GlyphLayout layout;
    public ShaderProgram fontShader;
    public GLProfiler profiler;

    public int offsetX, offsetY, ticks, ctps, ups, cups;
    public int savesOffset = 0;
    public int zoom = 10;
    public long lastMsg = 0;
    public boolean gui, debug, pause;
    public int selX, selY, selW, selH;
    public float scaleFactorX = 1f, scaleFactorY = 1f;
    public String typeStr = "", rotateStr = "";
    public BaseScene[] scenes;

    public BlockManager manager;
    public BlockType selectedType;
    public Rotate selectedRotate;
    public Timer ticker, sec, tps, upd;
    public GameScene scene = GameScene.Menu;
    public Texture btn, fontTexture;
    public LSStatus ls = LSStatus.None;
    public String msg = "", tempString = "";
    public ArrayList<SaveInfo> cache;
    public Button[] menu, settings;
    public LanguagePacket packet;
    public ArrayList<Coords> coords;
    public Benchmark bench;
    public SimpleDateFormat sdf;
    public MultiplayerStatus mStatus;
    public TutorialMaster master;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Config.isDev ? Application.LOG_DEBUG : Application.LOG_NONE);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Config.width, Config.height);

        ShaderProgram.pedantic = false;
        fontShader = new ShaderProgram(Gdx.files.internal("Shaders/font.vert"), Gdx.files.internal("Shaders/font.frag"));

        shapes = new ShapeRenderer();
        shapes.setProjectionMatrix(camera.combined);
        shapes.setColor(Color.BLACK);
        shapes.setAutoShapeType(true);
        layout = new GlyphLayout();

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        fontBatch = new SpriteBatch(1, fontShader);
        fontBatch.setProjectionMatrix(camera.combined);

        bench = new Benchmark();

        manager = new BlockManager(this);
        fontTexture = new Texture(Gdx.files.internal("Fonts/font.png"), true);
        fontTexture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear);
        font = new BitmapFont(Gdx.files.internal("Fonts/font.fnt"), new TextureRegion(fontTexture));
        fontSmall = new BitmapFont(Gdx.files.internal("Fonts/font.fnt"), new TextureRegion(fontTexture));
        fontSmall.getData().setScale(0.8f);
        btn = new Texture("Images/Other/btn.png");

        Config.init();
        if (Config.has()) {
            Config.load();
        } else {
            Config.save();
        }

        Gdx.graphics.setVSync(Config.vsync);
        Gdx.graphics.setForegroundFPS(Config.vsync ? 60 : 1000);

        coords = new ArrayList<>();
        cache = new ArrayList<>();
        Language.loadFiles();
        packet = Language.getByName(Config.lang);
        sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");

        //Load scenes
        scenes = new BaseScene[5];
        scenes[0] = new Menu();
        scenes[1] = new Options();
        scenes[2] = new Editor();
        scenes[3] = new SaveManager();
        scenes[4] = new Tutorials();

        for(BaseScene baseScene : scenes){
            baseScene.create(this);
        }
        ////////////

        master = new TutorialMaster();
        master.loadTutorials();

        menu = new Button[Config.coopEnable ? 4 : 3];
        menu[0] = new Button(Config.width - 150 - btn.getWidth(), Config.height - 150, packet.getString("pages.singleplayer"), this, (x, y) -> {
            if (Config.coopEnable) {
                if (mStatus == null) {
                    goSinglePlayer("Dungeon master");
                } else {
                    scene = GameScene.Editor;
                }
            } else {
                scene = GameScene.Editor;
            }
        });
        menu[1] = new Button(Config.width - 150 - btn.getWidth(), Config.height - 150 - (30 + btn.getHeight()), packet.getString("pages.tutorial"), this, (x, y) -> {
            scene = GameScene.Tutorial;
            master.scroll = 0;
        });
        if (Config.coopEnable) {
            menu[2] = new Button(Config.width - 150 - btn.getWidth(), Config.height - 150 - 2 * (30 + btn.getHeight()), packet.getString("pages.multiplayer"), this, (x, y) -> {
                if (Config.coopEnable) {
                    Client.close();
                    if (Client.Init("localhost", this, "Fucking slave")) {
                        mStatus = MultiplayerStatus.Client;
                        scene = GameScene.Editor;
                    } else {
                        sendMsg("Connection timeout");
                    }
                } else {
                    scene = GameScene.Editor;
                }
            });
            menu[3] = new Button(Config.width - 150 - btn.getWidth(), Config.height - 150 - 3 * (30 + btn.getHeight()), packet.getString("pages.exit"), this, (x, y) -> Gdx.app.exit());
        } else {
            menu[2] = new Button(Config.width - 150 - btn.getWidth(), Config.height - 150 - 2 * (30 + btn.getHeight()), packet.getString("pages.exit"), this, (x, y) -> Gdx.app.exit());
        }

        settings = new Button[6];
        settings[0] = new Button(50, Config.height - 50 - btn.getHeight(), "VSync: " + (Config.vsync ? "ON" : "OFF"), this, (x, y) -> {
            Config.vsync = !Config.vsync;
            Gdx.graphics.setVSync(Config.vsync);
            Gdx.graphics.setForegroundFPS(Config.vsync ? 60 : 1000);
            settings[0].text = "VSync: " + (Config.vsync ? "ON" : "OFF");
            Config.save();
        });
        settings[1] = new Button(50, Config.height - 2 * (50 + btn.getHeight()), packet.getString("settings.dev") + ": " + packet.getString(Config.isDev ? "global.yes" : "global.no"), this, (x, y) -> {
            Config.isDev = !Config.isDev;
            settings[1].text = packet.getString("settings.dev") + ": " + packet.getString(Config.isDev ? "global.yes" : "global.no");
            Config.save();
        });
        settings[2] = new Button(50, Config.height - 3 * (50 + btn.getHeight()), packet.name, this, (x, y) -> {
            Config.lang++;
            Config.lang %= Language.availableLangs.length;
            packet = Language.getByName(Config.lang);
            settings[2].text = packet.name;
            Config.save();
        });
        settings[3] = new Button(50, Config.height - 4 * (50 + btn.getHeight()), packet.getString("global.back"), this, (x, y) -> scene = GameScene.Menu);
        settings[4] = new Button(100 + btn.getWidth(), Config.height - 50 - btn.getHeight(), "+", 60, 60, this, (x, y) -> {
            Config.tps++;
            Config.tps = Math.min(Math.max(1, Config.tps), Config.isDev ? 128 : 32);
            ticker.setDelay(1000 / Config.tps);
            Config.save();
        });
        settings[5] = new Button(150 + btn.getWidth() + 170, Config.height - 50 - btn.getHeight(), "-", 60, 60, this, (x, y) -> {
            Config.tps--;
            Config.tps = Math.min(Math.max(1, Config.tps), Config.isDev ? 128 : 32);
            ticker.setDelay(1000 / Config.tps);
            Config.save();
        });

        selectedType = BlockType.WIRE;
        typeStr = packet.getString("elements." + selectedType.toString());
        selectedRotate = Rotate.UP;
        rotateStr = packet.getString("rotate." + selectedRotate.toString());

        ticker = new Timer(1000 / Config.tps, this);
        ticker.setActionCommand("tick");
        ticker.start();
        sec = new Timer(60000, this);
        sec.setActionCommand("min");
        sec.start();
        tps = new Timer(1000, this);
        tps.setActionCommand("tps");
        tps.start();
        upd = new Timer(1000 / Config.ups, this);
        upd.setActionCommand("upd");
        upd.start();

        profiler = new GLProfiler(Gdx.graphics);
        profiler.enable();

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(new Control(this));
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render() {
        if (scene.equals(GameScene.Menu)) {
            scenes[0].render();
        } else if (scene.equals(GameScene.Options)) {
            scenes[1].render();
        } else if (scene.equals(GameScene.Editor)) {
            scenes[2].render();
        } else if (scene.equals(GameScene.SaveManager)) {
            scenes[3].render();
        } else if (scene.equals(GameScene.Tutorial)) {
            scenes[4].render();
        }
    }

    @Override
    public void dispose() {
        if (Config.coopEnable) {
            upd.stop();
            if (Host.listen != null) Host.listen.interrupt();
            Client.Disconnect(); //Stop
        }
        for(BaseScene baseScene : scenes){
            baseScene.dispose();
        }

        shapes.dispose(); //Shape renderer
        batch.dispose();
        fontBatch.dispose(); //Sprite Batches
        font.dispose(); //Fonts

        fontShader.dispose();
    }

    @Override
    public void resize(int w, int h) {
        scaleFactorX = Config.width / (float) w;
        scaleFactorY = Config.height / (float) h;
    }

    public void goSinglePlayer(String nickname) throws IOException {
        Host.close();
        Client.close();
        Host.Init(this);
        Client.Init("8.8.8.8", this, nickname);
        mStatus = MultiplayerStatus.Host;
        scene = GameScene.Editor;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("tick")) {
            if (scene == GameScene.Editor && !pause)
                manager.tick();
        } else if (e.getActionCommand().equals("min")) {
            Config.minute();
        } else if (e.getActionCommand().equals("tps")) {
            ctps = ticks;
            ticks = 0;
            ups = cups;
            cups = 0;
        } else if (e.getActionCommand().equals("upd")) {
            if (Client.isConnected()) {
                Client.Update(offsetX, offsetY, manager.actions);
                manager.actions.clear();
            }
        }
    }

    public void drawText(int x, int y, int w, int h, String text) {
        layout.setText(font, text);
        float fontX = x + (w - layout.width) / 2f;
        float fontY = y + (h + layout.height) / 2f;
        font.draw(fontBatch, text, fontX, fontY);
    }

    public float getMouseX() {
        return Gdx.input.getX() * scaleFactorX;
    }

    public float getMouseY() {
        return Config.height - Gdx.input.getY() * scaleFactorY;
    }

    public float getStringWidth(String text) {
        layout.setText(font, text);
        return layout.width;
    }

    public void sendMsg(String t) {
        msg = t;
        lastMsg = System.currentTimeMillis() + 2000;
    }

    public void updateCache() {
        File fol = new File(getSchemasPath());
        if (!fol.exists())
            fol.mkdirs();
        cache.clear();
        Date date = new Date();
        File[] files = fol.listFiles();
        for (File f : files) {
            if (f.isFile()) {
                if (f.getName().endsWith(".bin")) {
                    try {
                        ByteBuffer buff = ByteBuffer.wrap(Files.readAllBytes(f.toPath()));
                        date.setTime(buff.getLong(2));
                        cache.add(new SaveInfo(getVersion(buff.getShort(0)), buff.getShort(0), f.getName().replace(".bin", ""), sdf.format(date), f.getPath(), humanReadableByteCountBin(f.length()), (int) ((f.length() - 19) / 11f)));
                        buff.clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (f.getName().endsWith(".ls")) {
                    try {
                        ByteBuffer buff = ByteBuffer.wrap(BytesCompressor.decompress(Files.readAllBytes(f.toPath())));
                        date.setTime(buff.getLong(2));
                        cache.add(new SaveInfo(getVersion(buff.getShort(0)), buff.getShort(0), f.getName().replace(".ls", ""), sdf.format(date), f.getPath(), humanReadableByteCountBin(f.length()), (int) ((buff.capacity() - 19) / 11f)));
                        buff.clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static String humanReadableByteCountBin(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.1f %cB", value / 1024.0, ci.current());
    }

    public String getSchemasPath() {
        return System.getenv("APPDATA") + File.separator + "Arrows" + File.separator + "schemas";
    }

    public String getScreenshotPath() {
        File fol = new File(System.getenv("APPDATA") + File.separator + "Arrows" + File.separator + "screenshots");
        if (!fol.exists()) fol.mkdirs();
        return fol.getAbsolutePath();
    }

    public String getVersion(int code) {
        if (code < 10) {
            return packet.getString("saves.outdated");
        } else if (code > Config.version + 1) {
            return packet.getString("saves.nonsupport");
        } else if (code <= 25) {
            return "1." + (code - 10) + "B";
        } else {
            return "1." + (code - 10) + "R";
        }
    }
}