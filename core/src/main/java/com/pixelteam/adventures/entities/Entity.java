package com.pixelteam.adventures.entities;

public abstract class Entity extends GameObject {
    protected int health;
    protected int maxHealth;
    protected float speed;
    protected boolean alive;

    public abstract void takeDamage(int damage);
    public abstract void die();
}
