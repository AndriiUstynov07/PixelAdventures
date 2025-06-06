package com.pixelteam.adventures.entities.enemies;

public class Boss extends Enemy {
    protected List<BossAbility> abilities;
    protected int phase;

    public void useAbility();
    public void changePhase();
}
