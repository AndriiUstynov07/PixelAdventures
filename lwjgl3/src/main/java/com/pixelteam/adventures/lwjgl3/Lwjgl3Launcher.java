// desktop/src/com/pixelteam/adventures/DesktopLauncher.java
package com.pixelteam.adventures.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.pixelteam.adventures.PixelAdventuresGame;

public class Lwjgl3Launcher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Pixel Adventures");
        config.setWindowedMode(1280, 720);
        config.setForegroundFPS(60);
        config.setResizable(true);

        new Lwjgl3Application(new PixelAdventuresGame(), config);
    }
}
