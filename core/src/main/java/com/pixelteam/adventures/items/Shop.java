package com.pixelteam.adventures.items;

import com.pixelteam.adventures.entities.player.Player;

import java.util.List;

public class Shop {
    private List<Item> weapons;
    private List<Item> armor;
    private List<Item> consumables;

    public boolean buyItem(Item item, Player player) {
        return false;
    }

    public void restockItems() {

    }
}
