package com.kewldan.tutorials;

import com.badlogic.gdx.files.FileHandle;
import com.kewldan.logical.LogicalSystem;

import java.util.ArrayList;

public class Tutorial {
    ArrayList<TutorialAsset> assets;
    public int maxScroll = Integer.MIN_VALUE;

    Tutorial(FileHandle file){
        assets = new ArrayList<>();
        String[] lines = file.readString("utf-8").split("\n");
        int offset = 0;
        for (String l : lines) {
            TutorialAsset asset = null;
            if (l.startsWith("###")) {
                asset = new UnderHeader(l.substring(3).trim(), offset);
            } else if (l.startsWith("##")) {
                asset = new Header(l.substring(2).trim(), offset);
            } else if (l.startsWith("-")) {
                asset = new Text(l.substring(1).trim(), offset);
            } else if (l.startsWith("IMG")) {
                String info = l.substring(3).trim();
                String[] data = info.split(",");
                for (int i1 = 0; i1 < data.length; i1++) {
                    data[i1] = data[i1].trim();
                }
                if (data.length == 5) {
                    asset = new Image(data[0], Integer.parseInt(data[1]), Integer.parseInt(data[2]), Integer.parseInt(data[3]), Integer.parseInt(data[4]), offset);
                } else if (data.length == 3) {
                    asset = new Image(data[0], Integer.parseInt(data[1]), Integer.parseInt(data[2]), offset);
                }
            }else if(l.startsWith("$")){
                String[] data = l.substring(1).trim().split("=");
                if(data.length == 2){
                    if(data[0].trim().equals("max")){
                        maxScroll = Integer.parseInt(data[1].trim());
                    }else{
                        //logger.error("Unknown parameter in file \"" + file.name() + "\": \"" + data[0].trim() + "\"");
                    }
                }
            }
            if (asset != null) {
                assets.add(asset);
                offset += asset.getHeight();
            }
        }
    }

    public void draw(LogicalSystem game, int scroll){
        for(TutorialAsset asset : assets){
            asset.render(game, scroll);
        }
        if(game.shapes.isDrawing()) game.shapes.end();
    }
}
