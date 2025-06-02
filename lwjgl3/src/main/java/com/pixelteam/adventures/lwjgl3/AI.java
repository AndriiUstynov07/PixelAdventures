package com.pixelteam.adventures.lwjgl3;

public abstract class AI {
    protected Enemy owner;

    public abstract void update(float deltaTime, Player target);
    public abstract Vector2 getNextMove();
}
