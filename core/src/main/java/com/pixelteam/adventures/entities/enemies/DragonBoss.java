package com.pixelteam.adventures.entities.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pixelteam.adventures.entities.player.Player;
import com.pixelteam.adventures.utils.Stats;

public class DragonBoss extends Boss {
    private static final float BOSS_SIZE = 128f; // Більший розмір для боса
    private static final int BOSS_HEALTH = 500;
    private static final float BOSS_SPEED = 50f;
    private static final float ATTACK_RANGE = 150f;
    private static final float ATTACK_COOLDOWN = 2.0f;

    private float attackTimer;
    private Player target;
    private boolean isMovingTowardsPlayer;
    private float phaseTimer;
    private int swordHitCount; // Counter for sword hits
    private float damageCooldown; // Cooldown to prevent multiple hits in a single attack

    // Implement the abstract method from GameObject
    @Override
    public void update(float deltaTime) {
        if (target != null) {
            update(deltaTime, target);
        } else {
            // Basic update if no target
            if (!alive || !active) return;

            // Update attack timer
            if (attackTimer > 0) {
                attackTimer -= deltaTime;
            }

            // Update damage cooldown
            if (damageCooldown > 0) {
                damageCooldown -= deltaTime;
            }

            // Update position
            position.add(velocity.x * deltaTime, velocity.y * deltaTime);

            // Constrain to right room
            constrainToRightRoom();
        }
    }

    // Implement the abstract method from Character
    @Override
    public void move(Vector2 direction) {
        if (!alive || !active) return;

        // Set velocity based on direction and speed
        velocity.set(direction).nor().scl(speed);
    }

    public DragonBoss(float x, float y) {
        // Ініціалізуємо базові параметри
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
        this.width = BOSS_SIZE;
        this.height = BOSS_SIZE;
        this.health = BOSS_HEALTH;
        this.maxHealth = BOSS_HEALTH;
        this.speed = BOSS_SPEED;
        this.alive = true;
        this.active = true;
        this.money = 1000; // Багато грошей за боса
        this.moneyDrop = 1000;
        this.stats = new Stats();

        // Специфічні параметри боса
        this.phase = 1;
        this.attackTimer = 0f;
        this.isMovingTowardsPlayer = false;
        this.phaseTimer = 0f;
        this.swordHitCount = 0; // Initialize sword hit counter
        this.damageCooldown = 0f; // Initialize damage cooldown
    }

    public void update(float deltaTime, Player player) {
        if (!alive || !active) return;

        this.target = player;
        this.phaseTimer += deltaTime;

        // Зміна фази залежно від здоров'я
        if (health <= maxHealth * 0.5f && phase == 1) {
            changePhase();
        }

        // Оновлення таймеру атаки
        if (attackTimer > 0) {
            attackTimer -= deltaTime;
        }

        // Update damage cooldown
        if (damageCooldown > 0) {
            damageCooldown -= deltaTime;
        }

        // ШІ боса
        updateAI(deltaTime);

        // Оновлення позиції
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);

        // Обмеження боса в межах правої кімнати
        constrainToRightRoom();
    }

    private void updateAI(float deltaTime) {
        if (target == null) return;

        float distanceToPlayer = position.dst(target.getPosition());

        // Перевіряємо, чи гравець знаходиться в правій кімнаті
        if (isPlayerInRightRoom()) {
            if (distanceToPlayer > ATTACK_RANGE) {
                // Рухаємося до гравця тільки якщо він в правій кімнаті
                moveTowardsPlayer();
                isMovingTowardsPlayer = true;
            } else {
                // В межах атаки
                velocity.set(0, 0);
                isMovingTowardsPlayer = false;

                // Атакуємо якщо можемо
                if (attackTimer <= 0) {
                    attack();
                    attackTimer = ATTACK_COOLDOWN;
                }
            }
        } else {
            // Гравець не в правій кімнаті, зупиняємося
            velocity.set(0, 0);
            isMovingTowardsPlayer = false;
        }

        // Використовуємо здібності періодично
        if (phaseTimer > 3.0f) {
            useAbility();
            phaseTimer = 0f;
        }
    }

    // Метод для перевірки, чи гравець знаходиться в правій кімнаті
    private boolean isPlayerInRightRoom() {
        if (target == null) return false;

        // Межі правої кімнати (з Player класу)
        float roomLeft = 835f;
        float roomRight = 835f + 310f;
        float roomBottom = 260f;
        float roomTop = 260f + 165f;

        // Отримуємо позицію гравця
        Vector2 playerPos = target.getPosition();
        float playerWidth = target.getWidth();
        float playerHeight = target.getHeight();

        // Перевіряємо, чи гравець хоча б частково знаходиться в правій кімнаті
        return playerPos.x + playerWidth > roomLeft &&
               playerPos.x < roomRight &&
               playerPos.y + playerHeight > roomBottom &&
               playerPos.y < roomTop;
    }

    private void moveTowardsPlayer() {
        if (target == null) return;

        Vector2 direction = new Vector2(target.getPosition()).sub(position).nor();
        velocity.set(direction).scl(speed);
    }

    private void constrainToRightRoom() {
        // Межі правої кімнати (з Player класу)
        float roomLeft = 835f;
        float roomRight = 835f + 310f;
        float roomBottom = 260f;
        float roomTop = 260f + 165f;

        // Обмежуємо позицію боса в межах правої кімнати
        if (position.x < roomLeft) {
            position.x = roomLeft;
            velocity.x = 0;
        }
        if (position.x + width > roomRight) {
            position.x = roomRight - width;
            velocity.x = 0;
        }
        if (position.y < roomBottom) {
            position.y = roomBottom;
            velocity.y = 0;
        }
        if (position.y + height > roomTop) {
            position.y = roomTop - height;
            velocity.y = 0;
        }
    }

    public void render(SpriteBatch batch) {
        if (texture != null && alive) {
            // Малюємо боса
            batch.draw(texture, position.x, position.y, width, height);

            // Малюємо смужку здоров'я
            renderHealthBar(batch);
        }
    }

    private void renderHealthBar(SpriteBatch batch) {
        // Тут можна додати візуалізацію смужки здоров'я
        // Поки що залишимо порожнім, оскільки потрібні додаткові текстури
    }

    public void attack() {
        if (target == null) return;

        // Простата атака - завдаємо шкоду якщо гравець поблизу
        float distanceToPlayer = position.dst(target.getPosition());
        if (distanceToPlayer <= ATTACK_RANGE) {
            target.takeDamage(25); // Босс завдає багато шкоди
        }
    }

    @Override
    public void useAbility() {
        if (phase == 1) {
            // Фаза 1: Швидка атака
            if (target != null) {
                attack();
            }
        } else if (phase == 2) {
            // Фаза 2: Подвійна атака
            if (target != null) {
                attack();
                // Додаткова атака через невелику затримку
                attack();
            }
        }
    }

    @Override
    public void changePhase() {
        phase = 2;
        speed = BOSS_SPEED * 1.5f; // Збільшуємо швидкість у другій фазі
        // Можна додати ефекти зміни фази
    }

    @Override
    public void dropLoot() {
        // Тут можна додати логіку випадання лутів
        // Поки що босс просто дає багато грошей (вже встановлено в конструкторі)
    }

    @Override
    public void takeDamage(int damage) {
        // Only take damage if not in cooldown period
        if (damageCooldown <= 0) {
            health -= damage;

            // Increment sword hit counter
            swordHitCount++;

            // Set cooldown to prevent multiple hits in a single attack
            damageCooldown = 0.5f; // Half a second cooldown

            // Die after 5 sword hits or when health reaches 0
            if (swordHitCount >= 5 || health <= 0) {
                die();
            }
        }
    }

    @Override
    public void die() {
        alive = false;
        dropLoot();
    }

    public Rectangle getBounds() {
        // Зменшуємо межі колізії, щоб гравець міг підійти ближче до боса
        float collisionMargin = 30f; // Зменшуємо межі на 30 пікселів з кожного боку
        return new Rectangle(
            position.x + collisionMargin,
            position.y + collisionMargin,
            width - 2 * collisionMargin,
            height - 2 * collisionMargin
        );
    }

    // Getters and Setters
    public boolean isMovingTowardsPlayer() {
        return isMovingTowardsPlayer;
    }

    public int getPhase() {
        return phase;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setTarget(Player target) {
        this.target = target;
    }

    public Player getTarget() {
        return this.target;
    }

    public boolean isAlive() {
        return this.alive;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }
}
