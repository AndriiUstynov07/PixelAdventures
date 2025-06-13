package com.pixelteam.adventures.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Simple trap that deals damage when a player steps on it.
 */
public class Trap extends GameObject {
    private final int damage;

    public Trap(float x, float y, float size, int damage, Texture texture) {
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
        this.width = size;
        this.height = size;
        this.damage = damage;
        this.texture = texture;
        this.active = true;
    }

    @Override
    public void update(float deltaTime) {
        // Traps have no behaviour
    }

    @Override
    public void render(SpriteBatch batch) {
        if (texture != null && active) {
            batch.draw(texture, position.x, position.y, width, height);
        }
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(position.x, position.y, width, height);
    }

    public int getDamage() {
        return damage;
    }

    /**
     * Deactivates the trap after it is triggered.
     */
    public void trigger() {
        this.active = false;
    }
}
