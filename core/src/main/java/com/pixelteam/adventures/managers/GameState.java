package com.pixelteam.adventures.managers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class GameState {
    public abstract void update(float deltaTime);
    public abstract void render(SpriteBatch batch);
    public abstract void handleInput();
}
