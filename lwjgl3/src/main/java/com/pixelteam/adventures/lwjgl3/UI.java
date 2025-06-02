package com.pixelteam.adventures.lwjgl3;

public class UI {
    protected Stage stage;
    protected Skin skin;

    public abstract void render();
    public abstract void update(float deltaTime);
}
