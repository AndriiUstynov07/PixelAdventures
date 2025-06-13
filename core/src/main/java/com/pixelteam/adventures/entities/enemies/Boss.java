package com.pixelteam.adventures.entities.enemies;

import com.pixelteam.adventures.entities.player.Player;
import com.pixelteam.adventures.weapons.MeleeWeapon;
import com.pixelteam.adventures.weapons.Weapon;

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

    public Weapon getWeapon() {
        return currentWeapon;
    }

    public boolean isFacingLeft() {

        return false;
    }

    public float getHeight() {
        return 0;
    }

    public float getWidth() {
        return 0;
    }

    public void setTarget(Player player) {
    }

    public void equipWeapon(MeleeWeapon weapon) {
    }
}
