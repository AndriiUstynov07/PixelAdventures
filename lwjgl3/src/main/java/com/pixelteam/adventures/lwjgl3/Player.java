package com.pixelteam.adventures.lwjgl3;

import com.badlogic.gdx.math.Vector2;

public class Player extends Character {
    private Inventory inventory;
    private Armor armor;
    private int experience;
    private PlayerController controller;

    @Override
    public void attack() { /* реалізація атаки гравця */ }
    @Override
    public void move(Vector2 direction) { /* рух гравця */ }

    public void levelUp();
    public void equipWeapon(Weapon weapon);
    public void equipArmor(Armor armor);
}
