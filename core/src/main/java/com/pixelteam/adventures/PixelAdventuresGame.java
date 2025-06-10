package com.pixelteam.adventures;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.pixelteam.adventures.entities.player.Player;
import com.pixelteam.adventures.entities.enemies.DragonBoss;
import com.pixelteam.adventures.weapons.MeleeWeapon;

public class PixelAdventuresGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private Player player;
    private MeleeWeapon sword;
    private DragonBoss boss;
    private float deltaTime;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // Завантаження фонового зображення
        backgroundTexture = new Texture(Gdx.files.internal("images/environment/tiles/map1.png"));

        // Завантаження текстури гравця
        Texture playerTexture = new Texture(Gdx.files.internal("images/player/player.png"));

        // Створення гравця
        player = new Player(Gdx.graphics.getWidth() / 2 - 32, Gdx.graphics.getHeight() / 2 - 32);
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

        // Створення боса в правій кімнаті
        // Позиція в центрі правої кімнати
        float bossX = 835f + (310f - 128f) / 2; // Центр правої кімнати мінус половина розміру боса
        float bossY = 260f + (165f - 128f) / 2; // Центр правої кімнати мінус половина розміру боса
        boss = new DragonBoss(bossX, bossY);
        boss.setTarget(player);

        // Add boss to player's list of bosses for collision detection
        player.addBoss(boss);

        // Завантаження текстури боса
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

        // Завантаження зброї боса
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

        // Pixel perfect налаштування
        Gdx.graphics.setVSync(true);

        // Вимкнути згладжування для pixel art
        Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_NEAREST);
        Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_NEAREST);
    }

    @Override
    public void render() {
        // Оновлення deltaTime
        deltaTime = Gdx.graphics.getDeltaTime();

        // Оновлення гравця (навіть якщо мертвий, для правильної обробки анімацій)
        player.update(deltaTime);

        // Оновлення боса тільки якщо гравець живий
        if (boss != null && boss.isAlive() && player.isAlive()) {
            boss.update(deltaTime, player);

            // Перевірка колізії між гравцем та босом (тільки якщо гравець живий)
            checkPlayerBossCollision();
        }

        // Очищаємо екран
        ScreenUtils.clear(0, 0, 0, 1);

        batch.begin();
        // Малюємо фонове зображення на весь екран
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Рендеринг гравця (метод render вже перевіряє чи гравець живий)
        player.render(batch);

        // Рендеринг боса
        if (boss != null && boss.isAlive()) {
            boss.render(batch);
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

    private void checkPlayerBossCollision() {
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
    }
}
