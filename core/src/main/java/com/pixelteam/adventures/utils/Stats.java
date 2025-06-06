package com.pixelteam.adventures.utils;

public class Stats {
    private int attack;
    private int defense;
    private int speed;
    private int health;

    public void addBonus(Stats bonus) {
        this.attack += bonus.attack;
        this.defense += bonus.defense;
        this.speed += bonus.speed;
        this.health += bonus.health;
    }
}
