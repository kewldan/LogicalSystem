package com.kewldan.tutorials;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.kewldan.logical.LogicalSystem;

import java.util.ArrayList;

public class TutorialMaster {
    ArrayList<Tutorial> tutorials;
    int selected;
    public int scroll;

    public static final String[] available = {
            "control",
            "elements"
    };

    public TutorialMaster() {
        tutorials = new ArrayList<>();
    }

    public void loadTutorials() {
        for (String s : available) {
            try {
                FileHandle handle = Gdx.files.internal("Tutorials/" + s + "." + LogicalSystem.instance.packet.code + ".md");
                tutorials.add(new Tutorial(handle));
            }catch (GdxRuntimeException e){
                FileHandle handle = Gdx.files.internal("Tutorials/" + s + ".en" + ".md");
                tutorials.add(new Tutorial(handle));
            }
        }
    }


    public void drawSelected() {
        tutorials.get(selected).draw(LogicalSystem.instance, scroll);
    }

    public Tutorial getSelected(){
        return tutorials.get(selected);
    }
}
