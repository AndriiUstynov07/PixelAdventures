package com.pixelteam.adventures;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.pixelteam.adventures.entities.player.Player;
import com.pixelteam.adventures.weapons.MeleeWeapon;

public class PixelAdventuresGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private Player player;
    private MeleeWeapon sword;
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
        // Try different paths to ensure the sword texture is loaded
        if (Gdx.files.internal("images/weapons/sword.png").exists()) {
            sword = new MeleeWeapon("Sword", 10, 1.0f, "images/weapons/sword.png");
        } else if (Gdx.files.internal("sword.png").exists()) {
            sword = new MeleeWeapon("Sword", 10, 1.0f, "sword.png");
        } else {
            sword = new MeleeWeapon("Sword", 10, 1.0f, "images/weapons/sword.png");
        }

        // Екіпіровка меча гравцем
        player.equipWeapon(sword);

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

        // Оновлення гравця
        player.update(deltaTime);

        // Очищаємо екран
        ScreenUtils.clear(0, 0, 0, 1);

        batch.begin();
        // Малюємо фонове зображення на весь екран
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Рендеринг гравця
        player.render(batch);

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (player != null && player.getTexture() != null) player.getTexture().dispose();
        if (sword != null) sword.dispose();
    }
}
