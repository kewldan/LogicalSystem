package com.kewldan.multiplayer;

import com.kewldan.blocks.Block;
import com.kewldan.blocks.BlockType;
import com.kewldan.blocks.Rotate;
import com.kewldan.logical.Config;
import com.kewldan.logical.LogicalSystem;
import com.kewldan.multiplayer.packets.*;
import com.kewldan.multiplayer.structures.Action;
import com.kewldan.multiplayer.structures.Player;
import com.kewldan.misc.GameScene;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Client {
    public static int id, password;
    public static String address, nickname;
    public static Socket socket;
    public static LogicalSystem ls;
    static OutputStream os;
    static InputStream is;
    static Thread listen;
    public static List<Player> players;
    static boolean auth;
    static Logger logger = LogManager.getLogger("CLIENT");

    public static boolean Init(String address, LogicalSystem game, String nickname) throws IOException {
        Client.ls = game;
        Client.address = address;
        Client.nickname = nickname;

        try {
            socket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(address, 48884);
            socket.connect(socketAddress, 1000);
        } catch (Exception e) {
            close();
            ls.scene = GameScene.Menu;
            return false;
        }
        os = socket.getOutputStream();
        is = socket.getInputStream();

        Connect(nickname);
        startListen();
        return true;
    }

    public static boolean isConnected() {
        return socket != null && socket.isConnected() && auth;
    }

    public static void close() {
        if (isConnected()) {
            auth = false;
            listen.interrupt();
            try {
                os.close();
                is.close();
            } catch (IOException ignored) {
            }
            os = null;
            is = null;
            address = null;
        }
    }

    public static void startListen() {
        listen = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    ByteBuffer buffer = Utils.waitForBytes(is);
                    byte type = buffer.get(0);
                    if (type == ResponseADD.v) {
                        logger.info("[" + nickname + "] startListen :: GOT ADD");
                        ResponseADD sadd = new ResponseADD(buffer);
                        id = sadd.id;
                        password = sadd.password;
                        auth = true;
                        logger.info("[" + nickname + "] startListen :: GOT ADD END");
                    } else if (type == ERR.v) {
                        logger.info("[" + nickname + "] startListen :: GOT ERR");
                        ERR err = new ERR(buffer);
                        System.out.println("ERR: " + err.text);
                        logger.info("[" + nickname + "] startListen :: GOT ERR END");
                    } else if (type == ResponseUPD.v) {
                        logger.info("[" + nickname + "] startListen :: GOT UPD");
                        ResponseUPD supd = new ResponseUPD(buffer);
                        players = supd.players;
                        for (Action a : supd.actions) {
                            if ((a.data & 0b11) == 0b1) { //Create
                                ls.manager.create(a.x, a.y, BlockType.getID(a.type), Rotate.getId2(a.data >> 2 & 0b11), false);
                            } else if ((a.data & 0b11) == 0) { //Remove
                                ls.manager.remove(a.x, a.y, false);
                            } else if ((a.data & 0b11) == 2) {
                                ls.manager.rotate(a.x, a.y, Rotate.getId2(a.data >> 2 & 0b11), false);
                            }
                        }
                        logger.info("[" + nickname + "] startListen :: GOT UPD END");
                    } else if (type == ResponseFLL.v) {
                        logger.info("[" + nickname + "] startListen :: GOT FFL");
                        ResponseFLL fll = new ResponseFLL(buffer);
                        ls.manager.blocks.clear();
                        for (Block b : fll.blocks) {
                            ls.manager.create(b.x, b.y, b.blockType, b.rotate, b.active);
                        }
                        logger.info("[" + nickname + "] startListen :: GOT FFL END");
                    } else {
                        logger.info("[" + nickname + "] startListen :: UNKNOWN");
                    }
                } catch (Exception ignored) {

                }
            }
            logger.info("[" + nickname + "] startListen :: ITERATED");
        });
        listen.start();
    }

    public static void send(byte[] bytes) {
        try {
            if (socket != null && socket.isConnected()) {
                os.write(bytes);
                os.flush();
            }
        } catch (Exception e) {
            ls.scene = GameScene.Menu;
            ls.upd.stop();
            Client.close();
        }
    }

    public static void Connect(String nickname) { //When client connect
        logger.info("[" + nickname + "] Connect :: BEGIN");
        ADD add = new ADD(nickname, (short) Config.version);
        ByteBuffer buffer = ByteBuffer.allocate(add.getLength() + 5);
        buffer.putInt(buffer.capacity() - 4);
        buffer.put(ADD.v);
        add.getBytes(buffer);
        send(buffer.array());
        logger.info("[" + nickname + "] Connect :: SENT");
    }

    public static void Update(int x, int y, ArrayList<Action> actions) { //When client update (30 per sec)
        logger.info("[" + nickname + "] Update :: BEGIN");
        UPD upd = new UPD(x, y, id, password, actions);

        ByteBuffer buffer = ByteBuffer.allocate(upd.getLength() + 5);
        buffer.putInt(buffer.capacity() - 4);
        buffer.put(UPD.v);
        upd.getBytes(buffer);
        send(buffer.array());

        if (ls != null)
            ls.cups++;
        logger.info("[" + nickname + "] Update :: SENT");
    }

    public static void Sync() {
        logger.info("[" + nickname + "] Sync :: BEGIN");
        FLL fll = new FLL(id, password);

        ByteBuffer buffer = ByteBuffer.allocate(fll.getLength() + 5);
        buffer.putInt(buffer.capacity() - 4);
        buffer.put(FLL.v);
        fll.getBytes(buffer);

        send(buffer.array());

        logger.info("[" + nickname + "] Sync :: SENT");
    }

    public static void Disconnect() { //When client disconnect
        logger.info("[" + nickname + "] Disconnect :: BEGIN");
        STP stp = new STP(id, password);
        ByteBuffer buffer = ByteBuffer.allocate(stp.getLength() + 5);
        buffer.putInt(buffer.capacity() - 4);
        buffer.put(STP.v);
        stp.getBytes(buffer);
        send(buffer.array());

        logger.info("[" + nickname + "] Disconnect :: SEND");
    }
}
