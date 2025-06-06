package com.pixelteam.adventures.items;

import com.pixelteam.adventures.entities.player.Player;

public abstract class Item {
    protected String name;
    protected String description;
    protected int price;
    protected Texture icon;

    public abstract void use(Player player);
}
