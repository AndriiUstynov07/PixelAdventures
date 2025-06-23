package com.pixelteam.adventures.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pixelteam.adventures.entities.player.Player;
import com.pixelteam.adventures.utils.Stats;
import com.pixelteam.adventures.weapons.MeleeWeapon;

public class FireBoss extends DragonBoss {
    private static final float FIRE_BOSS_SIZE = 84f;
    private static final int FIRE_BOSS_HEALTH = 800;
    private static final float FIRE_BOSS_SPEED = 90f;
    private static final float ATTACK_RANGE = 150f;
    private static final float ATTACK_COOLDOWN = 5.0f;
    private static final float WEAPON_SWING_COOLDOWN = 5.0f;

    public FireBoss(float x, float y) {
        super(x, y);
        // Встановлюємо специфічні параметри для FireBoss
        this.width = FIRE_BOSS_SIZE;
        this.height = FIRE_BOSS_SIZE;
        this.health = FIRE_BOSS_HEALTH;
        this.maxHealth = FIRE_BOSS_HEALTH;
        this.speed = FIRE_BOSS_SPEED;
        this.money = 700;
        this.moneyDrop = 700;
    }

    @Override
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
            // Махати зброєю автоматично
            if (weapon != null) {
                weapon.startAttack();
                isAttacking = true;
                weaponSwingTimer = WEAPON_SWING_COOLDOWN;
            }
        }

        // Update weapon animation
        updateWeaponAnimation(deltaTime);

        // Update weapon
        if (weapon != null) {
            weapon.update(deltaTime);
        }

        // AI для FireBoss
        updateFireBossAI(deltaTime);

        // Оновлення позиції
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);
        constrainToRoom10();
    }

    private void updateFireBossAI(float deltaTime) {
        if (target == null) return;

        float distanceToPlayer = position.dst(target.getPosition());

        // Перевіряємо, чи гравець знаходиться в кімнаті 10
        if (isPlayerInRoom10()) {
            if (distanceToPlayer > ATTACK_RANGE) {
                // Рухаємося до гравця
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
            // Гравець не в кімнаті 10, повертаємося в центр кімнати
            moveToRoom10Center();
            isMovingTowardsPlayer = false;
        }

        // Використовуємо здібності періодично
        if (phaseTimer > 3.0f) {
            useAbility();
            phaseTimer = 0f;
        }
    }

    private boolean isPlayerInRoom10() {
        if (target == null) return false;

        // Межі кімнати 10
        float roomLeft = 495f;
        float roomRight = 780f;
        float roomBottom = 575f;
        float roomTop = 732f;

        // Отримуємо позицію гравця
        Vector2 playerPos = target.getPosition();
        float playerWidth = target.getWidth();
        float playerHeight = target.getHeight();

        // Перевіряємо, чи гравець хоча б частково знаходиться в кімнаті 10
        return playerPos.x + playerWidth > roomLeft &&
               playerPos.x < roomRight &&
               playerPos.y + playerHeight > roomBottom &&
               playerPos.y < roomTop;
    }

    private void moveTowardsPlayer() {
        if (target == null) return;

        Vector2 direction = new Vector2(target.getPosition()).sub(position).nor();
        move(direction);
    }

    private void moveToRoom10Center() {
        // Розраховуємо центр кімнати 10
        float roomLeft = 495f;
        float roomRight = 780f;
        float roomBottom = 575f;
        float roomTop = 732f;

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

    private void constrainToRoom10() {
        // Межі кімнати 10 для FireBoss
        float roomLeft = 495f;
        float roomRight = 780f;
        float roomBottom = 575f;
        float roomTop = 732f;

        // Обмежуємо позицію FireBoss в межах кімнати 10
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

    @Override
    public void render(SpriteBatch batch) {
        if (texture != null && alive) {
            // Малюємо FireBoss з урахуванням напрямку
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

        // Розмір зброї (масштабуємо для FireBoss)
        float weaponWidth = weapon.getWidth() * weapon.getScale();
        float weaponHeight = weapon.getHeight() * weapon.getScale();

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
        float barWidth = width * 0.8f; // Ширина смужки 80% ширини боса
        float barHeight = 8f; // Висота смужки
        float barOffsetY = height;

        // Позиція смужки (по центру над босом)
        float barX = position.x + (width - barWidth) / 2f;
        float barY = position.y + barOffsetY;

        // Відсоток здоров'я
        float healthPercent = (float) health / (float) maxHealth;

        // Кольори для смужки
        float redBackground = 0.2f, greenBackground = 0.2f, blueBackground = 0.2f, alphaBackground = 0.8f;
        float redHealth = 1.0f - healthPercent;
        float greenHealth = healthPercent;
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

    @Override
    public void attack() {
        if (target == null) return;

        // Проста атака - завдаємо шкоду якщо гравець поблизу
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
        speed = FIRE_BOSS_SPEED * 1.5f; // Збільшуємо швидкість у другій фазі
    }

    @Override
    public void dropLoot() {
        // Тут можна додати логіку випадання лутів
    }

    @Override
    public void takeDamage(int damage) {
        // Only take damage if not in cooldown period
        if (damageCooldown <= 0) {
            health -= damage;
            // Set cooldown to prevent multiple hits in a single attack
            damageCooldown = 0.5f; // Half a second cooldown
            // Die only when health reaches 0
            if (health <= 0) {
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

    @Override
    public void dispose() {
        if (pixelTexture != null) {
            pixelTexture.dispose();
            pixelTexture = null;
        }
    }

    @Override
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

    @Override
    public void equipWeapon(MeleeWeapon weapon) {
        this.weapon = weapon;
    }

    @Override
    public boolean isMovingTowardsPlayer() {
        return isMovingTowardsPlayer;
    }

    @Override
    public int getPhase() {
        return phase;
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public void setTarget(Player target) {
        this.target = target;
    }

    @Override
    public Player getTarget() {
        return this.target;
    }

    @Override
    public boolean isAlive() {
        return this.alive;
    }

    @Override
    public float getWidth() {
        return this.width;
    }

    @Override
    public float getHeight() {
        return this.height;
    }

    @Override
    public MeleeWeapon getWeapon() {
        return this.weapon;
    }

    @Override
    public boolean isFacingLeft() {
        return this.facingLeft;
    }

    @Override
    public Rectangle getBossWeaponBounds() {
        if (weapon != null) {
            return weapon.getBounds();
        }
        return null;
    }

    @Override
    public boolean isAttacking() {
        return isAttacking;
    }

    @Override
    public void setWidth(float width) {
        this.width = width;
    }

    @Override
    public void setHeight(float height) {
        this.height = height;
    }

    @Override
    public void setHealth(int health) {
        this.health = health;
    }

    @Override
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    @Override
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public void setMoney(int money) {
        this.money = money;
    }

    @Override
    public void setMoneyDrop(int moneyDrop) {
        this.moneyDrop = moneyDrop;
    }
} 