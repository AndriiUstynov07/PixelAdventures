package com.pixelteam.adventures.lwjgl3;

public class Armor extends Item {
    protected int defense;
    protected int healthBonus;

    @Override
    public void use(Player player) {
        player.equipArmor(this);
    }
}
