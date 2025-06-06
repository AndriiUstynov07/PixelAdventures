package com.pixelteam.adventures.weapons;

import com.badlogic.gdx.graphics.Texture;
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

    public abstract void attack(Character user, Vector2 target);
    public abstract void upgrade();

    public Texture getTexture() {
        return texture;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public abstract void dispose();
}
