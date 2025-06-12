package com.pixelteam.adventures.entities.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pixelteam.adventures.entities.player.Player;
import com.pixelteam.adventures.utils.Stats;
import com.pixelteam.adventures.weapons.MeleeWeapon;

public class MiniBoss extends Boss {
    private static final float MINI_BOSS_SIZE = 70f; // Менший розмір ніж головний босс
    private static final int MINI_BOSS_HEALTH = 250;
    private static final float MINI_BOSS_SPEED = 75f; // Швидший за головного боса
    private static final float ATTACK_RANGE = 50f;
    private static final float ATTACK_COOLDOWN = 1.5f;
    private static final float WEAPON_SWING_COOLDOWN = 0.8f; // Частіше махає зброєю

    private float attackTimer;
    private Player target;
    private boolean isMovingTowardsPlayer;
    private float phaseTimer;
    private int swordHitCount; // Counter for sword hits
    private float damageCooldown; // Cooldown to prevent multiple hits in a single attack
    private MeleeWeapon weapon; // Зброя міні-боса
    private float weaponSwingTimer; // Таймер для махання зброєю
    private boolean facingLeft; // Напрямок погляду міні-боса
    private float weaponRotation; // Base rotation angle for weapon
    private boolean isAttacking; // Whether mini-boss is currently attacking
    private float weaponAttackAnimation; // Animation progress for weapon attack
    private boolean weaponSwingDirection; // Direction of weapon swing (true = forward, false = backward)
    private static Texture pixelTexture;

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

            // Update weapon swing timer
            if (weaponSwingTimer > 0) {
                weaponSwingTimer -= deltaTime;
            }

            // Update weapon animation
            updateWeaponAnimation(deltaTime);

            // Update weapon
            if (weapon != null) {
                weapon.update(deltaTime);
            }

            // Update position
            position.add(velocity.x * deltaTime, velocity.y * deltaTime);

            // Constrain to middle room
            constrainToMiddleRoom();
        }
    }

    // Implement the abstract method from Character
    @Override
    public void move(Vector2 direction) {
        if (!alive || !active) return;

        // Set velocity based on direction and speed
        velocity.set(direction).nor().scl(speed);

        // Визначення напрямку погляду
        if (direction.x < 0) {
            facingLeft = true;
        } else if (direction.x > 0) {
            facingLeft = false;
        }
    }

    public MiniBoss(float x, float y) {
        // Ініціалізуємо базові параметри
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
        this.width = MINI_BOSS_SIZE;
        this.height = MINI_BOSS_SIZE;
        this.health = MINI_BOSS_HEALTH;
        this.maxHealth = MINI_BOSS_HEALTH;
        this.speed = MINI_BOSS_SPEED;
        this.alive = true;
        this.active = true;
        this.money = 500; // Менше грошей ніж головний босс
        this.moneyDrop = 500;
        this.stats = new Stats();

        // Специфічні параметри міні-боса
        this.phase = 1;
        this.attackTimer = 0f;
        this.isMovingTowardsPlayer = false;
        this.phaseTimer = 0f;
        this.swordHitCount = 0; // Initialize sword hit counter
        this.damageCooldown = 0f; // Initialize damage cooldown
        this.weaponSwingTimer = 0f; // Initialize weapon swing timer
        this.facingLeft = false; // Initially facing right
        this.weaponRotation = 0f; // Initialize weapon base rotation
        this.isAttacking = false; // Initialize attack state
        this.weaponAttackAnimation = 0f; // Initialize attack animation
        this.weaponSwingDirection = true; // Initialize swing direction
    }

    public void update(float deltaTime, Player player) {
        if (!alive || !active) return;

        this.target = player;
        this.phaseTimer += deltaTime;

        // Зміна фази залежно від здоров'я
        if (health <= maxHealth * 0.6f && phase == 1) {
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

        // Update weapon swing timer
        if (weaponSwingTimer > 0) {
            weaponSwingTimer -= deltaTime;
        } else {
            // Махати зброєю автоматично
            if (weapon != null) {
                weapon.startAttack();
                isAttacking = true; // Start attack animation
                weaponSwingTimer = WEAPON_SWING_COOLDOWN;
            }
        }

        // Update weapon animation
        updateWeaponAnimation(deltaTime);

        // Update weapon
        if (weapon != null) {
            weapon.update(deltaTime);
        }

        // ШІ міні-боса
        updateAI(deltaTime);

        // Оновлення позиції
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);

        // Обмеження міні-боса в межах середньої кімнати
        constrainToMiddleRoom();
    }

    private void updateWeaponAnimation(float deltaTime) {
        // Weapon swing animation - 110 degrees swing (менше ніж у головного боса)
        if (isAttacking) {
            if (weaponSwingDirection) {
                // Swing forward
                weaponAttackAnimation += 900.0f * deltaTime; // Швидший рух
                if (weaponAttackAnimation >= 110.0f) {
                    weaponAttackAnimation = 110.0f;
                    weaponSwingDirection = false; // Start swinging back
                }
            } else {
                // Swing back to original position
                weaponAttackAnimation -= 900.0f * deltaTime;
                if (weaponAttackAnimation <= 0.0f) {
                    weaponAttackAnimation = 0.0f;
                    isAttacking = false;
                    weaponSwingDirection = true; // Reset for next attack
                }
            }
        }
    }

    private void updateAI(float deltaTime) {
        if (target == null) return;

        float distanceToPlayer = position.dst(target.getPosition());

        // Перевіряємо, чи гравець знаходиться в середній кімнаті
        if (isPlayerInMiddleRoom()) {
            if (distanceToPlayer > ATTACK_RANGE) {
                // Рухаємося до гравця тільки якщо він в середній кімнаті
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
            // Гравець не в середній кімнаті, повертаємося в центр кімнати
            moveToRoomCenter();
            isMovingTowardsPlayer = false;
        }

        // Використовуємо здібності періодично
        if (phaseTimer > 2.5f) {
            useAbility();
            phaseTimer = 0f;
        }
    }

    // Метод для перевірки, чи гравець знаходиться в середній кімнаті
    private boolean isPlayerInMiddleRoom() {
        if (target == null) return false;

        // Межі середньої кімнати (центральна кімната)
        float roomLeft = 385f;
        float roomRight = 385f + 360f;
        float roomBottom = 210f;
        float roomTop = 210f + 215f;

        // Отримуємо позицію гравця
        Vector2 playerPos = target.getPosition();
        float playerWidth = target.getWidth();
        float playerHeight = target.getHeight();

        // Перевіряємо, чи гравець хоча б частково знаходиться в середній кімнаті
        return playerPos.x + playerWidth > roomLeft &&
            playerPos.x < roomRight &&
            playerPos.y + playerHeight > roomBottom &&
            playerPos.y < roomTop;
    }

    private void moveTowardsPlayer() {
        if (target == null) return;

        Vector2 direction = new Vector2(target.getPosition()).sub(position).nor();
        move(direction); // Використовуємо метод move для автоматичного визначення напрямку
    }

    private void moveToRoomCenter() {
        // Розраховуємо центр середньої кімнати
        float roomLeft = 385f;
        float roomRight = 385f + 360f;
        float roomBottom = 210f;
        float roomTop = 210f + 215f;

        float centerX = roomLeft + (roomRight - roomLeft) / 2f - width / 2f;
        float centerY = roomBottom + (roomTop - roomBottom) / 2f - height / 2f;

        // Розраховуємо напрямок до центру кімнати
        Vector2 roomCenter = new Vector2(centerX, centerY);
        Vector2 direction = new Vector2(roomCenter).sub(position).nor();

        // Рухаємося до центру кімнати
        if (position.dst(roomCenter) > 5f) { // Якщо відстань більше 5 пікселів
            move(direction);
        } else {
            // Якщо ми вже в центрі, зупиняємося
            velocity.set(0, 0);
        }
    }

    private void constrainToMiddleRoom() {
        // Межі середньої кімнати для міні-боса
        float roomLeft = 385f;
        float roomRight = 385f + 360f;
        float roomBottom = 210f;
        float roomTop = 210f + 215f;

        // Обмежуємо позицію міні-боса в межах середньої кімнати
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
            // Малюємо міні-боса з урахуванням напрямку
            if (facingLeft) {
                // Відображаємо текстуру по горизонталі
                batch.draw(texture, position.x + width, position.y, -width, height);
            } else {
                // Звичайне відображення
                batch.draw(texture, position.x, position.y, width, height);
            }

            // Рендеринг зброї
            if (weapon != null) {
                renderWeapon(batch);
            }

            // Малюємо смужку здоров'я
            renderHealthBar(batch);
        }
    }

    private void renderWeapon(SpriteBatch batch) {
        if (weapon == null || weapon.getTexture() == null) return;

        // Розмір зброї (трохи менший для міні-боса)
        float weaponWidth = weapon.getWidth() * 0.7f;
        float weaponHeight = weapon.getHeight() * 0.7f;

        float offsetX;
        float offsetY;
        float totalRotation;

        if (facingLeft) {
            offsetX = -27.0f;
            offsetY = -7.0f;
            totalRotation = weaponRotation + weaponAttackAnimation;
        } else {
            offsetX = 27.0f;
            offsetY = -7.0f;
            totalRotation = weaponRotation - weaponAttackAnimation;
        }

        float weaponX = position.x + width / 2.0f + offsetX - weaponWidth / 2.0f;
        float weaponY = position.y + height / 2.0f + offsetY - weaponHeight / 2.0f;

        // Відображення зброї з урахуванням напрямку міні-боса
        batch.draw(weapon.getTexture(),
            weaponX, weaponY,
            weaponWidth / 2.0f, weaponHeight / 2.0f,
            weaponWidth, weaponHeight,
            1.0f, 1.0f,
            totalRotation,
            0, 0,
            weapon.getTexture().getWidth(), weapon.getTexture().getHeight(),
            false, false);
    }

    private void renderHealthBar(SpriteBatch batch) {
        // Параметри смужки здоров'я
        float barWidth = 70f;
        float barHeight = 8f;
        float barOffsetY = height;

        // Позиція смужки над міні-босом
        float barX = position.x + (width - barWidth) / 2f;
        float barY = position.y + barOffsetY;

        float healthPercent = (float) health / (float) maxHealth;

        // Малюємо фон смужки
        batch.setColor(0.2f, 0.2f, 0.2f, 0.8f);
        batch.draw(getPixelTexture(), barX - 2f, barY - 2f, barWidth + 4f, barHeight + 4f);

        // Основна смужка здоров'я (градієнт від зеленого до червоного)
        batch.setColor(1.0f - healthPercent, healthPercent, 0.0f, 1.0f);
        batch.draw(getPixelTexture(), barX, barY, barWidth * healthPercent, barHeight);

        // Повертаємо білий колір
        batch.setColor(1f, 1f, 1f, 1f);
    }

    public void attack() {
        if (target == null) return;

        // Атака - завдаємо шкоду якщо гравець поблизу
        float distanceToPlayer = position.dst(target.getPosition());
        if (distanceToPlayer <= ATTACK_RANGE) {
            target.takeDamage(15); // Міні-босс завдає менше шкоди ніж головний босс
        }

        // Також запускаємо атаку зброї
        if (weapon != null) {
            weapon.startAttack();
            isAttacking = true; // Start attack animation
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
            // Фаза 2: Швидша атака з меншою паузою
            if (target != null) {
                attack();
            }
        }
    }

    @Override
    public void changePhase() {
        phase = 2;
        speed = MINI_BOSS_SPEED * 1.3f; // Збільшуємо швидкість у другій фазі
        // Можна додати ефекти зміни фази
    }

    @Override
    public void dropLoot() {
        // Тут можна додати логіку випадання лутів
        // Поки що міні-босс просто дає менше грошей (вже встановлено в конструкторі)
    }

    @Override
    public void takeDamage(int damage) {
        // Only take damage if not in cooldown period
        if (damageCooldown <= 0) {
            health -= damage;

            // Increment sword hit counter
            swordHitCount++;

            // Set cooldown to prevent multiple hits in a single attack
            damageCooldown = 0.3f; // Менший кулдаун ніж у головного боса

            // Die after 3 sword hits or when health reaches 0 (легше вбити)
            if (swordHitCount >= 3 || health <= 0) {
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
        // Зменшуємо межі колізії, щоб гравець міг підійти ближче до міні-боса
        float collisionMargin = 35f; // Збільшуємо межу, щоб гравець міг підійти ще ближче
        return new Rectangle(
            position.x + collisionMargin,
            position.y + collisionMargin,
            width - 2 * collisionMargin,
            height - 2 * collisionMargin
        );
    }

    // Метод для екіпірування зброї
    public void equipWeapon(MeleeWeapon weapon) {
        this.weapon = weapon;
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

    public MeleeWeapon getWeapon() {
        return this.weapon;
    }

    public boolean isFacingLeft() {
        return this.facingLeft;
    }

    private Texture getPixelTexture() {
        if (pixelTexture == null) {
            com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
            pixmap.setColor(1, 1, 1, 1);
            pixmap.fill();
            pixelTexture = new Texture(pixmap);
            pixmap.dispose();
        }
        return pixelTexture;
    }

    public void dispose() {
        // Clean up any resources
        // Unlike DragonBoss, MiniBoss doesn't have a pixelTexture to dispose
        if (pixelTexture != null) {
            pixelTexture.dispose();
            pixelTexture = null;
        }
    }
}
