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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixelteam.adventures.entities.HealthPotion;
import com.pixelteam.adventures.entities.Trap;
import com.pixelteam.adventures.entities.enemies.DragonBoss;
import com.pixelteam.adventures.entities.enemies.MiniBoss;
import com.pixelteam.adventures.entities.player.Player;
import com.pixelteam.adventures.weapons.MeleeWeapon;


public class PixelAdventuresGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private Player player;
    private MeleeWeapon sword;
    private DragonBoss boss;
    private MiniBoss miniBoss;
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
    private static final float LEVEL2_SCALE = 3.0f; // Scale factor for level 2
    private float viewportWidth;
    private float viewportHeight;

    private List<Trap> traps;
    private Texture trapTexture;
    private List<HealthPotion> potions;
    private Texture potionTexture;

    // New level transition variables
    private Texture map2Texture;
    private boolean isLevel2 = false;
    private Vector2 level2PlayerSpawn = new Vector2(995f, 65f); // Adjusted spawn position (770 + 50 = 820, 175 - 50 = 125)

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

    @Override
    public void create() {
        batch = new SpriteBatch();

        // Load loading screen resources
        loadingTexture = new Texture(Gdx.files.internal("images/environment/tiles/loadingscreen.png"));
        loadingFont = new BitmapFont();
        loadingFont.getData().setScale(2f);

        // Load game over screen
        gameOverTexture = new Texture(Gdx.files.internal("images/environment/tiles/gameoverscreen.png"));

        // Завантаження фонового зображення
        backgroundTexture = new Texture(Gdx.files.internal("images/environment/tiles/map1.png"));

        // Завантаження текстури гравця
        Texture playerTexture = new Texture(Gdx.files.internal("images/player/player.png"));

        // Створення гравця в лівій кімнаті
        float playerX = 35f + (310f - 64f) / 2; // Центр лівої кімнати мінус половина розміру гравця
        float playerY = 260f + (165f - 64f) / 2; // Центр лівої кімнати мінус половина розміру гравця
        player = new Player(playerX, playerY);
        player.setTexture(playerTexture);

        // Створення меча
        if (Gdx.files.internal("images/weapons/sword.png").exists()) {
            sword = new MeleeWeapon("Sword", 100, 1.0f, "images/weapons/sword.png");
        } else if (Gdx.files.internal("sword.png").exists()) {
            sword = new MeleeWeapon("Sword", 100, 1.0f, "sword.png");
        } else {
            sword = new MeleeWeapon("Sword", 100, 1.0f, "images/weapons/sword.png");
        }

        // Екіпіровка меча гравцем
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
        miniBoss = new MiniBoss(miniBossX, miniBossY);
        miniBoss.setTarget(player);

        // Add bosses to player's list for collision detection
        player.addBoss(boss);
        player.addBoss(miniBoss);

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
            miniBoss.setTexture(miniBossTexture);
        } else if (Gdx.files.internal("images/enemies/mini_boss.png").exists()) {
            Texture miniBossTexture = new Texture(Gdx.files.internal("images/enemies/mini_boss.png"));
            miniBoss.setTexture(miniBossTexture);
        } else if (Gdx.files.internal("images/enemies/orc.png").exists()) {
            Texture miniBossTexture = new Texture(Gdx.files.internal("images/enemies/orc.png"));
            miniBoss.setTexture(miniBossTexture);
        } else {
            // Використовуємо текстуру гравця як заглушку для міні-боса
            miniBoss.setTexture(playerTexture);
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
            miniBoss.equipWeapon(miniBossWeapon);
        } else if (Gdx.files.internal("images/weapons/axe.png").exists()) {
            MeleeWeapon miniBossWeapon = new MeleeWeapon("Mini Boss Axe", 20, 1.0f, "images/weapons/axe.png");
            miniBoss.equipWeapon(miniBossWeapon);
        } else {
            // Використовуємо звичайний меч як заглушку
            MeleeWeapon miniBossWeapon = new MeleeWeapon("Mini Boss Weapon", 20, 1.0f, "images/weapons/sword.png");
            miniBoss.equipWeapon(miniBossWeapon);
        }

        // Pixel perfect налаштування
        Gdx.graphics.setVSync(true);

        // Вимкнути згладжування для pixel art
        Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_NEAREST);
        Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_NEAREST);

        // Load portal texture
        portalTexture = new Texture(Gdx.files.internal("images/environment/decorations/portal.png"));

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

        // Load map2 texture
        map2Texture = new Texture(Gdx.files.internal("images/environment/tiles/map2.png"));

        font = new BitmapFont();
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
        if (miniBoss != null && miniBoss.isAlive() && player.isAlive()) {
            miniBoss.update(deltaTime, player);
        }

        // Перевірка колізій тільки якщо гравець живий
        if (player.isAlive()) {
            // Перевірка колізії між гравцем та головним босом
            if (boss != null && boss.isAlive()) {
                checkPlayerBossCollision(boss);
            }

            // Перевірка колізії між гравцем та міні-босом
            if (miniBoss != null && miniBoss.isAlive()) {
                checkPlayerMiniBossCollision();
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

        // Рендеринг пастки
        for (Trap trap : traps) {
            trap.render(batch);
        }

        // Рендеринг зілля
        for (HealthPotion potion : potions) {
            potion.render(batch);
        }

        // Check if DragonBoss is dead and render portal if it is
        if (boss != null && !boss.isAlive()) {
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
                isLevel2 = true;
                backgroundTexture = map2Texture;
                player.getPosition().set(level2PlayerSpawn);

                // Make player smaller on level 2
                float scaleFactor = 0.5f; // 50% of original size

                // Resize player dimensions
                float originalWidth = player.getWidth();
                float originalHeight = player.getHeight();
                player.setWidth(originalWidth * scaleFactor);
                player.setHeight(originalHeight * scaleFactor);

                // Scale all other player attributes (weapon, speed, damage)
                player.scaleAllAttributes(scaleFactor);

                // Reset camera position to player
                camera.position.set(
                    player.getPosition().x + player.getWidth() / 2,
                    player.getPosition().y + player.getHeight() / 2,
                    0
                );

                // Make camera closer to player on level 2
                camera.zoom = 0.4f; // Zoom in more by setting zoom to 0.4 (more zoomed in than before)

                // Hide portal after transition
                showPortal = false;

                // Remove all level 1 elements
                traps.clear();
                potions.clear();

                // Clear bosses from player's list
                player.setBosses(new ArrayList<>());

                // Set bosses to null to prevent rendering
                boss = null;
                miniBoss = null;
            }
        }

        // Render portal if it should be shown (before player so it appears behind)
        if (showPortal && portalTexture != null) {
            batch.draw(portalTexture, portalPosition.x, portalPosition.y, 120f, 220f);

            // Show interaction prompt if player is near portal
            if (canEnterPortal) {
                font.draw(batch, "Press E to enter", portalPosition.x, portalPosition.y - 20);
            }
        }

        // Рендеринг гравця (метод render вже перевіряє чи гравець живий)
        player.render(batch);

        // Рендеринг головного боса
        if (boss != null && boss.isAlive()) {
            boss.render(batch);
        }

        // Рендеринг міні-боса
        if (miniBoss != null && miniBoss.isAlive()) {
            miniBoss.render(batch);
        }

        // Рендеринг смужки здоров'я гравця завжди (навіть якщо мертвий, щоб показати 0 HP)
        player.renderHealthBar(batch);

        batch.end();
    }

    private void checkPlayerBossCollision(DragonBoss boss) {
        // Перевіряємо колізію тільки якщо обидва живі
        if (!player.isAlive() || !boss.isAlive()) return;

        if (player.getBounds().overlaps(boss.getBounds())) {
            // Розрахунок вектора відштовхуванняdd
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
    }

    private void checkPlayerMiniBossCollision() {
        // Перевіряємо колізію тільки якщо обидва живі
        if (!player.isAlive() || !miniBoss.isAlive()) return;

        if (player.getBounds().overlaps(miniBoss.getBounds())) {
            // Розрахунок вектора відштовхування
            Vector2 playerCenter = new Vector2(
                player.getPosition().x + player.getWidth() / 2,
                player.getPosition().y + player.getHeight() / 2
            );
            Vector2 miniBossCenter = new Vector2(
                miniBoss.getPosition().x + miniBoss.getWidth() / 2,
                miniBoss.getPosition().y + miniBoss.getHeight() / 2
            );

            Vector2 pushDirection = playerCenter.sub(miniBossCenter).nor();

            // Відштовхуємо гравця від міні-боса
            float pushDistance = 5.0f;
            player.getPosition().add(pushDirection.scl(pushDistance));

            // Додаткова перевірка меж після відштовхування
            player.checkBounds();
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
        if (miniBoss != null) {
            if (miniBoss.getTexture() != null) miniBoss.getTexture().dispose();
            if (miniBoss.getWeapon() != null) miniBoss.getWeapon().dispose();
            miniBoss.dispose(); // Виклик dispose для міні-боса
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
    }
}
