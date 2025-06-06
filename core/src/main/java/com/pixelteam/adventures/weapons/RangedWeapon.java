package com.pixelteam.adventures.weapons;

import com.pixelteam.adventures.entities.Character;
import com.pixelteam.adventures.entities.projectiles.Projectile;

public class RangedWeapon extends Weapon {
    protected int ammunition;
    protected float projectileSpeed;

    @Override
    public void attack(Character user, Vector2 target) {
        // Створення снаряду
    }

    public Projectile createProjectile(Vector2 position, Vector2 direction);
}
