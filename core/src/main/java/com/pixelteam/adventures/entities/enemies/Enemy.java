package com.pixelteam.adventures.entities.enemies;

import com.pixelteam.adventures.entities.Character;
import com.pixelteam.adventures.utils.AI;

public abstract class Enemy extends Character {
    protected int moneyDrop;
    protected AI ai;

    public abstract void dropLoot();
}
