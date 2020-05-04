package game;

import engine.IGameLogic;
import engine.LwjglEngine;

public class Main {

    public static void main(String[] args) {
        IGameLogic gameLogic = new EngineTester();
//        LwjglEngine gameEng = new LwjglEngine("Lwjgl Engine", 1280, 720, 1, gameLogic);
        LwjglEngine gameEng = new LwjglEngine("Lwjgl Engine", 1920, 1080, 16, gameLogic);
//        LwjglEngine gameEng = new LwjglEngine("Lwjgl Engine", 2560, 1440, 1, gameLogic);
        gameEng.run();
    }
}
