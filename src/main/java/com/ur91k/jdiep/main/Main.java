package com.ur91k.jdiep.main;

import com.ur91k.jdiep.engine.Engine;

public class Main {
    public static void main(String[] args) {
        Engine engine = new Engine("JDiep", 1280, 720);
        engine.start();
    }
}
