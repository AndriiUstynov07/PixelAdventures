package com.pixelteam.adventures.weapons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.pixelteam.adventures.entities.Character;
import com.pixelteam.adventures.entities.GameObject;

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



    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public abstract void dispose();

    public Texture getTexture() {
        return texture;
    }

    public int getDamage() {
        return damage;
    }
}
