package com.pixelteam.adventures.lwjgl3;

public class Boss extends Enemy {
    protected List<BossAbility> abilities;
    protected int phase;

    public void useAbility();
    public void changePhase();
}
