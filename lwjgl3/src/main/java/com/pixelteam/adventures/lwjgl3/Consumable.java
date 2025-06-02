package com.pixelteam.adventures.lwjgl3;

public class Consumable extends Item {
    protected int healAmount;

    @Override
    public void use(Player player) {
        player.heal(healAmount);
    }
}
