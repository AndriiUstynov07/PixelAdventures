package com.pixelteam.adventures.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class GameObject {
    protected Vector2 position;
    protected Vector2 velocity;
    protected float width, height;
    protected boolean active;
    protected Texture texture;

    public abstract void update(float deltaTime);
    public abstract void render(SpriteBatch batch);
    public abstract Rectangle getBounds();

    // Getters and setters
    public Vector2 getPosition() { return position; }
    public void setPosition(Vector2 position) { this.position = position; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public void setTexture(Texture playerTexture) {
        this.texture = playerTexture;
    }
    public Texture getTexture() { return texture; }

    public void dispose() {
    }
}
