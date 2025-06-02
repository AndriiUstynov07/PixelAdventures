package com.pixelteam.adventures.lwjgl3;

public abstract class Item {
    protected String name;
    protected String description;
    protected int price;
    protected Texture icon;

    public abstract void use(Player player);
}
