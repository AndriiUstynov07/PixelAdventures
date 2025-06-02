package com.pixelteam.adventures.lwjgl3;

public class RangedWeapon extends Weapon {
    protected int ammunition;
    protected float projectileSpeed;

    @Override
    public void attack(Character user, Vector2 target) {
        // Створення снаряду
    }

    public Projectile createProjectile(Vector2 position, Vector2 direction);
}
