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
        // Тут буде ініціалізація PixelAdventures
        Gdx.app.log("PixelAdventures", "Гра запущена!");
    }

    @Override
    public void render() {
        // Темно-синій фон для pixel стилю
        ScreenUtils.clear(0.1f, 0.1f, 0.2f, 1);

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
