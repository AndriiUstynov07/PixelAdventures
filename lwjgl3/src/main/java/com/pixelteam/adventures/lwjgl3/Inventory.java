package com.pixelteam.adventures.lwjgl3;

public class Inventory {
    private List<Item> items;
    private int maxSize;

    public boolean addItem(Item item);
    public void removeItem(Item item);
    public List<Item> getItems();
}
