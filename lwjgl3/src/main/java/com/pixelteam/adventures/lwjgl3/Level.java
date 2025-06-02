package com.pixelteam.adventures.lwjgl3;

public class Level {
    private List<Enemy> enemies;
    private List<Boss> bosses;
    private List<MiniBoss> miniBosses;
    private TiledMap map;
    private List<GameObject> objects;

    public void spawnEnemies();
    public void checkWinCondition();
    public boolean isCompleted();
}
