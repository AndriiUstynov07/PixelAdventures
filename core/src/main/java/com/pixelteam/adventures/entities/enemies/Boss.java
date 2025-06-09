package com.pixelteam.adventures.entities.enemies;

public abstract class Boss extends Enemy {
    //protected List<BossAbility> abilities;
    protected int phase;

    public void useAbility() {

    }

    public void changePhase() {

    }

    public boolean isAlive() {
        return this.alive;
    }
}
