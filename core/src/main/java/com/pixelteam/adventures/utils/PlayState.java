package com.pixelteam.adventures.utils;

import com.pixelteam.adventures.entities.player.Player;
import com.pixelteam.adventures.managers.GameState;

public class PlayState extends GameState {
    private Level currentLevel;
    private Player player;
    private Camera camera;
    private UI gameUI;
}
