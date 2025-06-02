package com.pixelteam.adventures.lwjgl3;

public abstract class Enemy extends Character {
    protected int moneyDrop;
    protected AI ai;

    public abstract void dropLoot();
}
