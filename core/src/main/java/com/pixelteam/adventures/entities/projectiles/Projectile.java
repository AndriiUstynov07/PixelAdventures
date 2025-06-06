package com.pixelteam.adventures.entities.projectiles;

import com.pixelteam.adventures.entities.Character;
import com.pixelteam.adventures.entities.Entity;
import com.pixelteam.adventures.entities.GameObject;

public class Projectile extends GameObject {
    protected int damage;
    protected Character owner;
    protected float lifeTime;

    public void onHit(Entity target);
}
