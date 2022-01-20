package com.kewldan.blocks;

import com.kewldan.logical.Config;
import com.kewldan.logical.LogicalSystem;
import com.kewldan.multiplayer.structures.Action;
import com.kewldan.misc.BytesCompressor;
import com.kewldan.misc.Coords;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;

public class BlockManager {
    public HashMap<Long, Block> blocks;
    public ArrayList<Action> actions;

    LogicalSystem game;

    public BlockManager(LogicalSystem game) {
        this.game = game;
        this.blocks = new HashMap<>();
        this.actions = new ArrayList<>();
    }

    public Block getBlock(long id) {
        return blocks.getOrDefault(id, null);
    }

    public Block getBlock(int x, int y) {
        return getBlock(getId(x, y));
    }

    public void remove(int x, int y, boolean send) {
        blocks.remove(getId(x, y));
        if (send)
            actions.add(Action.remove(x, y));
    }

    public long getId(int x, int y){
        return (long) x << 32 | y & 0xFFFFFFFFL;
    }

    public void create(int x, int y, BlockType type, Rotate rotate, boolean send) {
        long id = getId(x, y);
        if (!blocks.containsKey(id)) {
            Block b1 = new Block(type, rotate, game.zoom);
            b1.setPosition(x, y);
            blocks.put(id, b1);
            if (send)
                actions.add(Action.create(x, y, type, rotate));
        }
    }

    public void rotate(int x, int y, Rotate rotate, boolean send) {
        Block b = getBlock(x, y);
        if (b != null) {
            b.setRotate(rotate);
            if (send)
                actions.add(Action.rotate(x, y, rotate));
        }
    }

    public void tick() {
        try {
            for (long key : blocks.keySet()) {
                Block j = getBlock(key);
                int x = j.x; //Get last 32 bits
                int y = j.y; //Get first 32 bits
                if (j.blockType == BlockType.ALL || j.blockType == BlockType.BTN) {
                    if (j.blockType == BlockType.ALL || j.active) {
                        setActive(x, y - 1);
                        setActive(x, y + 1);
                        setActive(x - 1, y);
                        setActive(x + 1, y);
                    }
                } else if ((j.blockType == BlockType.WIRE || j.blockType == BlockType.LONG_WIRE) && j.active) {
                    setActiveHigh(j.rotate, x, y, j.blockType == BlockType.LONG_WIRE ? 2 : 1);
                } else if (j.blockType == BlockType.NOT && !j.active) {
                    setActiveHigh(j.rotate, x, y);
                } else if (j.blockType == BlockType.TRIPLE_WIRE && j.active) {
                    if (j.rotate.equals(Rotate.UP)) {
                        setActive(x, y + 1);
                        setActive(x - 1, y);
                        setActive(x + 1, y);
                    }
                    if (j.rotate.equals(Rotate.RIGHT)) {
                        setActive(x, y - 1);
                        setActive(x, y + 1);
                        setActive(x + 1, y);
                    }
                    if (j.rotate.equals(Rotate.DOWN)) {
                        setActive(x, y - 1);
                        setActive(x - 1, y);
                        setActive(x + 1, y);
                    }
                    if (j.rotate.equals(Rotate.LEFT)) {
                        setActive(x, y - 1);
                        setActive(x, y + 1);
                        setActive(x - 1, y);
                    }
                } else if ((j.blockType.equals(BlockType.AND) || j.blockType.equals(BlockType.XOR) || j.blockType.equals(BlockType.NXOR) || j.blockType.equals(BlockType.NAND)) && j.active) {
                    setActiveHigh(j.rotate, x, y);
                } else if (j.blockType.equals(BlockType.THIRD_WIRE) && j.active) {
                    setActiveHigh(j.rotate, x, y, 3);
                } else if (j.blockType.equals(BlockType.DOUBLE_WIRE) && j.active) {
                    if (j.rotate.equals(Rotate.UP)) {
                        setActive(x, y + 1);
                        setActive(x + 1, y);
                    } else if (j.rotate.equals(Rotate.DOWN)) {
                        setActive(x, y - 1);
                        setActive(x - 1, y);
                    } else if (j.rotate.equals(Rotate.RIGHT)) {
                        setActive(x + 1, y);
                        setActive(x, y - 1);
                    } else if (j.rotate.equals(Rotate.LEFT)) {
                        setActive(x, y + 1);
                        setActive(x - 1, y);
                    }
                } else if (j.blockType.equals(BlockType.DOUBLE_WIRE_FLIP) && j.active) {
                    if (j.rotate.equals(Rotate.UP)) {
                        setActive(x, y + 1);
                        setActive(x - 1, y);
                    } else if (j.rotate.equals(Rotate.DOWN)) {
                        setActive(x, y - 1);
                        setActive(x + 1, y);
                    } else if (j.rotate.equals(Rotate.RIGHT)) {
                        setActive(x, y + 1);
                        setActive(x + 1, y);
                    } else if (j.rotate.equals(Rotate.LEFT)) {
                        setActive(x, y - 1);
                        setActive(x - 1, y);
                    }
                } else if (j.blockType.equals(BlockType.TIMER)) {
                    j.active = !j.active;
                    if (j.active)
                        setActiveHigh(j.rotate, x, y);
                } else if (j.blockType.equals(BlockType.DOUBLE_WIRE_STRAIGHT) && j.active) {
                    setActiveHigh(Rotate.getId(j.rotate.deg + 90), x, y);
                    setActiveHigh(Rotate.getId(j.rotate.deg - 90), x, y);
                }
            }
            for (long key : blocks.keySet()) {
                Block j = blocks.get(key);
                if (j.blockType == BlockType.AND) {
                    j.active = j.igot >= 2;
                } else if (j.blockType == BlockType.XOR) {
                    j.active = j.igot % 2 != 0;
                } else if (j.blockType == BlockType.NAND) {
                    j.active = j.igot < 2;
                } else if (j.blockType == BlockType.NXOR) {
                    j.active = j.igot % 2 == 0;
                } else if (j.blockType != BlockType.TIMER && j.blockType != BlockType.BTN) {
                    j.active = j.igot > 0;
                }
                j.igot = 0;
            }
            game.ticks++;
        }catch(ConcurrentModificationException ignored){

        }
    }

    void setActive(int x, int y) {
        Block b = getBlock(x, y);
        if (b != null) {
            b.igot++;
        }
    }

    void setActiveHigh(Rotate rot, int x, int y) {
        setActiveHigh(rot, x, y, 1);
    }

    void setActiveHigh(Rotate rot, int x, int y, int l) {
        if (rot.equals(Rotate.UP)) {
            setActive(x, y + l);
        } else if (rot.equals(Rotate.RIGHT)) {
            setActive(x + l, y);
        } else if (rot.equals(Rotate.DOWN)) {
            setActive(x, y - l);
        } else if (rot.equals(Rotate.LEFT)) {
            setActive(x - l, y);
        }
    }

    public ByteBuffer getShortBlockBytes(ArrayList<Coords> coords, int ox, int oy) {
        ByteBuffer buff = ByteBuffer.allocate(blocks.size() * 11);
        for (Coords coord : coords) {
            Block b = getBlock(coord.x, coord.y);
            if(b != null) {
                buff.putInt(coord.x - ox);
                buff.putInt(coord.y - oy);
                buff.put((byte) b.blockType.id);
                buff.put((byte) b.rotate.id);
                buff.put((byte) (b.active ? 1 : 0));
            }
        }
        return buff;
    }

    public void loadShortBlockBytes(ByteBuffer buffer, int ox, int oy) {
        int count = buffer.capacity() / 11;
        for (int i = 0; i < count; i++) {
            int x = buffer.getInt() + ox;
            int y = buffer.getInt() + oy;
            Block block = new Block(BlockType.getID(buffer.get()), Rotate.getId2(buffer.get()), buffer.get() == 1, game.zoom);
            block.setPosition(x, y);
            if (!blocks.containsKey(getId(x, y)))
                blocks.put(getId(x, y), block);
        }
    }

    public void saveFullBytes(String filename) throws IOException {
        File fol = new File(game.getSchemasPath());
        if (!fol.exists())
            fol.mkdirs();
        ByteBuffer buff = ByteBuffer.allocate(19 + blocks.size() * 11);
        buff.putShort((short) Config.version);
        buff.putLong(System.currentTimeMillis());
        buff.putInt(game.offsetX);
        buff.putInt(game.offsetY);
        buff.put((byte) game.zoom);
        for (long key : blocks.keySet()) {
            Block b = blocks.get(key);
            int x = (int) (key >> 32); //Get last 32 bits
            int y = (int) key; //Get first 32 bits
            buff.putInt(x);
            buff.putInt(y);
            buff.put((byte) b.blockType.id);
            buff.put((byte) b.rotate.id);
            buff.put((byte) (b.active ? 1 : 0));
        }
        FileOutputStream outputStream = new FileOutputStream(game.getSchemasPath() + File.separator + filename + ".ls");
        outputStream.write(BytesCompressor.compress(buff.array()));
        outputStream.close();
        buff.clear();
    }

    public void loadFullBytes(String filename) throws IOException {
        byte[] bytes = null;
        if (filename.endsWith(".bin")) {
            bytes = Files.readAllBytes(Paths.get(filename));
        } else if (filename.endsWith(".ls")) {
            bytes = BytesCompressor.decompress(Files.readAllBytes(Paths.get(filename)));
        }
        if (bytes == null)
            return;
        ByteBuffer buff = ByteBuffer.wrap(bytes);
        game.offsetX = buff.getInt(0xA);
        game.offsetY = buff.getInt(0xE);
        game.zoom = buff.get(0x12);
        int count = (bytes.length - 19) / 11;
        buff.position(19);
        for (int i = 0; i < count; i++) {
            int x = buff.getInt();
            int y = buff.getInt();
            Block block = new Block(BlockType.getID(buff.get()), Rotate.getId2(buff.get()), game.zoom);
            block.setPosition(x, y);
            block.active = buff.get() == 1;
            blocks.put(getId(x, y), block);
        }
        buff.clear();
    }

    public void updateZoom() {
        for (Block j : blocks.values()) {
            j.setSize(game.zoom);
        }
    }
}
