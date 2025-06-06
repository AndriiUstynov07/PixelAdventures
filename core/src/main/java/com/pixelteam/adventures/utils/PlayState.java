package com.pixelteam.adventures.utils;

import com.badlogic.gdx.graphics.Camera;
import com.pixelteam.adventures.entities.player.Player;
import com.pixelteam.adventures.managers.GameState;

public abstract class PlayState extends GameState {
    private Level currentLevel;
    private Player player;
    private Camera camera;
    private UI gameUI;
}
