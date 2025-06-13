package com.pixelteam.adventures;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixelteam.adventures.entities.player.Player;
import com.pixelteam.adventures.entities.enemies.DragonBoss;
import com.pixelteam.adventures.entities.enemies.MiniBoss;
import com.pixelteam.adventures.weapons.MeleeWeapon;
import com.pixelteam.adventures.entities.Trap;
import java.util.ArrayList;
import java.util.List;

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

    // Camera system
    private OrthographicCamera camera;
    private Viewport viewport;
    private float worldWidth;
    private float worldHeight;

    private List<Trap> traps;
    private Texture trapTexture;

    @Override
    public void create() {
        batch = new SpriteBatch();

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

        float viewportWidth = worldWidth * 0.412f;
        float viewportHeight = worldHeight * 0.412f;

        camera = new OrthographicCamera();
        viewport = new FitViewport(viewportWidth, viewportHeight, camera);
        viewport.apply();

        // Встановлюємо початкову позицію камери на гравця
        camera.position.set(
            player.getPosition().x + player.getWidth() / 2,
            player.getPosition().y + player.getHeight() / 2,
            0
        );
    }

    @Override
    public void render() {
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

        // Очищаємо екран
        ScreenUtils.clear(0, 0, 0, 1);

        // Встановлюємо проекцію камери для SpriteBatch
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Малюємо фонове зображення в оригінальному розмірі
        batch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);

        // Рендеринг пастки
        for (Trap trap : traps) {
            trap.render(batch);
        }

        // Check if DragonBoss is dead and render portal if it is
        if (boss != null && !boss.isAlive()) {
            // Show portal only after DragonBoss is defeated
            showPortal = true;
        }

        // Render portal if it should be shown (before player so it appears behind)
        if (showPortal && portalTexture != null) {
            batch.draw(portalTexture, portalPosition.x, portalPosition.y, 120f, 220f);
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

        // Якщо гравець мертвий, можна показати повідомлення "Game Over"
        if (!player.isAlive()) {
            renderGameOver(batch);
        }

        batch.end();
    }

    // Додайте цей метод до основного класу гри (опціонально)
    private void renderGameOver(SpriteBatch batch) {
        // Тут можна додати текст "Game Over" або інший UI елемент
        // Поки що просто залишимо порожнім, оскільки для тексту потрібен BitmapFont
        // System.out.println("Game Over!"); // Для налагодження
    }

    private void checkPlayerBossCollision(DragonBoss boss) {
        // Перевіряємо колізію тільки якщо обидва живі
        if (!player.isAlive() || !boss.isAlive()) return;

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
    }
}
