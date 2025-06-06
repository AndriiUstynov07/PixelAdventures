package com.pixelteam.adventures.weapons;

import com.pixelteam.adventures.entities.Character;

public abstract class Weapon {
    protected String name;
    protected int damage;
    protected float attackSpeed;
    protected int level;
    protected WeaponType type;

    public abstract void attack(Character user, Vector2 target);
    public abstract void upgrade();
}
