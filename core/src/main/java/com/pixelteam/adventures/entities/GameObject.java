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

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Texture getTexture() {
        return texture;
    }
}
