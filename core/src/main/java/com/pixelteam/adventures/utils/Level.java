package com.pixelteam.adventures.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.pixelteam.adventures.entities.GameObject;
import com.pixelteam.adventures.entities.enemies.Boss;
import com.pixelteam.adventures.entities.enemies.Enemy;
import com.pixelteam.adventures.entities.enemies.DragonBoss;
import com.pixelteam.adventures.entities.enemies.MiniBoss;
import com.pixelteam.adventures.entities.player.Player;
import com.pixelteam.adventures.entities.Trap;
import com.pixelteam.adventures.weapons.MeleeWeapon;

import java.util.ArrayList;
import java.util.List;

// Расширенный класс Level
public class Level {
    private int levelNumber;
    private String levelName;
    private List<Enemy> enemies;
    private List<Boss> bosses;
    private TiledMap map;
    private List<GameObject> objects;
    private List<Trap> traps;

    // Позиции спавна
    private Vector2 playerSpawnPoint;
    private Vector2 portalPosition;
    private boolean portalActive;

    // Ресурсы уровня
    private Texture backgroundTexture;
    private String backgroundPath;

    // Состояние уровня
    private boolean completed;
    private boolean initialized;
    private LevelType levelType;

    public enum LevelType {
        DUNGEON,
        FOREST,
        CAVE,
        CASTLE,
        FINAL_BOSS
    }

    public Level(int levelNumber, String levelName, LevelType type) {
        this.levelNumber = levelNumber;
        this.levelName = levelName;
        this.levelType = type;
        this.enemies = new ArrayList<>();
        this.bosses = new ArrayList<>();
        this.objects = new ArrayList<>();
        this.traps = new ArrayList<>();
        this.completed = false;
        this.initialized = false;
        this.portalActive = false;
    }

    // Инициализация уровня с конкретными параметрами
    public void initialize(String backgroundPath, Vector2 playerSpawn, Vector2 portalPos) {
        this.backgroundPath = backgroundPath;
        this.playerSpawnPoint = playerSpawn;
        this.portalPosition = portalPos;

        // Загрузка фонового изображения
        if (backgroundPath != null) {
            backgroundTexture = new Texture(backgroundPath);
        }

        // Спавн врагов и объектов
        spawnEnemies();
        spawnTraps();
        spawnObjects();

        this.initialized = true;
    }


    public void spawnEnemies() {
        enemies.clear();
        bosses.clear();

        switch (levelNumber) {
            case 1:
                spawnLevel1Enemies();
                break;
            case 2:
                spawnLevel2Enemies();
                break;
            case 3:
                spawnLevel3Enemies();
                break;
            default:
                spawnLevel1Enemies();
        }
    }

    private void spawnLevel1Enemies() {
        // Главный босс - Дракон
        DragonBoss dragonBoss = new DragonBoss(835f + (310f - 128f) / 2, 260f + (165f - 128f) / 2);
        bosses.add(dragonBoss);

        // Мини-босс
        MiniBoss miniBoss = new MiniBoss(385f + (360f - 96f) / 2, 210f + (215f - 96f) / 2);
        bosses.add(miniBoss);

        // Можно добавить обычных врагов
        // enemies.add(new Goblin(100, 100));
        // enemies.add(new Orc(200, 150));
    }

    private void spawnLevel2Enemies() {
        // Новые типы врагов для уровня 2
        // IceGolem iceGolem = new IceGolem(900f, 400f);
        // bosses.add(iceGolem);

        // Временно используем существующие классы
        DragonBoss iceBoss = new DragonBoss(900f, 400f);
        bosses.add(iceBoss);

        MiniBoss iceMiniBoss = new MiniBoss(500f, 250f);
        bosses.add(iceMiniBoss);

        // Добавляем больше обычных врагов
        // enemies.add(new IceSpider(300, 200));
        // enemies.add(new FrostWolf(600, 300));
    }

    private void spawnLevel3Enemies() {
        // Финальный босс
        // FinalBoss finalBoss = new FinalBoss(800f, 500f);
        // bosses.add(finalBoss);

        // Временно используем DragonBoss с увеличенным здоровьем
        DragonBoss finalBoss = new DragonBoss(800f, 500f);
        // finalBoss.setHealth(finalBoss.getHealth() * 2); // Удваиваем здоровье
        bosses.add(finalBoss);

        // Множественные мини-боссы
        MiniBoss shadowKnight1 = new MiniBoss(400f, 350f);
        MiniBoss shadowKnight2 = new MiniBoss(600f, 200f);
        bosses.add(shadowKnight1);
        bosses.add(shadowKnight2);
    }

    private void spawnTraps() {
        traps.clear();

        switch (levelNumber) {
            case 1:
                spawnLevel1Traps();
                break;
            case 2:
                spawnLevel2Traps();
                break;
            case 3:
                spawnLevel3Traps();
                break;
        }
    }

    private void spawnLevel1Traps() {
        // Ловушки из вашего оригинального кода
        float trapSize = 48f;
        float room2X = 385f;
        float room2Y = 210f;
        float room2Width = 205f;
        float room2Height = 230f;

        // Потребуется текстура ловушки
        // Texture trapTexture = new Texture("images/other/trap.png");

        // traps.add(new Trap(room2X+trapSize, room2Y, trapSize, 70, trapTexture));
        // traps.add(new Trap(room2X + room2Width, room2Y, trapSize, 70, trapTexture));
        // ... остальные ловушки
    }

    private void spawnLevel2Traps() {
        // Ледяные ловушки
        // traps.add(new IceTrap(200f, 200f, 48f, 100));
        // traps.add(new IceTrap(600f, 300f, 48f, 100));
    }

    private void spawnLevel3Traps() {
        // Смертельные ловушки финального уровня
        // traps.add(new DeathTrap(300f, 250f, 48f, 200));
        // traps.add(new DeathTrap(700f, 200f, 48f, 200));
    }

    private void spawnObjects() {
        objects.clear();

        // Добавляем объекты окружения
        switch (levelNumber) {
            case 1:
                // objects.add(new Chest(150, 300));
                // objects.add(new Barrel(400, 200));
                break;
            case 2:
                // objects.add(new IceCrystal(250, 350));
                break;
            case 3:
                // objects.add(new AncientRelic(500, 400));
                break;
        }
    }

    public void update(float deltaTime, Player player) {
        if (!initialized) return;

        // Обновляем всех врагов
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                enemy.update(deltaTime);
            }
        }

        // Обновляем всех боссов
        for (Boss boss : bosses) {
            if (boss.isAlive()) {
                boss.update(deltaTime);
            }
        }

        // Обновляем объекты
        for (GameObject obj : objects) {
            obj.update(deltaTime);
        }

        // Проверяем условие победы
        checkWinCondition();
    }

    public void render(SpriteBatch batch) {
        if (!initialized) return;

        // Рендерим фон
        if (backgroundTexture != null) {
            batch.draw(backgroundTexture, 0, 0, 1280f, 720f);
        }

        // Рендерим ловушки
        for (Trap trap : traps) {
            trap.render(batch);
        }

        // Рендерим объекты
        for (GameObject obj : objects) {
            obj.render(batch);
        }

        // Рендерим врагов
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                enemy.render(batch);
            }
        }

        // Рендерим боссов
        for (Boss boss : bosses) {
            if (boss.isAlive()) {
                boss.render(batch);
            }
        }

        // Рендерим портал если активен
        if (portalActive && portalPosition != null) {
            // batch.draw(portalTexture, portalPosition.x, portalPosition.y, 120f, 220f);
        }
    }


    public void checkWinCondition() {
        if (completed) return;

        // Проверяем, что все боссы побеждены
        boolean allBossesDefeated = true;
        for (Boss boss : bosses) {
            if (boss.isAlive()) {
                allBossesDefeated = false;
                break;
            }
        }

        // Проверяем, что все обязательные враги побеждены
        boolean allEnemiesDefeated = true;
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                allEnemiesDefeated = false;
                break;
            }
        }

        if (allBossesDefeated && allEnemiesDefeated) {
            completed = true;
            portalActive = true;
        }
    }


    public boolean isCompleted() {
        return completed;
    }

    // Проверка коллизий для игрока
    public void checkCollisions(Player player) {
        if (!initialized || !player.isAlive()) return;

        // Коллизии с врагами
        for (Enemy enemy : enemies) {
            if (enemy.isAlive() && player.getBounds().overlaps(enemy.getBounds())) {
                handlePlayerEnemyCollision(player, enemy);
            }
        }

        // Коллизии с боссами
        for (Boss boss : bosses) {
            if (boss.isAlive() && player.getBounds().overlaps(boss.getBounds())) {
                handlePlayerBossCollision(player, boss);
            }
        }

        // Коллизии с ловушками
        for (Trap trap : traps) {
            if (trap.isActive() && player.getLowerBounds().overlaps(trap.getBounds())) {
                player.takeDamage(trap.getDamage());
                trap.trigger();
            }
        }


    }

    private void handlePlayerEnemyCollision(Player player, Enemy enemy) {
        // Логика отталкивания игрока от врага
        Vector2 pushDirection = new Vector2(
            player.getPosition().x - enemy.getPosition().x,
            player.getPosition().y - enemy.getPosition().y
        ).nor();

        float pushDistance = 5.0f;
        player.getPosition().add(pushDirection.scl(pushDistance));
        player.checkBounds();
    }

    private void handlePlayerBossCollision(Player player, Boss boss) {
        // Аналогичная логика для боссов
        Vector2 pushDirection = new Vector2(
            player.getPosition().x - boss.getPosition().x,
            player.getPosition().y - boss.getPosition().y
        ).nor();

        float pushDistance = 5.0f;
        player.getPosition().add(pushDirection.scl(pushDistance));
        player.checkBounds();
    }

    // Проверка взаимодействия с порталом
    public boolean checkPortalInteraction(Player player) {
        if (!portalActive || portalPosition == null) return false;

        // Создаем bounds для портала
        float portalX = portalPosition.x;
        float portalY = portalPosition.y;
        float portalWidth = 120f;
        float portalHeight = 220f;

        // Проверяем пересечение с игроком
        return player.getBounds().overlaps(new com.badlogic.gdx.math.Rectangle(portalX, portalY, portalWidth, portalHeight));
    }

    // Методы для настройки уровня
    public void setPlayerTarget(Player player) {
        for (Boss boss : bosses) {
            boss.setTarget(player);
        }
        for (Enemy enemy : enemies) {
            enemy.setTarget(player);
        }
    }

    public void equipWeapons() {
        // Экипируем оружие боссам
        for (int i = 0; i < bosses.size(); i++) {
            Boss boss = bosses.get(i);
            String weaponPath = getWeaponPathForBoss(i, levelNumber);
            if (weaponPath != null) {
                MeleeWeapon weapon = new MeleeWeapon(
                    "Boss Weapon " + i,
                    getBossWeaponDamage(levelNumber),
                    1.0f,
                    weaponPath
                );
                boss.equipWeapon(weapon);
            }
        }
    }

    private String getWeaponPathForBoss(int bossIndex, int levelNum) {
        switch (levelNum) {
            case 1:
                return bossIndex == 0 ? "images/weapons/boss1weapon.png" : "images/weapons/monster_sword.png";
            case 2:
                return "images/weapons/ice_sword.png";
            case 3:
                return "images/weapons/shadow_blade.png";
            default:
                return "images/weapons/sword.png";
        }
    }

    private int getBossWeaponDamage(int levelNum) {
        switch (levelNum) {
            case 1: return 30;
            case 2: return 50;
            case 3: return 80;
            default: return 30;
        }
    }

    // Освобождение ресурсов
    public void dispose() {
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }

        for (Enemy enemy : enemies) {
            enemy.dispose();
        }

        for (Boss boss : bosses) {
            boss.dispose();
        }

        for (GameObject obj : objects) {
            obj.dispose();
        }

        if (map != null) {
            map.dispose();
        }
    }

    // Getters и Setters
    public int getLevelNumber() { return levelNumber; }
    public String getLevelName() { return levelName; }
    public List<Enemy> getEnemies() { return enemies; }
    public List<Boss> getBosses() { return bosses; }
    public List<Trap> getTraps() { return traps; }
    public List<GameObject> getObjects() { return objects; }
    public Vector2 getPlayerSpawnPoint() { return playerSpawnPoint; }
    public Vector2 getPortalPosition() { return portalPosition; }
    public boolean isPortalActive() { return portalActive; }
    public LevelType getLevelType() { return levelType; }
    public boolean isInitialized() { return initialized; }
}
