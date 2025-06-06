package com.pixelteam.adventures.items;

import com.pixelteam.adventures.entities.player.Player;

public class Consumable extends Item {
    protected int healAmount;

    @Override
    public void use(Player player) {
        player.heal(healAmount);
    }
}
