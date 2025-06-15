package com.pixelteam.adventures.weapons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.pixelteam.adventures.entities.Character;

public abstract class Weapon {
    protected String name;
    protected int damage;
    protected float attackSpeed;
    protected int level;
    protected WeaponType type;
    protected Texture texture;
    protected float width;
    protected float height;
    protected float scale = 1.0f;

    public abstract void attack(Character user, Vector2 target);
    public abstract void upgrade();

    public void setScale(float scale) {
        this.scale = scale;
        this.width = 30f * scale;  // Базовий розмір зброї
        this.height = 48f * scale; // Базовий розмір зброї
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public abstract void dispose();

    public Texture getTexture() {
        return texture;
    }

    public int getDamage() {
        return damage;
    }

    public void render(SpriteBatch batch, float x, float y, float rotation) {
        if (texture != null) {
            batch.draw(texture,
                x, y,
                width / 2.0f, height / 2.0f,
                width, height,
                1.0f, 1.0f,
                rotation,
                0, 0,
                texture.getWidth(), texture.getHeight(),
                false, false);
        }
    }
}
