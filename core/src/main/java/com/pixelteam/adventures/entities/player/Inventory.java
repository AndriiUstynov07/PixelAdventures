package com.pixelteam.adventures.entities.player;

public class Inventory {
    private List<Item> items;
    private int maxSize;

    public boolean addItem(Item item);
    public void removeItem(Item item);
    public List<Item> getItems();
}
