package com.pixelteam.adventures.entities.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pixelteam.adventures.entities.player.Player;
import com.pixelteam.adventures.utils.Stats;
import com.pixelteam.adventures.weapons.MeleeWeapon;

public class DragonBoss extends Boss {
    private static final float BOSS_SIZE = 128f; // Більший розмір для боса
    private static final int BOSS_HEALTH = 500;
    private static final float BOSS_SPEED = 50f;
    private static final float ATTACK_RANGE = 150f;
    private static final float ATTACK_COOLDOWN = 5.0f;
    private static final float WEAPON_SWING_COOLDOWN = 5.0f; // Махання зброєю раз на секунду

    private float attackTimer;
    private Player target;
    private boolean isMovingTowardsPlayer;
    private float phaseTimer;
    private int swordHitCount; // Counter for sword hits
    private float damageCooldown; // Cooldown to prevent multiple hits in a single attack
    private MeleeWeapon weapon; // Зброя боса
    private float weaponSwingTimer; // Таймер для махання зброєю
    private boolean facingLeft; // Напрямок погляду боса
    private float weaponRotation; // Base rotation angle for weapon
    private boolean isAttacking; // Whether boss is currently attacking
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

            // Constrain to right room (expanded)
            constrainToRightRoom();
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

        // Update weapon swing timer
        if (weaponSwingTimer > 0) {
            weaponSwingTimer -= deltaTime;
        } else {
            // Махати зброєю автоматично кожну секунду
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

        // ШІ боса
        updateAI(deltaTime);

        // Оновлення позиції
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);

        // Обмеження боса в межах правої кімнати (розширено)
        constrainToRightRoom();
    }

    private void updateWeaponAnimation(float deltaTime) {
        // Weapon swing animation - 130 degrees swing
        if (isAttacking) {
            if (weaponSwingDirection) {
                // Swing forward
                weaponAttackAnimation += 800.0f * deltaTime; // Speed of swing
                if (weaponAttackAnimation >= 130.0f) {
                    weaponAttackAnimation = 130.0f;
                    weaponSwingDirection = false; // Start swinging back
                }
            } else {
                // Swing back to original position
                weaponAttackAnimation -= 800.0f * deltaTime;
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

    // Метод для перевірки, чи гравець знаходиться в правій кімнаті (розширено)
    private boolean isPlayerInRightRoom() {
        if (target == null) return false;

        // Розширені межі правої кімнати
        float roomLeft = 785f; // Розширено на 50 пікселів вліво
        float roomRight = 835f + 360f; // Розширено на 50 пікселів вправо
        float roomBottom = 210f; // Розширено на 50 пікселів вниз
        float roomTop = 260f + 215f; // Розширено на 50 пікселів вгору

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
        move(direction); // Використовуємо метод move для автоматичного визначення напрямку
    }

    private void constrainToRightRoom() {
        // Розширені межі правої кімнати для боса
        float roomLeft = 735f; // Розширено на 50 пікселів вліво
        float roomRight = 835f + 360f; // Розширено на 50 пікселів вправо
        float roomBottom = 130f; // Розширено на 50 пікселів вниз
        float roomTop = 260f + 215f; // Розширено на 50 пікселів вгору

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
            // Малюємо боса з урахуванням напрямку
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

        // Розмір зброї (масштабуємо для боса)
        float weaponWidth = (float) (weapon.getWidth()*1.3);
        float weaponHeight = (float) (weapon.getHeight()*1.3);

        float offsetX;
        float offsetY;
        float totalRotation;

        if (facingLeft) {
            offsetX = -54.0f;
            offsetY = -15.0f;
            totalRotation = weaponRotation + weaponAttackAnimation;
        } else {
            offsetX = 62.0f;
            offsetY = -15.0f;
            totalRotation = weaponRotation - weaponAttackAnimation;
        }

        float weaponX = position.x + width / 2.0f + offsetX - weaponWidth / 2.0f;
        float weaponY = position.y + height / 2.0f + offsetY - weaponHeight / 2.0f;

        // Відображення зброї з урахуванням напрямку боса (як у гравця)
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
        float barWidth = 70f; // Ширина смужки
        float barHeight = 8f; // Висота смужки
        float barOffsetY = height;

        // Позиція смужки (по центру над босом)
        float barX = position.x + (width - barWidth) / 2f;
        float barY = position.y + barOffsetY;

        // Відсоток здоров'я
        float healthPercent = (float) health / (float) maxHealth;

        // Кольори для смужки
        float redBackground = 0.2f, greenBackground = 0.2f, blueBackground = 0.2f, alphaBackground = 0.8f;
        float redHealth = 1.0f - healthPercent; // Червоний колір збільшується при зменшенні здоров'я
        float greenHealth = healthPercent; // Зелений колір зменшується при зменшенні здоров'я
        float blueHealth = 0.0f;
        float alphaHealth = 1.0f;

        // Малюємо фон смужки (темний прямокутник)
        batch.setColor(redBackground, greenBackground, blueBackground, alphaBackground);
        batch.draw(getPixelTexture(), barX - 2f, barY - 2f, barWidth + 4f, barHeight + 4f);

        // Малюємо основну смужку здоров'я
        batch.setColor(redHealth, greenHealth, blueHealth, alphaHealth);
        batch.draw(getPixelTexture(), barX, barY, barWidth * healthPercent, barHeight);

        // Повертаємо білий колір для нормального рендерингу інших об'єктів
        batch.setColor(1f, 1f, 1f, 1f);
    }

    public void attack() {
        if (target == null) return;

        // Простата атака - завдаємо шкоду якщо гравець поблизу
        float distanceToPlayer = position.dst(target.getPosition());

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
    private Texture getPixelTexture() {
        if (pixelTexture == null) {
            // Створюємо 1x1 білу текстуру
            com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
            pixmap.setColor(1, 1, 1, 1); // Білий колір
            pixmap.fill();
            pixelTexture = new Texture(pixmap);
            pixmap.dispose();
        }
        return pixelTexture;
    }

    public void dispose() {
        if (pixelTexture != null) {
            pixelTexture.dispose();
            pixelTexture = null;
        }
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
}
