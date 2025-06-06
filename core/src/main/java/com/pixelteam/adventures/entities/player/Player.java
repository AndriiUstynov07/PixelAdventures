package com.pixelteam.adventures.entities.player;

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
    }

    @Override
    public void update(float deltaTime) {
        controller.update();

        // Update position based on velocity
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);

        // Update attack cooldown
        if (attackCooldown > 0) {
            attackCooldown -= deltaTime;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (texture != null) {
            batch.draw(texture, position.x, position.y, width, height);
        }

        // Render weapon if attacking
        if (isAttacking && currentWeapon != null && currentWeapon.getTexture() != null) {
            // Render weapon in front of player
            batch.draw(currentWeapon.getTexture(),
                      position.x + width/2,
                      position.y + height/2,
                      currentWeapon.getWidth(),
                      currentWeapon.getHeight());
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
            attackCooldown = 0.5f; // Half second cooldown
            // Attack logic will be implemented in weapon classes
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
