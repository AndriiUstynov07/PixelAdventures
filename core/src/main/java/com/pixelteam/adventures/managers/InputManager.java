package com.pixelteam.adventures.managers;

import com.pixelteam.adventures.entities.player.Player;

public class InputManager {
    public boolean isKeyPressed(int keycode);
    public Vector2 getMousePosition();
    public void handleInput(Player player);
}
