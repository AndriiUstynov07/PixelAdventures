package com.pixelteam.adventures.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.pixelteam.adventures.entities.Character;

public class MeleeWeapon extends Weapon {
    private float range;
    private float swingAngle;
    private boolean attacking;
    private float attackRotation;
    private float attackTimer;

    public MeleeWeapon(String name, int damage, float attackSpeed, String texturePath) {
        this.name = name;
        this.damage = damage;
        this.attackSpeed = attackSpeed;
        this.level = 1;
        this.type = WeaponType.MELEE;
        this.attacking = false;
        this.attackRotation = 0f;
        this.attackTimer = 0f;

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

        // Set dimensions based on loaded texture, or use defaults
        if (this.texture != null) {

            this.width = 30;
            this.height = 48;
        }

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

    /**
     * Updates the weapon state
     * @param deltaTime Time since last update
     */
    public void update(float deltaTime) {
        if (attacking) {
            // Update attack animation
            attackTimer += deltaTime;

            // Calculate rotation based on attack progress
            float attackDuration = 0.3f; // 300ms for full attack
            float progress = Math.min(attackTimer / attackDuration, 1.0f);

            // Swing from -30 to 90 degrees during attack
            attackRotation = -30f + progress * 120f;

            // End attack after duration
            if (attackTimer >= attackDuration) {
                attacking = false;
                attackTimer = 0f;
                attackRotation = 0f;
            }
        }
    }

    /**
     * Starts a weapon attack
     */
    public void startAttack() {
        attacking = true;
        attackTimer = 0f;
    }

    /**
     * Checks if weapon is currently attacking
     * @return true if attacking, false otherwise
     */
    public boolean isAttacking() {
        return attacking;
    }

    /**
     * Gets the current attack rotation angle
     * @return rotation angle in degrees
     */
    public float getAttackRotation() {
        return attackRotation;
    }
}
