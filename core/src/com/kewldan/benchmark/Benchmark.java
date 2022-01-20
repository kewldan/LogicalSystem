package com.kewldan.benchmark;

import java.util.HashMap;

public class Benchmark extends HashMap<String, Info> {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public void add(String name) {
        Info info;
        if (containsKey(name)) {
            info = get(name);
        } else {
            info = new Info();
            put(name, info);
        }

        info.begin = System.nanoTime();
        info.samples++;
    }

    public void end(String name) {
        Info info = get(name);
        info.time += System.nanoTime() - info.begin;
    }

    public void printTable() {
        String leftAlignFormat = "| %-10s | %-7d | %-13d | %-13f | %-4s |%n" + ANSI_RESET;

        System.out.format("+------------+---------+---------------+---------------+------+%n");
        System.out.format("| NAME       | SAMPLES | CPU TIME (ms) | AVG TIME (ms) | %%    |%n");
        System.out.format("+------------+---------+---------------+---------------+------+%n");

        long allCpu = 0;
        for (String k : keySet()) {
            Info i = get(k);
            allCpu += Math.round(i.time / 100000f);
        }
        for (String k : keySet()) {
            Info i = get(k);
            String color = ANSI_WHITE;
            int percent = Math.round(Math.round(i.time / 100000f) / (float) allCpu * 100f);
            if (percent > 85) {
                color = ANSI_RED;
            } else if (percent > 50) {
                color = ANSI_GREEN;
            }
            System.out.format(color + leftAlignFormat, k, i.samples, Math.round(i.time / 100000f), i.time / (float) i.samples / 1000000f, percent + "%");
        }
        System.out.format("+------------+---------+---------------+---------------+------+%n");
    }
}
