package com.kewldan.multiplayer.packets;

import com.kewldan.blocks.Block;
import com.kewldan.blocks.BlockType;
import com.kewldan.blocks.Rotate;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ResponseFLL extends SimplePacket {
    public List<Block> blocks;

    public static final byte v = 0x9;

    public ResponseFLL(ByteBuffer src) {
        blocks = new ArrayList<>();
        int count = src.getInt(1);
        int offset = 5;
        for (int i = 0; i < count; i++) {
            Block h = new Block(
                    BlockType.getID(src.get(offset + 8)),
                    Rotate.getId2(src.get(offset + 9)),
                    src.get(offset + 10) == 1,
                    -1);
            h.setPosition(src.getInt(offset), src.getInt(offset + 4));
            blocks.add(h);
            offset += 11;
        }
        type = v;
    }

    public ResponseFLL(List<Block> blocks) {
        this.blocks = blocks;
        type = v;
    }

    @Override
    public void getBytes(ByteBuffer src) {
        src.putInt(5, blocks.size());
        int offset = 9;
        for (Block b : blocks) {
            src.putInt(offset, b.x);
            src.putInt(offset + 4, b.y);
            src.put(offset + 8, (byte) b.blockType.id);
            src.put(offset + 9, (byte) b.rotate.id);
            src.put(offset + 10, (byte) (b.active ? 1 : 0));
            offset += 11;
        }
    }

    @Override
    public int getLength() {
        return blocks.size() * 11 + 4;
    }
}

