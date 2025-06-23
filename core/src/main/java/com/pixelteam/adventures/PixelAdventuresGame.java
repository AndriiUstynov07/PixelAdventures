package com.pixelteam.adventures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixelteam.adventures.entities.HealthPotion;
import com.pixelteam.adventures.entities.Trap;
import com.pixelteam.adventures.entities.enemies.DragonBoss;
import com.pixelteam.adventures.entities.enemies.IceSpirit;
import com.pixelteam.adventures.entities.enemies.MiniBossFirst;
import com.pixelteam.adventures.entities.enemies.MiniBossIceKnight;
import com.pixelteam.adventures.entities.player.Player;
import com.pixelteam.adventures.weapons.MeleeWeapon;
import com.pixelteam.adventures.entities.enemies.IceBoss;


public class PixelAdventuresGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private Player player;
    private MeleeWeapon sword;
    private DragonBoss boss;
    private MiniBossFirst miniBossFirst;
    private float deltaTime;

    // Portal variables
    private Texture portalTexture;
    private boolean showPortal = false;
    private Vector2 portalPosition = new Vector2();
    private boolean canEnterPortal = false;
    private static final float PORTAL_INTERACTION_DISTANCE = 100f; // Distance to interact with portal

    // Camera system
    private OrthographicCamera camera;
    private Viewport viewport;
    private float worldWidth;
    private float worldHeight;
    private static final float LEVEL2_CAMERA_SCALE = 0.6f; // Scale factor for level 2 camera
    public static final float LEVEL2_PLAYER_SCALE = 0.25f; // Scale factor for level 2 player
    private float viewportWidth;
    private float viewportHeight;

    private List<Trap> traps;
    private Texture trapTexture;
    private List<HealthPotion> potions;
    private Texture potionTexture;

    // New level transition variables
    private Texture map2Texture;
    private Texture map3Texture;
    private static boolean isLevel2 = false;
    private static boolean isLevel3 = false;
    private Vector2 level2PlayerSpawn = new Vector2(995f, 65f); // Adjusted spawn position (770 + 50 = 820, 175 - 50 = 125)
    private Vector2 level3PlayerSpawn = new Vector2(1100f, 350f); // Position player directly on the portal
    private Vector2 level2PortalPosition = new Vector2();
    private Vector2 level3PortalPosition = new Vector2(); // Fixed position for level 3 portal
    private boolean showLevel2Portal = false;
    private boolean canEnterLevel2Portal = false;
    private boolean showLevel3Portal = true; // Додаємо змінну для відображення порталу на 3 рівні

    // Add getter for isLevel2
    public static boolean isLevel2() {
        return isLevel2;
    }

    private BitmapFont font;

    private boolean isLoading = true;
    private float loadingTime = 0f;
    private static final float LOADING_DURATION = 3f; // 3 seconds loading screen
    private Texture loadingTexture;
    private BitmapFont loadingFont;

    // Game Over variables
    private float gameOverTimer = 0f;
    private static final float GAME_OVER_DELAY = 2f; // 2 seconds delay
    private Texture gameOverTexture;
    private boolean showGameOver = false;

    private MiniBossIceKnight miniBossIceKnight;
    private IceBoss iceBoss;

    private List<IceSpirit> iceSpirits;
    private Texture iceSpiritTexture;
    private float iceSpiritSpawnTimer;
    private static final float ICE_SPIRIT_SPAWN_INTERVAL = 1.3f;
    private int iceSpiritsSpawned;
    private static final int MAX_ICE_SPIRITS = 5;

    // Fire miniboss for level 3
    private MiniBossFirst fireMiniBoss;
    private MeleeWeapon fireMiniBossWeapon;
    private static final float FIRE_MINIBOSS_SIZE = 50f;
    private static final int FIRE_MINIBOSS_DAMAGE = 25;
    private static final int FIRE_MINIBOSS_HEALTH = 600;
    private static com.badlogic.gdx.graphics.Texture fireMiniBossHealthBarPixel;
    private Vector2 fireMiniBossPrevPlayerPos = new Vector2();

    private boolean fireMiniBossWasHitThisAttack = false;

    // Add this field to the class:
    private DragonBoss fireBoss = null;
    private boolean fireBossPlayerEnteredRoom = false;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // Load loading screen resources
        try {
                loadingTexture = new Texture(Gdx.files.internal("assets/images/environment/tiles/loadingscreen.png"));
            System.out.println("Loading screen texture loaded successfully");
        } catch (Exception e) {
            System.out.println("Error loading loading screen texture: " + e.getMessage());
            e.printStackTrace();
        }
        loadingFont = new BitmapFont();
        loadingFont.getData().setScale(2f);

        // Load game over screen
        try {
                gameOverTexture = new Texture(Gdx.files.internal("assets/images/environment/tiles/gameoverscreen.png"));
            System.out.println("Game over screen texture loaded successfully");
        } catch (Exception e) {
            System.out.println("Error loading game over screen texture: " + e.getMessage());
            e.printStackTrace();
        }
        // Завантаження фонового зображення
        try {
                backgroundTexture = new Texture(Gdx.files.internal("assets/images/environment/tiles/map1.png"));
            System.out.println("Map1 texture loaded successfully");

            map2Texture = new Texture(Gdx.files.internal("assets/images/environment/tiles/map2.png"));
            System.out.println("Map2 texture loaded successfully");

            map3Texture = new Texture(Gdx.files.internal("assets/images/environment/tiles/map3.PNG"));
            System.out.println("Map3 texture loaded successfully");
        } catch (Exception e) {
            System.out.println("Error loading map textures: " + e.getMessage());
            e.printStackTrace();
        }
        // Завантаження текстури гравця
        Texture playerTexture = null;
        try {
            playerTexture = new Texture(Gdx.files.internal("assets/images/player/player.png"));
            System.out.println("Player texture loaded successfully");
        } catch (Exception e) {
            System.out.println("Error loading player texture: " + e.getMessage());
            e.printStackTrace();
        }

        // Створення гравця в лівій кімнаті
        float playerX = 35f + (310f - 64f) / 2; // Центр лівої кімнати мінус половина розміру гравця
        float playerY = 260f + (165f - 64f) / 2; // Центр лівої кімнати мінус половина розміру гравця
        player = new Player(playerX, playerY);
        if (playerTexture != null) {
            player.setTexture(playerTexture);
        }
        // Створення меча
        sword = new MeleeWeapon("Sword", 100, 1.0f, "images/weapons/sword.png");
        player.equipWeapon(sword);

        // Створення головного боса в правій кімнаті
        // Позиція в центрі правої кімнати
        float bossX = 835f + (310f - 128f) / 2; // Центр правої кімнати мінус половина розміру боса
        float bossY = 260f + (165f - 128f) / 2; // Центр правої кімнати мінус половина розміру боса
        boss = new DragonBoss(bossX, bossY);
        boss.setTarget(player);

        // Створення міні-боса в середній кімнаті
        float miniBossX = 385f + (360f - 96f) / 2; // Центр середньої кімнати мінус половина розміру міні-боса
        float miniBossY = 210f + (215f - 96f) / 2; // Центр середньої кімнати мінус половина розміру міні-боса
        miniBossFirst = new MiniBossFirst(miniBossX, miniBossY);
        miniBossFirst.setTarget(player);

        // Add bosses to player's list for collision detection
        player.addBoss(boss);
        player.addBoss(miniBossFirst);

        trapTexture = new Texture(Gdx.files.internal("images/other/trap.png"));
        traps = new ArrayList<>();
        float trapSize = 48f;
        float room2X = 385f;
        float room2Y = 210f;
        float room2Width = 205f;
        float room2Height = 230f;
        traps.add(new Trap(room2X+trapSize, room2Y, trapSize, 70, trapTexture));
        traps.add(new Trap(room2X + room2Width, room2Y, trapSize, 70, trapTexture));
        traps.add(new Trap(room2X +trapSize, room2Y + room2Height - trapSize, trapSize, 70, trapTexture));
        traps.add(new Trap(room2X + room2Width, room2Y + room2Height - trapSize, trapSize, 70, trapTexture));
        traps.add(new Trap(327f, 288f, trapSize, 70, trapTexture));
        traps.add(new Trap(695f, 350f, trapSize, 70, trapTexture));

        // Health potions
        potionTexture = new Texture(Gdx.files.internal("images/other/health_potion.png"));
        potions = new ArrayList<>();
        float potionSize = 40f;
        float potionX = room2X + trapSize * 2f;
        float potionY = room2Y + room2Height - potionSize;
        potions.add(new HealthPotion(potionX, potionY, potionSize, 100, potionTexture));

        // Завантаження текстури головного боса
        if (Gdx.files.internal("images/monsters/big_boss_1.PNG").exists()) {
            Texture bossTexture = new Texture(Gdx.files.internal("images/monsters/big_boss_1.PNG"));
            boss.setTexture(bossTexture);
        } else if (Gdx.files.internal("images/enemies/dragon_boss.png").exists()) {
            Texture bossTexture = new Texture(Gdx.files.internal("images/enemies/dragon_boss.png"));
            boss.setTexture(bossTexture);
        } else if (Gdx.files.internal("images/enemies/boss.png").exists()) {
            Texture bossTexture = new Texture(Gdx.files.internal("images/enemies/boss.png"));
            boss.setTexture(bossTexture);
        } else {
            // Використовуємо текстуру гравця як заглушку для боса (збільшену)
            boss.setTexture(playerTexture);
        }

        // Завантаження текстури міні-боса
        if (Gdx.files.internal("images/monsters/mini_boss_1.png").exists()) {
            Texture miniBossTexture = new Texture(Gdx.files.internal("images/monsters/mini_boss_1.png"));
            miniBossFirst.setTexture(miniBossTexture);
        } else if (Gdx.files.internal("images/enemies/mini_boss.png").exists()) {
            Texture miniBossTexture = new Texture(Gdx.files.internal("images/enemies/mini_boss.png"));
            miniBossFirst.setTexture(miniBossTexture);
        } else if (Gdx.files.internal("images/enemies/orc.png").exists()) {
            Texture miniBossTexture = new Texture(Gdx.files.internal("images/enemies/orc.png"));
            miniBossFirst.setTexture(miniBossTexture);
        } else {
            // Використовуємо текстуру гравця як заглушку для міні-боса
            miniBossFirst.setTexture(playerTexture);
        }

        // Завантаження зброї головного боса
        if (Gdx.files.internal("images/weapons/boss1weapon.png").exists()) {
            MeleeWeapon bossWeapon = new MeleeWeapon("Boss Weapon", 30, 1.0f, "images/weapons/boss1weapon.png");
            boss.equipWeapon(bossWeapon);
        } else if (Gdx.files.internal("boss1weapon.png").exists()) {
            MeleeWeapon bossWeapon = new MeleeWeapon("Boss Weapon", 30, 1.0f, "boss1weapon.png");
            boss.equipWeapon(bossWeapon);
        } else {
            // Використовуємо звичайний меч як заглушку
            MeleeWeapon bossWeapon = new MeleeWeapon("Boss Weapon", 30, 1.0f, "images/weapons/sword.png");
            boss.equipWeapon(bossWeapon);
        }

        // Завантаження зброї міні-боса
        if (Gdx.files.internal("images/weapons/monster_sword.png").exists()) {
            MeleeWeapon miniBossWeapon = new MeleeWeapon("Mini Boss Weapon", 20, 1.0f, "images/weapons/monster_sword.png");
            miniBossFirst.equipWeapon(miniBossWeapon);
        } else if (Gdx.files.internal("images/weapons/axe.png").exists()) {
            MeleeWeapon miniBossWeapon = new MeleeWeapon("Mini Boss Axe", 20, 1.0f, "images/weapons/axe.png");
            miniBossFirst.equipWeapon(miniBossWeapon);
        } else {
            // Використовуємо звичайний меч як заглушку
            MeleeWeapon miniBossWeapon = new MeleeWeapon("Mini Boss Weapon", 20, 1.0f, "images/weapons/sword.png");
            miniBossFirst.equipWeapon(miniBossWeapon);
        }

        // Pixel perfect налаштування
        Gdx.graphics.setVSync(true);

        // Вимкнути згладжування для pixel art
        Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_NEAREST);
        Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_NEAREST);

        // Load portal texture
        try {
                portalTexture = new Texture(Gdx.files.internal("assets/images/environment/decorations/portal.png"));
            System.out.println("Portal texture loaded successfully from: assets/images/environment/decorations/portal.png");
        } catch (Exception e) {
            System.out.println("Error loading portal texture: " + e.getMessage());
            e.printStackTrace();
        }
        // Set portal position in the middle of the right room
        portalPosition.x = 835f + (310f - 120f) / 2; // Center of right room minus half of portal width
        portalPosition.y = 260f + (165f - 220f) / 2; // Center of right room minus half of portal height

        // Ініціалізація камери та viewport
        worldWidth = 1280f;  // Ширина карти
        worldHeight = 720f;  // Висота карти

        viewportWidth = worldWidth * 0.412f;
        viewportHeight = worldHeight * 0.412f;

        camera = new OrthographicCamera();
        viewport = new FitViewport(viewportWidth, viewportHeight, camera);
        viewport.apply();

        // Встановлюємо початкову позицію камери на гравця
        camera.position.set(
            player.getPosition().x + player.getWidth() / 2,
            player.getPosition().y + player.getHeight() / 2,
            0
        );

        font = new BitmapFont();

        // Load ice spirit texture
        if (Gdx.files.internal("images/monsters/icespirit.png").exists()) {
            iceSpiritTexture = new Texture(Gdx.files.internal("images/monsters/icespirit.png"));
        } else {
            // Use a fallback texture if the ice spirit texture is not found
            iceSpiritTexture = new Texture(Gdx.files.internal("images/player/player.png"));
        }
    }

    @Override
    public void render() {
        if (isLoading) {
            // Show loading screen
            ScreenUtils.clear(0, 0, 0, 1);
            batch.begin();

            // Draw loading background
            batch.draw(loadingTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            // Draw loading text
            String loadingText = "Loading... " + (int)((loadingTime / LOADING_DURATION) * 100) + "%";
            loadingFont.draw(batch, loadingText,
                Gdx.graphics.getWidth() / 2f - 100,
                Gdx.graphics.getHeight() / 2f);

            batch.end();

            // Update loading time
            loadingTime += Gdx.graphics.getDeltaTime();
            if (loadingTime >= LOADING_DURATION) {
                isLoading = false;
            }
            return;
        }

        // Оновлення deltaTime
        deltaTime = Gdx.graphics.getDeltaTime();

        // Оновлення гравця (навіть якщо мертвий, для правильної обробки анімацій)
        player.update(deltaTime);

        // Оновлення головного боса тільки якщо гравець живий
        if (boss != null && boss.isAlive() && player.isAlive()) {
            boss.update(deltaTime, player);
        }

        // Оновлення міні-боса тільки якщо гравець живий
        if (miniBossFirst != null && miniBossFirst.isAlive() && player.isAlive()) {
            miniBossFirst.update(deltaTime, player);
        }

        // Оновлення fireBoss тільки якщо гравець живий
        if (isLevel3 && fireBoss != null && fireBoss.isAlive() && player.isAlive()) {
            fireBoss.update(deltaTime, player);
        }

        // Перевірка колізій тільки якщо гравець живий
        if (player.isAlive()) {
            // Перевірка колізії між гравцем та головним босом
            if (boss != null && boss.isAlive()) {
                checkPlayerBossCollision(boss);
            }

            // Перевірка колізії між гравцем та міні-босом
            if (miniBossFirst != null && miniBossFirst.isAlive()) {
                checkPlayerMiniBossCollision();
            }

            // Перевірка колізії між гравцем та fireBoss
            if (isLevel3 && fireBoss != null && fireBoss.isAlive()) {
                checkPlayerBossCollision(fireBoss);
            }

            // Перевірка колізії з пастками (враховуємо тільки нижню частину гравця)
            for (Trap trap : traps) {
                if (trap.isActive() && player.getLowerBounds().overlaps(trap.getBounds())) {
                    player.takeDamage(trap.getDamage());
                    trap.trigger();
                }
            }
        }

        // Check collision with health potions
        Iterator<HealthPotion> potionIterator = potions.iterator();
        while (potionIterator.hasNext()) {
            HealthPotion potion = potionIterator.next();
            if (potion.isActive() && player.getBounds().overlaps(potion.getBounds())) {
                player.heal(potion.getHealAmount());
                potion.setActive(false);
                potionIterator.remove();
            }
        }

        // Оновлюємо позицію камери, щоб вона слідувала за гравцем
        if (player.isAlive()) {
            camera.position.set(
                player.getPosition().x + player.getWidth() / 2,
                player.getPosition().y + player.getHeight() / 2,
                0
            );
        }

        // Оновлюємо камеру
        camera.update();

        // Check for game over
        if (!player.isAlive() && !showGameOver) {
            gameOverTimer += Gdx.graphics.getDeltaTime();
            if (gameOverTimer >= GAME_OVER_DELAY) {
                showGameOver = true;
            }
        }

        // Очищаємо екран
        ScreenUtils.clear(0, 0, 0, 1);

        if (showGameOver) {
            // Create a new camera for game over screen without zoom
            OrthographicCamera gameOverCamera = new OrthographicCamera();
            gameOverCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            gameOverCamera.update();

            // Draw game over screen using the new camera
            batch.setProjectionMatrix(gameOverCamera.combined);
            batch.begin();
            batch.draw(gameOverTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.end();
            return;
        }

        // Встановлюємо проекцію камери для SpriteBatch
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Малюємо фонове зображення в оригінальному розмірі
        batch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);

        // --- Рендер головного боса (тільки якщо не 3 рівень) ---
        if (boss != null && boss.isAlive() && !isLevel3) {
            boss.render(batch);
        }

        // Рендеринг пастки
        for (Trap trap : traps) {
            trap.render(batch);
        }

        // Рендеринг зілля
        for (HealthPotion potion : potions) {
            potion.render(batch);
        }

        // Check if DragonBoss is dead and render portal if it is (only on level 1)
        if (boss != null && !boss.isAlive() && !isLevel3) {
            // Show portal only after DragonBoss is defeated
            showPortal = true;
        }

        // Check if player is near portal
        if (showPortal) {
            float distanceToPortal = player.getPosition().dst(portalPosition);
            canEnterPortal = distanceToPortal < PORTAL_INTERACTION_DISTANCE;

            // Check for portal interaction
            if (canEnterPortal && Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.E)) {
                // Transition to level 2
                setLevel2();
                backgroundTexture = map2Texture;
                player.getPosition().set(level2PlayerSpawn);

                // Apply level 2 scale to viewport and camera
                viewportWidth = worldWidth * 0.412f * LEVEL2_CAMERA_SCALE;
                viewportHeight = worldHeight * 0.412f * LEVEL2_CAMERA_SCALE;
                viewport.setWorldSize(viewportWidth, viewportHeight);
                viewport.apply();

                // Scale player and its components
                player.setScale(LEVEL2_PLAYER_SCALE);
                if (player.getWeapon() != null) {
                    player.getWeapon().setScale(LEVEL2_PLAYER_SCALE);
                }
                player.setHealthBarScale(LEVEL2_PLAYER_SCALE);

                // Set new playable areas for level 2
                player.setLevel2Areas();

                // Clear traps and portal
                traps.clear();
                addLevel2Traps();
                showPortal = false;
                canEnterPortal = false;
                if (portalTexture != null) {
                    portalTexture.dispose();
                    portalTexture = null;
                }

                // Reset camera position to player
                camera.position.set(
                    player.getPosition().x + player.getWidth() / 2,
                    player.getPosition().y + player.getHeight() / 2,
                    0
                );
            }
        }

        // Render portal if it should be shown (before player so it appears behind)
        if (showPortal && portalTexture != null && !isLevel3) {
            batch.draw(portalTexture, portalPosition.x, portalPosition.y, 120f, 220f);

            // Show interaction prompt if player is near portal
            if (canEnterPortal) {
                font.draw(batch, "Press E to enter", portalPosition.x, portalPosition.y - 20);
            }
        }

        // Update Ice Boss
        if (isLevel2 && iceBoss != null && iceBoss.isAlive() && player.isAlive()) {
            iceBoss.update(deltaTime, player);
        }

        // Update Ice Knight mini-boss
        if (isLevel2 && miniBossIceKnight != null && miniBossIceKnight.isAlive() && player.isAlive()) {
            miniBossIceKnight.update(deltaTime, player);
        }

        // Check collision with Ice Boss
        if (isLevel2 && player.isAlive() && iceBoss != null && iceBoss.isAlive()) {
            checkPlayerIceBossCollision();
        }

        // Check collision with Ice Knight mini-boss
        if (isLevel2 && player.isAlive() && miniBossIceKnight != null && miniBossIceKnight.isAlive()) {
            checkPlayerIceKnightCollision();
        }

        // Check if both bosses are dead and show portalAdd commentMore actions
        if (isLevel2 && !isLevel3 && iceBoss != null && miniBossIceKnight != null &&
                !iceBoss.isAlive() && !miniBossIceKnight.isAlive() && !showLevel2Portal) {
            showLevel2Portal = true;
            // Set portal position at the ice boss location
            level2PortalPosition.set(iceBoss.getPosition());
            System.out.println("Portal should appear at: " + level2PortalPosition); // Debug message

            // Reload portal texture if it was disposed
            if (portalTexture == null) {
                try {
                    portalTexture = new Texture(Gdx.files.internal("assets/images/environment/decorations/portal.png"));
                    System.out.println("Portal texture reloaded successfully");
                } catch (Exception e) {
                    System.out.println("Error reloading portal texture: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        // Check if player is near level 2 portal
        if (showLevel2Portal) {
            float distanceToPortal = player.getPosition().dst(level2PortalPosition);
            canEnterLevel2Portal = distanceToPortal < PORTAL_INTERACTION_DISTANCE;

            // Check for portal interaction
            if (canEnterLevel2Portal && Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.E)) {
                // Transition to level 3
                setLevel3();
                backgroundTexture = map3Texture;
                player.getPosition().set(level3PlayerSpawn);

                // Clear all level 2 entities
                traps.clear();
                potions.clear();
                if (iceSpirits != null) {
                    iceSpirits.clear();
                }
                iceSpiritsSpawned = 0;

                // Reset portal state
                showLevel2Portal = false;
                canEnterLevel2Portal = false;
                if (portalTexture != null) {
                    portalTexture.dispose();
                    portalTexture = null;
                }

                // Reset camera position to player
                camera.position.set(
                        player.getPosition().x + player.getWidth() / 2,
                        player.getPosition().y + player.getHeight() / 2,
                        0
                );
            }
        }

        // Render level 2 portal if it should be shown
        if (showLevel2Portal) {
            if (portalTexture != null) {
                System.out.println("Drawing portal at: " + level2PortalPosition.x + ", " + level2PortalPosition.y);
                // Scale portal dimensions for level 2
                float portalWidth = 120f * LEVEL2_PLAYER_SCALE;
                float portalHeight = 220f * LEVEL2_PLAYER_SCALE;
                batch.draw(portalTexture, level2PortalPosition.x, level2PortalPosition.y, portalWidth, portalHeight);

                // Show interaction prompt if player is near portal
                if (canEnterLevel2Portal) {
                    // Scale text position and size
                    float textX = level2PortalPosition.x;
                    float textY = level2PortalPosition.y - (20f * LEVEL2_PLAYER_SCALE);
                    font.getData().setScale(LEVEL2_PLAYER_SCALE);
                    font.draw(batch, "Press E to enter", textX, textY);
                    font.getData().setScale(1.0f); // Reset scale after drawing
                }
            } else {
                System.out.println("Portal texture is null!");
            }
        }

        // Update ice spirit spawn timer and spawn new spirits
        if (isLevel2 && iceSpirits != null && iceSpiritsSpawned < MAX_ICE_SPIRITS) {
            // Check if player is in room 2 (bottom central room)
            boolean isPlayerInRoom2 = false;

            // Define Room 2 bounds based on initializeLevel2PlayableAreas
            Rectangle room2Bounds = new Rectangle(
                513.32f + 12.5f, // x (from initializeLevel2PlayableAreas)
                24.01f,         // y (from initializeLevel2PlayableAreas)
                235.62f - 12.5f, // width (from initializeLevel2PlayableAreas)
                89.04f          // height (from initializeLevel2PlayableAreas)
            );

            // Check if player overlaps with Room 2
            if (player.getBounds().overlaps(room2Bounds)) {
                isPlayerInRoom2 = true;
                System.out.println("Player is in Room 2!");
            }

            if (isPlayerInRoom2) {
                iceSpiritSpawnTimer += deltaTime;
                if (iceSpiritSpawnTimer >= ICE_SPIRIT_SPAWN_INTERVAL) {
                    // Spawn position in room 2 (bottom-left corner)
                    float spawnX = 513.32f + 12.5f; // Room 2 x + PLAYER_WIDTH
                    float spawnY = 16.01f + 17.0f;  // Room 2 y + PLAYER_HEIGHT

                    IceSpirit spirit = new IceSpirit(spawnX, spawnY);
                    spirit.setTexture(iceSpiritTexture);
                    spirit.setTarget(player);
                    iceSpirits.add(spirit);

                    iceSpiritSpawnTimer = 0f;
                    iceSpiritsSpawned++;
                }
            }
        }

        // Update and render ice spirits
        if (iceSpirits != null) {
            for (IceSpirit spirit : iceSpirits) {
                if (spirit.isAlive()) {
                    spirit.update(deltaTime);
                    spirit.render(batch);
                    spirit.checkPlayerCollision(player);

                    // Check if player's weapon hits the spirit
                    if (player.isAttacking() && player.getWeapon() != null) {
                        Rectangle weaponBounds = player.getWeaponBounds();
                        if (weaponBounds != null && weaponBounds.overlaps(spirit.getBounds())) {
                            spirit.takeDamage(1); // One hit kill
                        }
                    }
                }
            }
        }

        // Render portal on level 3 at fixed position
        if (isLevel3 && showLevel3Portal) {
            if (portalTexture != null) {
                // Scale portal dimensions for level 3
                float portalWidth = 200f * LEVEL2_PLAYER_SCALE;
                float portalHeight = 300f * LEVEL2_PLAYER_SCALE;
                batch.draw(portalTexture,
                    level3PortalPosition.x - portalWidth/2,
                    level3PortalPosition.y - portalHeight/2,
                    portalWidth,
                    portalHeight
                );
            } else {
                try {
                    portalTexture = new Texture(Gdx.files.internal("assets/images/environment/decorations/portal.png"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Update and render fire miniboss on level 3
        if (isLevel3 && fireMiniBoss != null && fireMiniBoss.isAlive()) {
            // --- Prevent boss from getting too close to player ---
            float minDistanceToPlayer = 40f;
            float bossToPlayerDist = fireMiniBoss.getPosition().dst(player.getPosition());
            if (bossToPlayerDist < minDistanceToPlayer) {
                try {
                    java.lang.reflect.Field velocityField = fireMiniBoss.getClass().getSuperclass().getDeclaredField("velocity");
                    velocityField.setAccessible(true);
                    com.badlogic.gdx.math.Vector2 velocity = (com.badlogic.gdx.math.Vector2) velocityField.get(fireMiniBoss);
                    velocity.set(0, 0);
                } catch (Exception e) { /* ignore */ }
            }
            fireMiniBoss.update(deltaTime, player);
            // --- EXPLICITLY UPDATE WEAPON SWING ANIMATION ---
            try {
                java.lang.reflect.Method updateWeaponAnim = fireMiniBoss.getClass().getDeclaredMethod("updateWeaponAnimation", float.class);
                updateWeaponAnim.setAccessible(true);
                updateWeaponAnim.invoke(fireMiniBoss, deltaTime);
            } catch (Exception e) { /* ignore */ }
            // --- Render fire miniboss sprite (with facing) ---
            if (fireMiniBoss.getTexture() != null && fireMiniBoss.isAlive()) {
                if (fireMiniBoss.isFacingLeft()) {
                    batch.draw(fireMiniBoss.getTexture(), fireMiniBoss.getPosition().x + fireMiniBoss.getWidth(), fireMiniBoss.getPosition().y, -fireMiniBoss.getWidth(), fireMiniBoss.getHeight());
                } else {
                    batch.draw(fireMiniBoss.getTexture(), fireMiniBoss.getPosition().x, fireMiniBoss.getPosition().y, fireMiniBoss.getWidth(), fireMiniBoss.getHeight());
                }
            }
            // --- Compute weapon bounds and render staff with correct animation (MiniBossFirst logic, but larger) ---
            Rectangle weaponBounds = null;
            if (fireMiniBoss.getWeapon() != null && fireMiniBoss.getWeapon().getTexture() != null) {
                float weaponWidth = fireMiniBoss.getWeapon().getWidth() * 0.5f;
                float weaponHeight = fireMiniBoss.getWeapon().getHeight() * 0.5f;
                float offsetX, offsetY, totalRotation;
                float weaponRotation = -30.0f;
                float weaponAttackAnimation = 0f;
                try {
                    java.lang.reflect.Method getWAA = fireMiniBoss.getClass().getMethod("getWeaponAttackAnimation");
                    weaponAttackAnimation = (float) getWAA.invoke(fireMiniBoss);
                } catch (Exception e) { /* fallback to 0 */ }
                boolean flipX = fireMiniBoss.isFacingLeft();
                if (fireMiniBoss.isFacingLeft()) {
                    offsetX = -13.5f;
                    offsetY = -10.5f;
                    totalRotation = weaponRotation - weaponAttackAnimation;
                } else {
                    offsetX = 13.5f;
                    offsetY = -10.5f;
                    totalRotation = weaponRotation + weaponAttackAnimation;
                }
                float weaponX = fireMiniBoss.getPosition().x + fireMiniBoss.getWidth() / 2.0f + offsetX - weaponWidth / 2.0f;
                float weaponY = fireMiniBoss.getPosition().y + fireMiniBoss.getHeight() / 2.0f + offsetY - weaponHeight / 2.0f;
                weaponBounds = new Rectangle(weaponX, weaponY, weaponWidth, weaponHeight);
                batch.draw(fireMiniBoss.getWeapon().getTexture(), weaponX, weaponY, weaponWidth / 2.0f, weaponHeight / 2.0f, weaponWidth, weaponHeight, 1.0f, 1.0f, totalRotation, 0, 0, fireMiniBoss.getWeapon().getTexture().getWidth(), fireMiniBoss.getWeapon().getTexture().getHeight(), flipX, false);
            }
            // --- Collision: do nothing (allow overlap, no bounce, no stop) ---
            // Draw a smaller health bar above the fire miniboss
            if (fireMiniBossHealthBarPixel == null) {
                com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
                pixmap.setColor(1, 1, 1, 1);
                pixmap.fill();
                fireMiniBossHealthBarPixel = new com.badlogic.gdx.graphics.Texture(pixmap);
                pixmap.dispose();
            }
            float barWidth = 35f; // smaller width
            float barHeight = 5f; // smaller height
            float barOffsetY = fireMiniBoss.getHeight();
            float barX = fireMiniBoss.getPosition().x + (fireMiniBoss.getWidth() - barWidth) / 2f;
            float barY = fireMiniBoss.getPosition().y + barOffsetY;
            // --- Health bar for 3-hit miniboss ---
            int hitsTaken = 0;
            try {
                java.lang.reflect.Field swordHitCountField = fireMiniBoss.getClass().getSuperclass().getDeclaredField("swordHitCount");
                swordHitCountField.setAccessible(true);
                hitsTaken = swordHitCountField.getInt(fireMiniBoss);
            } catch (Exception e) { /* ignore */ }
            float healthPercent = 1.0f - (hitsTaken / 3.0f);
            if (healthPercent < 0f) healthPercent = 0f;
            batch.setColor(0.2f, 0.2f, 0.2f, 0.8f);
            batch.draw(fireMiniBossHealthBarPixel, barX - 2f, barY - 2f, barWidth + 4f, barHeight + 4f);
            batch.setColor(1.0f - healthPercent, healthPercent, 0.0f, 1.0f);
            batch.draw(fireMiniBossHealthBarPixel, barX, barY, barWidth * healthPercent, barHeight);
            batch.setColor(1f, 1f, 1f, 1f);
            // --- Player attacks fire miniboss: check collision with boss body and staff (MiniBossFirst logic) ---
            if (player.isAttacking() && player.getWeapon() != null) {
                Rectangle playerWeaponBounds = player.getWeaponBounds();
                if (playerWeaponBounds != null) {
                    // Boss body (MiniBossFirst logic: collisionMargin = 15f)
                    float collisionMargin = 15f;
                    Rectangle bossBodyBounds = new Rectangle(
                        fireMiniBoss.getPosition().x + collisionMargin,
                        fireMiniBoss.getPosition().y + collisionMargin,
                        fireMiniBoss.getWidth() - 2 * collisionMargin,
                        fireMiniBoss.getHeight() - 2 * collisionMargin
                    );
                    if (playerWeaponBounds.overlaps(bossBodyBounds)) {
                        fireMiniBoss.takeDamage(player.getWeapon().getDamage());
                    }
                    // Boss staff
                    if (weaponBounds != null && playerWeaponBounds.overlaps(weaponBounds)) {
                        fireMiniBoss.takeDamage(player.getWeapon().getDamage());
                    }
                }
            }
            // --- Check collision with player (weapon only) ---
            if (player.isAlive()) {
                checkPlayerFireMiniBossCollision(weaponBounds);
            }
            // --- Push player away if colliding with boss body (smaller zone) ---
            float collisionMargin = 15f;
            Rectangle bossBodyBounds = new Rectangle(
                fireMiniBoss.getPosition().x + collisionMargin,
                fireMiniBoss.getPosition().y + collisionMargin,
                fireMiniBoss.getWidth() - 2 * collisionMargin,
                fireMiniBoss.getHeight() - 2 * collisionMargin
            );
            if (player.isAlive() && bossBodyBounds.overlaps(player.getBounds())) {
                Vector2 playerCenter = new Vector2(
                    player.getPosition().x + player.getWidth() / 2,
                    player.getPosition().y + player.getHeight() / 2
                );
                Vector2 bossCenter = new Vector2(
                    fireMiniBoss.getPosition().x + fireMiniBoss.getWidth() / 2,
                    fireMiniBoss.getPosition().y + fireMiniBoss.getHeight() / 2
                );
                Vector2 pushDirection = playerCenter.sub(bossCenter).nor();
                float pushDistance = 2.0f;
                player.getPosition().add(pushDirection.scl(pushDistance));
                player.checkBounds();
            }
        }

        // --- Рендер fireBoss на 3 рівні ---
        if (isLevel3 && fireBoss != null && fireBoss.isAlive()) {
            fireBoss.render(batch);
        }

        // Рендеринг гравця (метод render вже перевіряє чи гравець живий)
        player.render(batch);

        // Рендеринг головного боса
        if (boss != null && boss.isAlive()) {
            boss.render(batch);
        }

        // Рендеринг міні-боса
        if (miniBossFirst != null && miniBossFirst.isAlive()) {
            miniBossFirst.render(batch);
        }

        // Рендеринг смужки здоров'я гравця завжди (навіть якщо мертвий, щоб показати 0 HP)
        player.renderHealthBar(batch);

        // Render Ice Boss
        if (isLevel2 && iceBoss != null && iceBoss.isAlive()) {
            iceBoss.render(batch);
        }

        // Render Ice Knight mini-boss
        if (isLevel2 && miniBossIceKnight != null && miniBossIceKnight.isAlive()) {
            miniBossIceKnight.render(batch);
        }

        // Render fireBoss
        if (isLevel3 && fireBoss != null && fireBoss.isAlive()) {
            fireBoss.render(batch);
        }

        batch.end();
    }



    private void checkPlayerBossCollision(DragonBoss boss) {
        // Перевіряємо колізію тільки якщо обидва живі
        if (!player.isAlive() || !boss.isAlive()) return;

        // Перевіряємо колізію з тілом боса (тільки відштовхування)
        if (player.getBounds().overlaps(boss.getBounds())) {
            // Розрахунок вектора відштовхування
            Vector2 playerCenter = new Vector2(
                player.getPosition().x + player.getWidth() / 2,
                player.getPosition().y + player.getHeight() / 2
            );
            Vector2 bossCenter = new Vector2(
                boss.getPosition().x + boss.getWidth() / 2,
                boss.getPosition().y + boss.getHeight() / 2
            );

            Vector2 pushDirection = playerCenter.sub(bossCenter).nor();

            // Відштовхуємо гравця від боса
            float pushDistance = 5.0f;
            player.getPosition().add(pushDirection.scl(pushDistance));

            // Додаткова перевірка меж після відштовхування
            player.checkBounds();
        }

        // Перевіряємо колізію зі зброєю боса
        Rectangle weaponBounds = boss.getBossWeaponBounds();
        if (weaponBounds != null) {
            // Зміщуємо хітбокс зброї далі від центру боса
            float weaponOffset = 40f; // Зміщення зброї від центру
            if (boss.isFacingLeft()) {
                weaponBounds.x -= weaponOffset;
            } else {
                weaponBounds.x += weaponOffset;
            }

            if (player.getBounds().overlaps(weaponBounds)) {
                // Check if the boss is facing the player
                boolean isFacingPlayer = (boss.isFacingLeft() && player.getPosition().x < boss.getPosition().x) ||
                                       (!boss.isFacingLeft() && player.getPosition().x > boss.getPosition().x);

                // Calculate distance between player and boss
                float distance = player.getPosition().dst(boss.getPosition());
                float minDamageDistance = 25f;

                // Додаткова перевірка, чи гравець дійсно знаходиться в зоні атаки
                boolean isInAttackRange = distance <= minDamageDistance &&
                                        Math.abs(player.getPosition().y - boss.getPosition().y) < 20f;

                // Перевіряємо, чи зброя дійсно знаходиться в правильній позиції для атаки
                boolean isWeaponInAttackPosition = false;
                if (boss.isFacingLeft()) {
                    isWeaponInAttackPosition = weaponBounds.x < boss.getPosition().x;
                } else {
                    isWeaponInAttackPosition = weaponBounds.x > boss.getPosition().x;
                }

                if (boss.isAttacking() &&
                    player.getDamageCooldown() <= 0 &&
                    isFacingPlayer &&
                    isInAttackRange &&
                    isWeaponInAttackPosition) {
                    MeleeWeapon weapon = boss.getWeapon();
                    if (weapon != null) {
                        player.takeDamage(weapon.getDamage());
                        player.setDamageCooldown(1.0f);
                    }
                }
            }
        }
    }

    private void checkPlayerIceBossCollision() {
        if (!player.isAlive() || iceBoss == null || !iceBoss.isAlive()) return;

        if (player.getBounds().overlaps(iceBoss.getBounds())) {
            Vector2 playerCenter = new Vector2(
                    player.getPosition().x + player.getWidth() / 2,
                    player.getPosition().y + player.getHeight() / 2
            );
            Vector2 bossCenter = new Vector2(
                    iceBoss.getPosition().x + iceBoss.getWidth() / 2,
                    iceBoss.getPosition().y + iceBoss.getHeight() / 2
            );

            Vector2 pushDirection = playerCenter.sub(bossCenter).nor();
            float pushDistance = 5.0f;
            player.getPosition().add(pushDirection.scl(pushDistance));
            player.checkBounds();
        }

        // Check collision with weapon
        Rectangle weaponBounds = iceBoss.getBossWeaponBounds();
        if (weaponBounds != null && player.getBounds().overlaps(weaponBounds)) {
            boolean isFacingPlayer = isBossFacingPlayer(iceBoss, player);
            float distance = player.getPosition().dst(iceBoss.getPosition());
            float minDamageDistance = 15f;
            boolean isInAttackRange = distance <= minDamageDistance &&
                                    Math.abs(player.getPosition().y - iceBoss.getPosition().y) < 10f;
            boolean isWeaponInAttackPosition = false;
            if (iceBoss.isFacingLeft()) {
                isWeaponInAttackPosition = weaponBounds.x < iceBoss.getPosition().x;
            } else {
                isWeaponInAttackPosition = weaponBounds.x > iceBoss.getPosition().x;
            }
            if (iceBoss.isAttacking() &&
                player.getDamageCooldown() <= 0 &&
                isFacingPlayer &&
                isInAttackRange &&
                isWeaponInAttackPosition) {
                MeleeWeapon weapon = iceBoss.getWeapon();
                if (weapon != null) {
                    player.takeDamage(weapon.getDamage());
                    player.setDamageCooldown(1.0f);
                }
            }
        }
    }

    private void checkPlayerMiniBossCollision() {
        // Перевіряємо колізію тільки якщо обидва живі
        if (!player.isAlive() || !miniBossFirst.isAlive()) return;

        if (player.getBounds().overlaps(miniBossFirst.getBounds())) {
            // Розрахунок вектора відштовхування
            Vector2 playerCenter = new Vector2(
                player.getPosition().x + player.getWidth() / 2,
                player.getPosition().y + player.getHeight() / 2
            );
            Vector2 miniBossCenter = new Vector2(
                miniBossFirst.getPosition().x + miniBossFirst.getWidth() / 2,
                miniBossFirst.getPosition().y + miniBossFirst.getHeight() / 2
            );

            Vector2 pushDirection = playerCenter.sub(miniBossCenter).nor();

            // Відштовхуємо гравця від міні-боса
            float pushDistance = 5.0f;
            player.getPosition().add(pushDirection.scl(pushDistance));

            // Додаткова перевірка меж після відштовхування
            player.checkBounds();
        }

        // Перевіряємо колізію зі зброєю міні-боса
        Rectangle weaponBounds = miniBossFirst.getBossWeaponBounds();
        if (weaponBounds != null && player.getBounds().overlaps(weaponBounds)) {
            // Check if the boss is facing the player
            boolean isFacingPlayer = (miniBossFirst.isFacingLeft() && player.getPosition().x < miniBossFirst.getPosition().x) ||
                                   (!miniBossFirst.isFacingLeft() && player.getPosition().x > miniBossFirst.getPosition().x);

            // Calculate distance between player and boss
            float distance = player.getPosition().dst(miniBossFirst.getPosition());
            float minDamageDistance = 25f; // Збільшуємо дистанцію для першого рівня

            // Додаткова перевірка, чи гравець дійсно знаходиться в зоні атаки
            boolean isInAttackRange = distance <= minDamageDistance &&
                                    Math.abs(player.getPosition().y - miniBossFirst.getPosition().y) < 20f;

            if (miniBossFirst.isAttacking() &&
                player.getDamageCooldown() <= 0 &&
                isFacingPlayer &&
                isInAttackRange) {
                MeleeWeapon weapon = miniBossFirst.getWeapon();
                if (weapon != null) {
                    player.takeDamage(weapon.getDamage());
                    player.setDamageCooldown(1.0f);
                }
            }
        }
    }

    private void checkPlayerIceKnightCollision() {
        // Check collision only if both are alive
        if (!player.isAlive() || miniBossIceKnight == null || !miniBossIceKnight.isAlive()) return;

        // Push player away if colliding with body
        if (player.getBounds().overlaps(miniBossIceKnight.getBounds())) {
            Vector2 playerCenter = new Vector2(
                player.getPosition().x + player.getWidth() / 2,
                player.getPosition().y + player.getHeight() / 2
            );
            Vector2 bossCenter = new Vector2(
                miniBossIceKnight.getPosition().x + miniBossIceKnight.getWidth() / 2,
                miniBossIceKnight.getPosition().y + miniBossIceKnight.getHeight() / 2
            );
            Vector2 pushDirection = playerCenter.sub(bossCenter).nor();
            float pushDistance = 5.0f;
            player.getPosition().add(pushDirection.scl(pushDistance));
            player.checkBounds();
        }

        // Check collision with weapon
        Rectangle weaponBounds = miniBossIceKnight.getBossWeaponBounds();
        if (weaponBounds != null && player.getBounds().overlaps(weaponBounds)) {
            boolean isFacingPlayer = isBossFacingPlayer(miniBossIceKnight, player);
            float distance = player.getPosition().dst(miniBossIceKnight.getPosition());
            float minDamageDistance = 20f;
            boolean isInAttackRange = distance <= minDamageDistance &&
                                    Math.abs(player.getPosition().y - miniBossIceKnight.getPosition().y) < 10f;
            boolean isWeaponInAttackPosition = false;
            if (miniBossIceKnight.isFacingLeft()) {
                isWeaponInAttackPosition = weaponBounds.x < miniBossIceKnight.getPosition().x;
            } else {
                isWeaponInAttackPosition = weaponBounds.x > miniBossIceKnight.getPosition().x;
            }
            if (miniBossIceKnight.isAttacking() &&
                player.getDamageCooldown() <= 0 &&
                isFacingPlayer &&
                isInAttackRange &&
                isWeaponInAttackPosition) {
                MeleeWeapon weapon = miniBossIceKnight.getWeapon();
                if (weapon != null) {
                    player.takeDamage(weapon.getDamage());
                    player.setDamageCooldown(1.0f);
                }
            }
        }
    }

    // Helper method to check if the boss is facing the player (MiniBossIceKnight version)
    private boolean isBossFacingPlayer(MiniBossIceKnight boss, Player player) {
        Vector2 bossPosition = boss.getPosition();
        Vector2 playerPosition = player.getPosition();
        boolean isBossFacingLeft = boss.isFacingLeft();
        boolean isPlayerToLeft = playerPosition.x < bossPosition.x;
        return (isBossFacingLeft && isPlayerToLeft) || (!isBossFacingLeft && !isPlayerToLeft);
    }
    // Overload for IceBoss
    private boolean isBossFacingPlayer(IceBoss boss, Player player) {
        Vector2 bossPosition = boss.getPosition();
        Vector2 playerPosition = player.getPosition();
        boolean isBossFacingLeft = boss.isFacingLeft();
        boolean isPlayerToLeft = playerPosition.x < bossPosition.x;
        return (isBossFacingLeft && isPlayerToLeft) || (!isBossFacingLeft && !isPlayerToLeft);
    }

    // Overload for MiniBossFirst
    private boolean isBossFacingPlayer(MiniBossFirst boss, Player player) {
        Vector2 bossPosition = boss.getPosition();
        Vector2 playerPosition = player.getPosition();
        boolean isBossFacingLeft = boss.isFacingLeft();
        boolean isPlayerToLeft = playerPosition.x < bossPosition.x;
        return (isBossFacingLeft && isPlayerToLeft) || (!isBossFacingLeft && !isPlayerToLeft);
    }

    // Method to check collision between player and fire miniboss (weapon only)
    private void checkPlayerFireMiniBossCollision(Rectangle weaponBounds) {
        if (!player.isAlive() || fireMiniBoss == null || !fireMiniBoss.isAlive()) return;
        // Check collision with weapon (MiniBossFirst logic)
        if (weaponBounds != null && player.getBounds().overlaps(weaponBounds)) {
            boolean isFacingPlayer = isBossFacingPlayer(fireMiniBoss, player);
            float distance = player.getPosition().dst(fireMiniBoss.getPosition());
            float minDamageDistance = 20f;
            boolean isInAttackRange = distance <= minDamageDistance &&
                                    Math.abs(player.getPosition().y - fireMiniBoss.getPosition().y) < 15f;
            boolean isWeaponInAttackPosition = false;
            if (fireMiniBoss.isFacingLeft()) {
                isWeaponInAttackPosition = weaponBounds.x < fireMiniBoss.getPosition().x;
            } else {
                isWeaponInAttackPosition = weaponBounds.x > fireMiniBoss.getPosition().x;
            }
            if (fireMiniBoss.isAttacking() &&
                player.getDamageCooldown() <= 0 &&
                isFacingPlayer &&
                isInAttackRange &&
                isWeaponInAttackPosition) {
                MeleeWeapon weapon = fireMiniBoss.getWeapon();
                if (weapon != null) {
                    player.takeDamage(weapon.getDamage());
                    player.setDamageCooldown(1.0f);
                    fireMiniBossWasHitThisAttack = true;
                }
            }
        }
    }

    // Check if player is in room 10 of level 3
    private boolean isPlayerInRoom10() {
        if (!isLevel3 || player == null) return false;

        // Room 10 boundaries
        float room10Left = 495f;
        float room10Right = 780f;
        float room10Bottom = 575f;
        float room10Top = 732f;

        // Get player position and dimensions
        Vector2 playerPos = player.getPosition();
        float playerWidth = player.getWidth();
        float playerHeight = player.getHeight();

        // Check if player is at least partially in room 10
        return playerPos.x + playerWidth > room10Left &&
               playerPos.x < room10Right &&
               playerPos.y + playerHeight > room10Bottom &&
               playerPos.y < room10Top;
    }

    // Move fireBoss back to center of room 10
    private void moveFireBossToCenter(float deltaTime) {
        if (fireBoss == null || !fireBoss.isAlive()) return;

        // Room 10 boundaries
        float room10Left = 495f;
        float room10Right = 780f;
        float room10Bottom = 575f;
        float room10Top = 732f;

        // Calculate center position
        float centerX = room10Left + (room10Right - room10Left) / 2f - fireBoss.getWidth() / 2f;
        float centerY = room10Bottom + (room10Top - room10Bottom) / 2f - fireBoss.getHeight() / 2f;

        // Get current position
        Vector2 currentPos = fireBoss.getPosition();

        // Calculate direction to center
        Vector2 direction = new Vector2(centerX - currentPos.x, centerY - currentPos.y);

        // If boss is close to center, just set position directly
        if (direction.len() < 5f) {
            currentPos.set(centerX, centerY);
            try {
                java.lang.reflect.Field velocityField = fireBoss.getClass().getSuperclass().getDeclaredField("velocity");
                velocityField.setAccessible(true);
                Vector2 velocity = (Vector2) velocityField.get(fireBoss);
                velocity.set(0, 0);
            } catch (Exception e) {
                // Ignore reflection errors
            }
        } else {
            // Move towards center
            direction.nor();
            try {
                // Access the velocity field using reflection
                java.lang.reflect.Field velocityField = fireBoss.getClass().getSuperclass().getDeclaredField("velocity");
                velocityField.setAccessible(true);
                Vector2 velocity = (Vector2) velocityField.get(fireBoss);

                // Get boss speed
                float bossSpeed = 50f; // Default speed
                try {
                    java.lang.reflect.Field speedField = fireBoss.getClass().getSuperclass().getDeclaredField("speed");
                    speedField.setAccessible(true);
                    bossSpeed = speedField.getFloat(fireBoss);
                } catch (Exception e) {
                    // Use default speed if reflection fails
                }

                // Set velocity to move towards center
                velocity.set(direction.scl(bossSpeed));
            } catch (Exception e) {
                // Fallback if reflection fails
                currentPos.add(direction.x * 50f * deltaTime, direction.y * 50f * deltaTime);
            }
        }
    }

    // Constrain fireBoss to room 10 boundaries
    private void constrainFireBossToRoom10() {
        if (fireBoss == null || !fireBoss.isAlive()) return;

        // Room 10 boundaries
        float room10Left = 495f;
        float room10Right = 780f;
        float room10Bottom = 575f;
        float room10Top = 732f;

        // Get current position and size
        Vector2 position = fireBoss.getPosition();
        float width = 0;
        float height = 0;

        try {
            java.lang.reflect.Field widthField = fireBoss.getClass().getSuperclass().getDeclaredField("width");
            java.lang.reflect.Field heightField = fireBoss.getClass().getSuperclass().getDeclaredField("height");
            widthField.setAccessible(true);
            heightField.setAccessible(true);
            width = widthField.getFloat(fireBoss);
            height = heightField.getFloat(fireBoss);
        } catch (Exception e) {
            // Fallback to getWidth/getHeight if reflection fails
            width = fireBoss.getWidth();
            height = fireBoss.getHeight();
        }

        // Constrain position to room boundaries
        if (position.x < room10Left) {
            position.x = room10Left;
            try {
                java.lang.reflect.Field velocityField = fireBoss.getClass().getSuperclass().getDeclaredField("velocity");
                velocityField.setAccessible(true);
                Vector2 velocity = (Vector2) velocityField.get(fireBoss);
                velocity.x = 0;
            } catch (Exception e) {
                // Ignore reflection errors
            }
        }
        if (position.x + width > room10Right) {
            position.x = room10Right - width;
            try {
                java.lang.reflect.Field velocityField = fireBoss.getClass().getSuperclass().getDeclaredField("velocity");
                velocityField.setAccessible(true);
                Vector2 velocity = (Vector2) velocityField.get(fireBoss);
                velocity.x = 0;
            } catch (Exception e) {
                // Ignore reflection errors
            }
        }
        if (position.y < room10Bottom) {
            position.y = room10Bottom;
            try {
                java.lang.reflect.Field velocityField = fireBoss.getClass().getSuperclass().getDeclaredField("velocity");
                velocityField.setAccessible(true);
                Vector2 velocity = (Vector2) velocityField.get(fireBoss);
                velocity.y = 0;
            } catch (Exception e) {
                // Ignore reflection errors
            }
        }
        if (position.y + height > room10Top) {
            position.y = room10Top - height;
            try {
                java.lang.reflect.Field velocityField = fireBoss.getClass().getSuperclass().getDeclaredField("velocity");
                velocityField.setAccessible(true);
                Vector2 velocity = (Vector2) velocityField.get(fireBoss);
                velocity.y = 0;
            } catch (Exception e) {
                // Ignore reflection errors
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        // Оновлюємо viewport при зміні розміру вікна
        viewport.update(width, height);
        camera.update();
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (player != null) {
            if (player.getTexture() != null) player.getTexture().dispose();
            player.dispose(); // Додаємо виклик dispose для гравця
        }
        if (sword != null) sword.dispose();
        if (boss != null) {
            if (boss.getTexture() != null) boss.getTexture().dispose();
            if (boss.getWeapon() != null) boss.getWeapon().dispose();
            boss.dispose(); // Виклик dispose для боса
        }
        if (miniBossFirst != null) {
            if (miniBossFirst.getTexture() != null) miniBossFirst.getTexture().dispose();
            if (miniBossFirst.getWeapon() != null) miniBossFirst.getWeapon().dispose();
            miniBossFirst.dispose(); // Виклик dispose для міні-боса
        }
        // Dispose portal texture
        if (portalTexture != null) portalTexture.dispose();

        if (trapTexture != null) trapTexture.dispose();

        if (potionTexture != null) potionTexture.dispose();

        if (map2Texture != null) {
            map2Texture.dispose();
        }

        font.dispose();

        if (loadingTexture != null) {
            loadingTexture.dispose();
        }
        if (loadingFont != null) {
            loadingFont.dispose();
        }

        if (gameOverTexture != null) {
            gameOverTexture.dispose();
        }

        if (iceBoss != null) {
            if (iceBoss.getTexture() != null) iceBoss.getTexture().dispose();
            if (iceBoss.getWeapon() != null) iceBoss.getWeapon().dispose();
            iceBoss.dispose();
        }

        if (miniBossIceKnight != null) {
            if (miniBossIceKnight.getTexture() != null) miniBossIceKnight.getTexture().dispose();
            if (miniBossIceKnight.getWeapon() != null) miniBossIceKnight.getWeapon().dispose();
            miniBossIceKnight.dispose();
        }

        if (iceSpirits != null) {
            for (IceSpirit spirit : iceSpirits) {
                spirit.dispose();
            }
        }
        if (iceSpiritTexture != null) {
            iceSpiritTexture.dispose();
        }
    }

    public void setLevel2() {
        isLevel2 = true;
        player.setLevel2Areas();
        player.setScale(LEVEL2_PLAYER_SCALE);
        player.setPosition(new Vector2(100, 100));
        player.restoreFullHealth();
        player.setSpeed(100000f);
        potions.clear();
        potions.add(new HealthPotion(260f, 70f, 20, 100, potionTexture));
        potions.add(new HealthPotion(260f, 300f, 20, 100, potionTexture));

        // Make sure player always has a sword
        if (player.getWeapon() == null) {
            if (sword == null) {
                if (Gdx.files.internal("images/weapons/sword.png").exists()) {
                    sword = new MeleeWeapon("Sword", 100, 1.0f, "images/weapons/sword.png");
                } else if (Gdx.files.internal("sword.png").exists()) {
                    sword = new MeleeWeapon("Sword", 100, 1.0f, "sword.png");
                } else {
                    sword = new MeleeWeapon("Sword", 100, 1.0f, "images/weapons/sword.png");
                }
            }
            player.equipWeapon(sword);
        }

        // Create and initialize Ice Boss in room 6
        float iceBossX = 580.76f + (100.86f - 64f) / 2f;
        float iceBossY = 625.40f + (57.40f - 64f) / 2f;
        iceBoss = new IceBoss(iceBossX, iceBossY);
        iceBoss.setTarget(player);

        // Load Ice Boss texture
        if (Gdx.files.internal("images/monsters/ice_boss.PNG").exists()) {
            Texture iceBossTexture = new Texture(Gdx.files.internal("images/monsters/ice_boss.PNG"));
            iceBoss.setTexture(iceBossTexture);
        } else {
            Texture fallbackTexture = player.getTexture();
            iceBoss.setTexture(fallbackTexture);
        }

        // Ice Boss uses the same weapon as the mini ice boss
        if (Gdx.files.internal("images/weapons/miniboss2weapon.png").exists()) {
            MeleeWeapon iceBossWeapon = new MeleeWeapon("Ice Boss Weapon", 30, 1.0f, "images/weapons/miniboss2weapon.png");
            iceBoss.equipWeapon(iceBossWeapon);
        } else {
            MeleeWeapon iceBossWeapon = new MeleeWeapon("Ice Boss Weapon", 30, 1.0f, "images/weapons/sword.png");
            iceBoss.equipWeapon(iceBossWeapon);
        }

        // Create and initialize Ice Knight mini-boss
        float iceKnightX = 385f + (360f - 80f) / 2;
        float iceKnightY = 210f + (215f - 80f) / 2;
        miniBossIceKnight = new MiniBossIceKnight(iceKnightX, iceKnightY);
        miniBossIceKnight.setTarget(player);

        // Load Ice Knight texture
        if (Gdx.files.internal("images/monsters/mini_boss_2.png").exists()) {
            Texture iceKnightTexture = new Texture(Gdx.files.internal("images/monsters/mini_boss_2.png"));
            miniBossIceKnight.setTexture(iceKnightTexture);
        } else if (Gdx.files.internal("images/enemies/mini_boss_2.png").exists()) {
            Texture iceKnightTexture = new Texture(Gdx.files.internal("images/enemies/mini_boss_2.png"));
            miniBossIceKnight.setTexture(iceKnightTexture);
        } else {
            // Use player texture as fallback and scale it to match the boss size
            Texture fallbackTexture = player.getTexture();
            miniBossIceKnight.setTexture(fallbackTexture);
        }

        // Load Ice Knight weapon
        if (Gdx.files.internal("images/weapons/miniboss2weapon.png").exists()) {
            MeleeWeapon iceKnightWeapon = new MeleeWeapon("Ice Knight Weapon", 40, 1.0f, "images/weapons/miniboss2weapon.png");
            miniBossIceKnight.equipWeapon(iceKnightWeapon);
        } else {
            // Use regular sword as fallback
            MeleeWeapon iceKnightWeapon = new MeleeWeapon("Ice Knight Weapon",40 , 1.0f, "images/weapons/sword.png");
            miniBossIceKnight.equipWeapon(iceKnightWeapon);
        }

        // Add Ice Knight to player's boss list for collision detection
        player.addBoss(miniBossIceKnight);
        player.addBoss(iceBoss);

        // Initialize ice spirits list and spawn timer
        iceSpirits = new ArrayList<>();
        iceSpiritSpawnTimer = 0f;
        iceSpiritsSpawned = 0;
    }

    private void addLevel2Traps() {
        if (trapTexture == null || traps == null) return;

        float trapSize = 20f;
        float corridorX = 610.79f;
        float startY = 420f;
        float gap = 75f;

        traps.add(new Trap(corridorX, startY, trapSize, 70, trapTexture));
        traps.add(new Trap(corridorX+30f, startY + gap, trapSize, 70, trapTexture));
        traps.add(new Trap(corridorX, startY + 2 * gap, trapSize, 70, trapTexture));

        float corridor2X = 327.92f;
        float corridor2Y = 65.94f;
        float gap2 = 60f;

        traps.add(new Trap(corridor2X, corridor2Y-7f, trapSize, 70, trapTexture));
        traps.add(new Trap(corridor2X+gap2, corridor2Y+15f, trapSize, 70, trapTexture));
        traps.add(new Trap(corridor2X+2*gap2, corridor2Y-7f, trapSize, 70, trapTexture));
    }

    public void setLevel3() {
        isLevel3 = true;
        isLevel2 = false;
        showPortal = false; // Ensure level 1 portal is not shown on level 3

        // Set level 3 background
        backgroundTexture = map3Texture;

        // Set player position for level 3
        player.getPosition().set(level3PlayerSpawn);

        // Set fixed position for level 3 portal to be at the same position as player spawn
        level3PortalPosition.set(level3PlayerSpawn.x, level3PlayerSpawn.y); // Portal at the same position as player spawn

        // Load portal texture if needed
        if (portalTexture == null) {
            try {
                portalTexture = new Texture(Gdx.files.internal("assets/images/environment/decorations/portal.png"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showLevel3Portal = true;

        // Restore player's health to full
        player.restoreFullHealth();

        // Make sure player always has a sword
        if (player.getWeapon() == null) {
            if (sword == null) {
                sword = new MeleeWeapon("Sword", 100, 1.0f, "images/weapons/sword.png");
            }
            player.equipWeapon(sword);
        }

        // Set level 3 playable areas to cover the entire map
        player.setLevel3Areas();

        // Apply level 2 scale to player and camera
        player.setScale(LEVEL2_PLAYER_SCALE);
        if (player.getWeapon() != null) {
            player.getWeapon().setScale(LEVEL2_PLAYER_SCALE);
        }
        player.setHealthBarScale(LEVEL2_PLAYER_SCALE);

        // Apply level 2 camera scale
        viewportWidth = worldWidth * 0.412f * LEVEL2_CAMERA_SCALE;
        viewportHeight = worldHeight * 0.412f * LEVEL2_CAMERA_SCALE;
        viewport.setWorldSize(viewportWidth, viewportHeight);
        viewport.apply();

        // Reset camera position to player
        camera.position.set(
            player.getPosition().x + player.getWidth() / 2,
            player.getPosition().y + player.getHeight() / 2,
            0
        );

        // Clear all level 2 entities
        traps.clear();
        potions.clear();
        if (iceSpirits != null) {
            iceSpirits.clear();
        }
        iceSpiritsSpawned = 0;

        // Reset portal state
        showLevel2Portal = false;
        canEnterLevel2Portal = false;
        if (portalTexture != null) {
            portalTexture.dispose();
            portalTexture = null;
        }

        // --- Додаємо мінібоса (fireenemy) у 6 кімнату ---
        float fireMiniBossX = 495f + (780f - 495f) / 2f - FIRE_MINIBOSS_SIZE / 2f;
        float fireMiniBossY = 256f + (399f - 256f) / 2f - FIRE_MINIBOSS_SIZE / 2f;
        fireMiniBoss = new MiniBossFirst(fireMiniBossX, fireMiniBossY) {
            {
                this.width = FIRE_MINIBOSS_SIZE;
                this.height = FIRE_MINIBOSS_SIZE;
                this.health = FIRE_MINIBOSS_HEALTH;
                this.maxHealth = FIRE_MINIBOSS_HEALTH;
            }
        };
        fireMiniBoss.setTarget(player);
        if (Gdx.files.internal("assets/images/monsters/fireenemy.png").exists()) {
            Texture fireTexture = new Texture(Gdx.files.internal("assets/images/monsters/fireenemy.png"));
            fireMiniBoss.setTexture(fireTexture);
        } else {
            fireMiniBoss.setTexture(player.getTexture());
        }
        if (Gdx.files.internal("assets/images/weapons/miniboss3weapon.png").exists()) {
            fireMiniBossWeapon = new MeleeWeapon("Fire MiniBoss Weapon", FIRE_MINIBOSS_DAMAGE, 1.2f, "assets/images/weapons/miniboss3weapon.png");
        } else {
            fireMiniBossWeapon = new MeleeWeapon("Fire MiniBoss Weapon", FIRE_MINIBOSS_DAMAGE, 1.2f, "images/weapons/sword.png");
        }
        fireMiniBoss.equipWeapon(fireMiniBossWeapon);
        player.addBoss(fireMiniBoss);

        // --- Додаємо fireBoss (DragonBoss) у 10 кімнату ---
        float fireBossSize = 84f; // трохи більший за мінібоса (70*1.2)
        float fireBossHealth = 800; // більше, ніж у мінібоса (250*3+)
        float fireBossSpeed = 90f; // трохи швидший
        int fireBossMoney = 700; // більше, ніж у мінібоса
        int fireBossWeaponDamage = 40; // більше, ніж у мінібоса
        float fireBossWeaponScale = 0.9f; // трохи менша зброя

        float room10Left = 495f;
        float room10Right = 780f;
        float room10Bottom = 575f;
        float room10Top = 732f;
        float fireBossX = (room10Left + room10Right - fireBossSize) / 2f;
        float fireBossY = (room10Bottom + room10Top) / 2f - fireBossSize / 2f - 60f;
        fireBoss = new DragonBoss(fireBossX, fireBossY);
        fireBoss.setTarget(player);
        fireBoss.setFireBoss(true); // Встановлюємо що це fireBoss
        fireBoss.setWidth(fireBossSize);
        fireBoss.setHeight(fireBossSize);
        fireBoss.setHealth((int)fireBossHealth);
        fireBoss.setMaxHealth((int)fireBossHealth);
        fireBoss.setSpeed(fireBossSpeed);
        fireBoss.setMoney(fireBossMoney);
        fireBoss.setMoneyDrop(fireBossMoney);

        if (Gdx.files.internal("assets/images/monsters/fireboss.png").exists()) {
            Texture fireBossTexture = new Texture(Gdx.files.internal("assets/images/monsters/fireboss.png"));
            fireBoss.setTexture(fireBossTexture);
        } else if (Gdx.files.internal("images/monsters/big_boss_1.PNG").exists()) {
            Texture fallbackTexture = new Texture(Gdx.files.internal("images/monsters/big_boss_1.PNG"));
            fireBoss.setTexture(fallbackTexture);
        }

        // Equip fireBoss with a weapon
        MeleeWeapon fireBossWeapon;
        if (Gdx.files.internal("assets/images/weapons/sword.png").exists()) {
            fireBossWeapon = new MeleeWeapon("Fire Boss Weapon", fireBossWeaponDamage, 1.5f, "assets/images/weapons/sword.png");
        } else {
            fireBossWeapon = new MeleeWeapon("Fire Boss Weapon", fireBossWeaponDamage, 1.5f, "images/weapons/sword.png");
        }
        fireBossWeapon.setScale(fireBossWeaponScale);
        fireBoss.equipWeapon(fireBossWeapon);

        player.addBoss(fireBoss);
        fireBossPlayerEnteredRoom = false;
    }
}
