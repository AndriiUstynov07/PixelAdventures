package com.pixelteam.adventures.utils;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.pixelteam.adventures.entities.GameObject;
import com.pixelteam.adventures.entities.enemies.Boss;
import com.pixelteam.adventures.entities.enemies.Enemy;
//import com.pixelteam.adventures.entities.enemies.MiniBoss;

import java.util.List;

public class Level {
    private List<Enemy> enemies;
    private List<Boss> bosses;
    //private List<MiniBoss> miniBosses;
    private TiledMap map;
    private List<GameObject> objects;

    public void spawnEnemies() {

    }

    public void checkWinCondition() {

    }

    public boolean isCompleted() {
        return false;
    }
}
