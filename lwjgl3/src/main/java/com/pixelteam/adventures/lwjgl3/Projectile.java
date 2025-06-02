package com.pixelteam.adventures.lwjgl3;

public class Projectile extends GameObject {
    protected int damage;
    protected Character owner;
    protected float lifeTime;

    public void onHit(Entity target);
}
