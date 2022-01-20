package com.kewldan.localization;

import com.badlogic.gdx.files.FileHandle;

import java.io.BufferedReader;
import java.util.Properties;

public class LanguagePacket extends Properties {
    public String name;
    public String code;

    public LanguagePacket(FileHandle stream, String code) {
        this.code = code;
        BufferedReader result = stream.reader(1024, "cp1251");
        for (Object o : result.lines().toArray()) {
            String line = (String) o;
            if (line != null) {
                String[] pair = line.split("=");
                if (pair.length == 2) {
                    put(pair[0].trim(), pair[1].trim());
                }
            }
        }
        this.name = getString("package.name");
    }

    public String getString(String eng) {
        return (String) getOrDefault(eng, eng);
    }
}
