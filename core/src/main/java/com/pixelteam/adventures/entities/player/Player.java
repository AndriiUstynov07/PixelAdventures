package com.pixelteam.adventures.entities.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pixelteam.adventures.entities.Character;
import com.pixelteam.adventures.items.Armor;
import com.pixelteam.adventures.utils.Stats;
import com.pixelteam.adventures.weapons.Weapon;

public class Player extends Character {
    private Inventory inventory;
    private Armor armor;
    private int experience;
    private PlayerController controller;
    private float attackCooldown;
    private boolean isAttacking;
    private float swordRotation;
    private float swordAttackAnimation;

    public Player(float x, float y) {
        position = new Vector2(x, y);
        velocity = new Vector2(0, 0);
        width = 64;
        height = 64;
        health = 100;
        maxHealth = 100;
        speed = 200;
        alive = true;
        active = true;
        money = 0;
        experience = 0;
        stats = new Stats();
        inventory = new Inventory(20); // Inventory with 20 slots
        controller = new PlayerController(this);
        attackCooldown = 0;
        isAttacking = false;
        swordRotation = 15; // 15 degrees to the right
        swordAttackAnimation = 0;
    }

    @Override
    public void update(float deltaTime) {
        controller.update();

        // Update position based on velocity
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);

        // Apply map boundaries
        // Prevent player from moving outside the screen
        if (position.x < 0) position.x = 0;
        if (position.y < 0) position.y = 0;
        if (position.x > Gdx.graphics.getWidth() - width) position.x = Gdx.graphics.getWidth() - width;
        if (position.y > Gdx.graphics.getHeight() - height) position.y = Gdx.graphics.getHeight() - height;

        // Update attack cooldown
        if (attackCooldown > 0) {
            attackCooldown -= deltaTime;
        }

        // Update sword attack animation
        if (isAttacking) {
            // Animate sword swing down by 30 degrees over 0.25 seconds
            swordAttackAnimation += 120 * deltaTime; // 30 degrees / 0.25 seconds = 120 degrees/second
            if (swordAttackAnimation > 30) {
                // Start returning to original position
                swordAttackAnimation = 30;
                isAttacking = false;
            }
        } else if (swordAttackAnimation > 0) {
            // Return sword to original position
            swordAttackAnimation -= 120 * deltaTime;
            if (swordAttackAnimation < 0) {
                swordAttackAnimation = 0;
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        // Render player
        if (texture != null) {
            batch.draw(texture, position.x, position.y, width, height);
        }

        // Render weapon if available - SIMPLIFIED VERSION
        if (currentWeapon != null && currentWeapon.getTexture() != null) {
            // Position sword in the center of the screen for maximum visibility
            float swordX = Gdx.graphics.getWidth() / 2 - currentWeapon.getWidth() / 2;
            float swordY = Gdx.graphics.getHeight() / 2 - currentWeapon.getHeight() / 2;

            // Draw the sword without rotation - super simplified to ensure visibility
            batch.draw(
                currentWeapon.getTexture(),
                swordX, swordY,
                currentWeapon.getWidth() * 2, // Make it twice as large
                currentWeapon.getHeight() * 2  // Make it twice as large
            );
        }
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(position.x, position.y, width, height);
    }

    @Override
    public void attack() {
        if (attackCooldown <= 0 && currentWeapon != null) {
            isAttacking = true;
            swordAttackAnimation = 0; // Reset animation to start from the beginning
            attackCooldown = 0.5f; // Half second cooldown

            // Call the weapon's attack method
            Vector2 target = new Vector2(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
            currentWeapon.attack(this, target);
        }
    }

    @Override
    public void move(Vector2 direction) {
        velocity.set(direction).scl(speed);
    }

    @Override
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            die();
        }
    }

    @Override
    public void die() {
        alive = false;
        // Додаткова логіка смерті гравця
    }

    public void levelUp() {
        // Логіка підвищення рівня
    }

    public void equipWeapon(Weapon weapon) {
        this.currentWeapon = weapon;
    }

    public void equipArmor(Armor armor) {
        this.armor = armor;
    }

    public PlayerController getController() {
        return controller;
    }

    public boolean isAttacking() {
        return isAttacking;
    }

    public void setAttacking(boolean attacking) {
        this.isAttacking = attacking;
    }
}
