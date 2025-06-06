package com.pixelteam.adventures.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.pixelteam.adventures.entities.Character;

public class MeleeWeapon extends Weapon {
    private float range;
    private float swingAngle;

    public MeleeWeapon(String name, int damage, float attackSpeed, String texturePath) {
        this.name = name;
        this.damage = damage;
        this.attackSpeed = attackSpeed;
        this.level = 1;
        this.type = WeaponType.MELEE;

        // Try to load the texture from the specified path
        try {
            if (Gdx.files.internal(texturePath).exists()) {
                this.texture = new Texture(Gdx.files.internal(texturePath));
            } else {
                // Try alternative paths
                String[] alternativePaths = {
                    "sword.png",
                    "images/weapons/sword.png",
                    "lwjgl3/build/resources/main/images/weapons/sword.png",
                    "assets/images/weapons/sword.png"
                };

                for (String path : alternativePaths) {
                    if (Gdx.files.internal(path).exists()) {
                        this.texture = new Texture(Gdx.files.internal(path));
                        break;
                    }
                }
            }
        } catch (Exception e) {
            // Silently handle the exception
        }
        this.width = 64;
        this.height = 64;
        this.range = 100;
        this.swingAngle = 90; // Degrees
    }

    @Override
    public void attack(Character user, Vector2 target) {
        // Implement melee attack logic
        // For now, just a placeholder
    }

    @Override
    public void upgrade() {
        level++;
        damage += 5;
        attackSpeed += 0.1f;
    }

    public float getRange() {
        return range;
    }

    public float getSwingAngle() {
        return swingAngle;
    }

    @Override
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
