package com.pixelteam.adventures.entities.player;

import com.pixelteam.adventures.items.Item;
import com.pixelteam.adventures.weapons.Weapon;
import com.pixelteam.adventures.items.Armor;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List<Item> items;
    private List<Weapon> weapons;
    private List<Armor> armors;
    private int maxSize;

    public Inventory(int maxSize) {
        this.maxSize = maxSize;
        this.items = new ArrayList<>();
        this.weapons = new ArrayList<>();
        this.armors = new ArrayList<>();
    }

    public boolean addItem(Item item) {
        if (items.size() + weapons.size() + armors.size() < maxSize) {
            items.add(item);
            return true;
        }
        return false;
    }

    public boolean addWeapon(Weapon weapon) {
        if (items.size() + weapons.size() + armors.size() < maxSize) {
            weapons.add(weapon);
            return true;
        }
        return false;
    }

    public boolean addArmor(Armor armor) {
        if (items.size() + weapons.size() + armors.size() < maxSize) {
            armors.add(armor);
            return true;
        }
        return false;
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public void removeWeapon(Weapon weapon) {
        weapons.remove(weapon);
    }

    public void removeArmor(Armor armor) {
        armors.remove(armor);
    }

    public List<Item> getItems() {
        return items;
    }

    public List<Weapon> getWeapons() {
        return weapons;
    }

    public List<Armor> getArmors() {
        return armors;
    }

    public int getCurrentSize() {
        return items.size() + weapons.size() + armors.size();
    }

    public int getMaxSize() {
        return maxSize;
    }
}
