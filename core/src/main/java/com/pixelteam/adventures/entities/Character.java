package com.pixelteam.adventures.entities;

import com.badlogic.gdx.math.Vector2;

public abstract class Character extends Entity {
    protected Weapon currentWeapon;
    protected int money;
    protected Stats stats;

    public abstract void attack();
    public abstract void move(Vector2 direction);
}
