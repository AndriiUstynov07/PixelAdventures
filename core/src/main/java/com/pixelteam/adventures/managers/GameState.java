package com.pixelteam.adventures.managers;

public abstract class GameState {
    public abstract void update(float deltaTime);
    public abstract void render(SpriteBatch batch);
    public abstract void handleInput();
}
