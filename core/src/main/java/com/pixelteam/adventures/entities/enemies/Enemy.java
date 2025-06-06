package com.pixelteam.adventures.entities.enemies;

import com.pixelteam.adventures.entities.Character;

public abstract class Enemy extends Character {
    protected int moneyDrop;
    protected AI ai;

    public abstract void dropLoot();
}
