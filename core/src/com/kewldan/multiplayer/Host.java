package com.kewldan.multiplayer;

import com.kewldan.blocks.Block;
import com.kewldan.blocks.BlockManager;
import com.kewldan.blocks.BlockType;
import com.kewldan.blocks.Rotate;
import com.kewldan.logical.Config;
import com.kewldan.logical.LogicalSystem;
import com.kewldan.multiplayer.packets.*;
import com.kewldan.multiplayer.structures.Action;
import com.kewldan.multiplayer.structures.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

public class Host {
    public static ArrayList<Player> players;
    public static BlockManager manager;
    static ServerSocket server;
    public static Thread listen;
    static Random random;
    static LogicalSystem game;
    static Logger logger = LogManager.getLogger("HOST");

    public static void Init(LogicalSystem game) throws IOException {
        Host.game = game;
        players = new ArrayList<>();
        manager = new BlockManager(game);
        random = new Random();

        server = new ServerSocket(48884);
        logger.info("Hosting started");

        startListening();
    }

    public static void close() {
        logger.info("Trying to stop server");
        if (players != null) {
            for (Player p : players) {
                p.thread.interrupt();
            }
            players.clear();
            players = null;
            listen.interrupt();
            listen = null;
            logger.info("Server successfully has stopped");
        }
    }

    static void startListening() {
        logger.info("Waiting for connections...");
        listen = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket socket = server.accept();
                    InputStream is = socket.getInputStream();
                    OutputStream os = socket.getOutputStream();
                    logger.info("New connection registered");
                    onRecivedPacket(Utils.waitForBytes(is), os, is, socket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        listen.start();
    }

    public static void onRecivedPacket(ByteBuffer data, OutputStream os, InputStream is, Socket socket) {
        data.position(0);
        byte type = data.get();
        logger.info("Packet with type " + type + " was received");
        if (type == ADD.v) {
            if (data.capacity() >= 3 + Config.MIN_NICKNAME_LENGTH) {
                Player g = SADD(data, os);
                logger.info("Handshake is OK, creating thread");
                g.thread = new Thread(() -> {
                    while (!Thread.currentThread().isInterrupted() && socket.isConnected()) {
                        try {
                            onRecivedPacket(Utils.waitForBytes(is), os, is, socket);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    players.remove(g);
                });
                g.thread.start();
                logger.info("New player was connected");
            } else {
                ERR("Too small nickname", os);
            }
        } else if (type == UPD.v) {
            SUPD(data, os);
        } else if (type == STP.v) {
            SSTP(data, os);
        } else if (type == FLL.v) {
            logger.info("Get full map...");
            FLL fll = new FLL(data);
            Player p = get(fll.id, fll.password);
            if (p != null) {
                ArrayList<Block> fuckingList = new ArrayList<>(manager.blocks.values());
                ResponseFLL sfll = new ResponseFLL(fuckingList);

                ByteBuffer buffer = ByteBuffer.allocate(sfll.getLength() + 5);
                buffer.putInt(buffer.capacity() - 4);
                buffer.put(ResponseFLL.v);
                sfll.getBytes(buffer);

                send(os, buffer.array());
                logger.info("Full map is OK");
            }else{
                logger.warn("Player which try to get full map was invalid");
            }
        } else {
            logger.error("Unknown packet type=" + type);
        }

    }

    public static void send(OutputStream os, byte[] bytes) {
        try {
            os.write(bytes);
            os.flush();
            logger.info("sent " + bytes.length + " bytes");
        } catch (IOException e) {
            logger.fatal("IOException with sending packet with length " + bytes.length);
            e.printStackTrace();
        }
    }

    public static Player SADD(ByteBuffer input, OutputStream os) { //When client connect
        logger.info("Handshake testing...");
        ADD packet = new ADD(input);

        logger.info("Packet parser successfully, " + packet.username + " connected");

        int id = random.nextInt();
        int password = random.nextInt();
        Player player = new Player(id, password, packet.version, packet.username);
        players.add(player);

        logger.info("Player registered");

        ResponseADD add = new ResponseADD(id, password);
        ByteBuffer buffer = ByteBuffer.allocate(add.getLength() + 5);
        buffer.putInt(buffer.capacity() - 4);
        buffer.put(ResponseADD.v);
        add.getBytes(buffer);

        logger.info("Response created");

        send(os, buffer.array());
        logger.info("Response sent");
        return player;
    }

    public static void SUPD(ByteBuffer input, OutputStream os) { //When client update (30 per sec)
        logger.info("Packet Update received");
        UPD upd = new UPD(input);

        Player author = get(upd.id, upd.password);
        if (author == null) {
            ERR("You not auth", os);
            logger.error("SUPD :: !!! SUPD END WITH ERR !!! tried connected with id=" + upd.id + ", password=" + upd.password);
        } else {
            author.x = upd.x;
            author.y = upd.y;
            for (Action a : upd.actions) {
                if ((a.data & 0b11) == 0b1) { //Create
                    manager.create(a.x, a.y, BlockType.getID(a.type), Rotate.getId2(a.data >> 2 & 0b11), false);
                } else if ((a.data & 0b11) == 0) { //Remove
                    manager.remove(a.x, a.y, false);
                } else if ((a.data & 0b11) == 2) {
                    manager.rotate(a.x, a.y, Rotate.getId2(a.data >> 2 & 0b11), false);
                }
            }
            for (Player h : players) {
                if (h != author) {
                    logger.info(author.nickname + " sent to " + h.nickname + " " + upd.actions.size() + " actions");
                    h.actions.addAll(upd.actions);
                }
            }
            logger.info("SUPD :: EXECUTED " + upd.actions.size() + " actions");

            logger.info("SUPD :: SENT TO " + author.nickname + " " + author.actions.size() + " ACTIONS");
            ResponseUPD supd = new ResponseUPD(players, author.actions);
            ByteBuffer buffer = ByteBuffer.allocate(supd.getLength() + 5);
            buffer.putInt(buffer.capacity() - 4);
            buffer.put(ResponseUPD.v);
            supd.getBytes(buffer);

            logger.info("Response created");

            send(os, buffer.array());
            author.actions.clear();
            logger.info("SUPD :: SUPD END SUCCESS");
        }
    }

    public static void SSTP(ByteBuffer input, OutputStream os) { //When client disconnect
        STP stp = new STP(input);
        Player p = get(stp.id, stp.password);
        if (p != null) {
            players.remove(p);
        }
    }

    public static Player get(int id, int password) {
        for (Player p : players) {
            if (p.id == id && p.password == password) {
                return p;
            }
        }
        return null;
    }

    public static void ERR(String text, OutputStream os) {
        ERR err = new ERR(text);

        ByteBuffer buffer = ByteBuffer.allocate(err.getLength());
        buffer.putInt(buffer.capacity() - 4);
        buffer.put(ERR.v);

        err.getBytes(buffer);

        send(os, buffer.array());
    }
}
