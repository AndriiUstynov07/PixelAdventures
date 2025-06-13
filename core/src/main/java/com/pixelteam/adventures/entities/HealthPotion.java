package com.pixelteam.adventures.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class HealthPotion extends GameObject {
    private final int healAmount;

    public HealthPotion(float x, float y, float size, int healAmount, Texture texture) {
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
        this.width = (float) (size*0.7);
        this.height = size;
        this.healAmount = healAmount;
        this.texture = texture;
        this.active = true;
    }

    @Override
    public void update(float deltaTime) {
        // Potions have no behaviour
    }

    @Override
    public void render(SpriteBatch batch) {
        if (active && texture != null) {
            batch.draw(texture, position.x, position.y, width, height);
        }
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(position.x, position.y, width, height);
    }

    public int getHealAmount() {
        return healAmount;
    }
}
