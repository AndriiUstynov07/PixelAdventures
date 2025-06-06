package com.pixelteam.adventures.utils;

import com.badlogic.gdx.math.Vector2;
import com.pixelteam.adventures.entities.enemies.Enemy;
import com.pixelteam.adventures.entities.player.Player;

public abstract class AI {
    protected Enemy owner;

    public abstract void update(float deltaTime, Player target);
    public abstract Vector2 getNextMove();
}
