package com.pixelteam.adventures.entities;

import com.badlogic.gdx.math.Vector2;
import com.pixelteam.adventures.utils.Stats;
import com.pixelteam.adventures.weapons.Weapon;

public abstract class Character extends Entity {
    protected Weapon currentWeapon;
    protected int money;
    protected Stats stats;

    public abstract void attack();
    public abstract void move(Vector2 direction);
}
