package com.pixelteam.adventures.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pixelteam.adventures.entities.player.Player;

public class IceSpirit {
    private Vector2 position;
    private float width;
    private float height;
    private Texture texture;
    private Player target;
    private float speed;
    private boolean alive;
    private float damageTimer;
    private static final float DAMAGE_INTERVAL = 0.3f;
    private static final int DAMAGE = 30;
    private static final float SPIRIT_SIZE = 40f;
    private static final float SPIRIT_SPEED = 50f;

    public IceSpirit(float x, float y) {
        this.position = new Vector2(x, y);
        this.width = SPIRIT_SIZE;
        this.height = SPIRIT_SIZE;
        this.speed = SPIRIT_SPEED;
        this.alive = true;
        this.damageTimer = 0f;
    }

    public void update(float deltaTime) {
        if (!alive || target == null) return;

        // Рухаємося до гравця
        Vector2 direction = new Vector2(
            target.getPosition().x - position.x,
            target.getPosition().y - position.y
        ).nor();

        position.add(direction.scl(speed * deltaTime));

        // Оновлюємо таймер шкоди
        if (damageTimer > 0) {
            damageTimer -= deltaTime;
        }
    }

    public void render(SpriteBatch batch) {
        if (!alive || texture == null) return;
        batch.draw(texture, position.x, position.y, width, height);
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public void setTarget(Player target) {
        this.target = target;
    }

    public Rectangle getBounds() {
        return new Rectangle(position.x, position.y, width, height);
    }

    public void takeDamage(int damage) {
        alive = false;
    }

    public boolean isAlive() {
        return alive;
    }

    public void checkPlayerCollision(Player player) {
        if (!alive || !player.isAlive()) return;

        if (getBounds().overlaps(player.getBounds())) {
            if (damageTimer <= 0) {
                player.takeDamage(DAMAGE);
                damageTimer = DAMAGE_INTERVAL;
            }
        }
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
} 