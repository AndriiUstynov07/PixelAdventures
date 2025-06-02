package com.pixelteam.adventures.lwjgl3;

public abstract class GameState {
    public abstract void update(float deltaTime);
    public abstract void render(SpriteBatch batch);
    public abstract void handleInput();
}
