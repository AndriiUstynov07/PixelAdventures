package com.pixelteam.adventures.items;

import com.pixelteam.adventures.entities.player.Player;

public class Armor extends Item {
    protected int defense;
    protected int healthBonus;

    @Override
    public void use(Player player) {
        player.equipArmor(this);
    }
}
