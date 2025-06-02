package com.pixelteam.adventures;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class PixelAdventuresGame extends ApplicationAdapter {
    SpriteBatch batch;
    Texture playerTexture;


    @Override
    public void create() {
        batch = new SpriteBatch();

        // Pixel perfect налаштування
        Gdx.graphics.setVSync(true);

        // Вимкнути згладжування для pixel art
        Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_NEAREST);
        Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_NEAREST);
    }

    @Override
    public void render() {
        // !!! ЗМІНА ТУТ: Встановлюємо червоний колір фону
        // Значення 1f, 0f, 0f, 1f відповідають R (червоний), G (зелений), B (синій) і A (альфа/прозорість)
        ScreenUtils.clear(1f, 0f, 0f, 1f); // Яскраво-червоний фон

        batch.begin();
        // Тут буде рендеринг PixelAdventures
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (playerTexture != null) playerTexture.dispose();
    }


}
