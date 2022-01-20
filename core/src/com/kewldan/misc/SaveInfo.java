package com.kewldan.misc;

import com.kewldan.logical.LogicalSystem;

public class SaveInfo {
    public String version;
    public int versionCode;
    public String name, time, path, weight;
    public int count;

    public SaveInfo(String version, int versionCode, String name, String time, String path, String weight, int count) {
        this.version = version;
        this.versionCode = versionCode;
        this.name = name;
        this.time = time;
        this.path = path;
        this.weight = weight;
        this.count = count;
    }

    @Override
    public String toString() {
        return name + " | " + version + " | " + time + " | " + count + " " + LogicalSystem.instance.packet.getString("saves.blocks");
    }
}
