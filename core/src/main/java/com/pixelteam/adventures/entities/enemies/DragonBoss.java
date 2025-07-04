package com.pixelteam.adventures.entities.enemies;

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

    protected float attackTimer;
    protected Player target;
    protected boolean isMovingTowardsPlayer;
    protected float phaseTimer;
    private int swordHitCount; // Counter for sword hits
    protected float damageCooldown; // Cooldown to prevent multiple hits in a single attack
    protected MeleeWeapon weapon; // Зброя боса
    protected float weaponSwingTimer; // Таймер для махання зброєю
    protected boolean facingLeft; // Напрямок погляду боса
    protected float weaponRotation; // Base rotation angle for weapon
    protected boolean isAttacking; // Whether boss is currently attacking
    protected float weaponAttackAnimation; // Animation progress for weapon attack
    protected boolean weaponSwingDirection; // Direction of weapon swing (true = forward, false = backward)
    protected static Texture pixelTexture;
    private boolean isFireBoss = false; // Для розрізнення fireBoss від звичайного DragonBoss

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
        }
    }

    // Implement the abstract method from Character
    @Override
    public void move(Vector2 direction) {
        if (!alive || !active) return;

        // Set velocity based on direction and speed
        velocity.set(direction).nor().scl(speed);
        System.out.println("FireBoss move() called: direction=" + direction + ", speed=" + speed + ", velocity=" + velocity);

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

        // --- Відновлюємо AI та рух ---
        updateAI(deltaTime);

        // Оновлення позиції
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);
        if (isFireBoss) {
            constrainToRoom10();
        } else {
            constrainToRightRoom();
        }
    }

    private void updateAI(float deltaTime) {
        if (target == null) return;
        if (isFireBoss) {
            // Debug для fireBoss
            System.out.println("FireBoss AI: isFireBoss=" + isFireBoss + ", playerInRoom=" + isPlayerInRoom10() + ", target=" + (target != null));
            System.out.println("FireBoss position: " + position + ", target position: " + target.getPosition());
            
            // Логіка для fireBoss (кімната 10)
            if (isPlayerInRoom10()) {
                float distanceToPlayer = position.dst(target.getPosition());
                System.out.println("FireBoss: distanceToPlayer=" + distanceToPlayer + ", ATTACK_RANGE=" + ATTACK_RANGE);
                if (distanceToPlayer > ATTACK_RANGE) {
                    System.out.println("FireBoss: Calling moveTowardsPlayer()");
                    moveTowardsPlayer();
                    isMovingTowardsPlayer = true;
                    System.out.println("FireBoss: Moving towards player, velocity=" + velocity);
                } else {
                    velocity.set(0, 0);
                    isMovingTowardsPlayer = false;
                    if (attackTimer <= 0) {
                        attack();
                        attackTimer = ATTACK_COOLDOWN;
                    }
                }
            } else {
                // Гравець не в кімнаті 10, повертаємося в центр кімнати
                System.out.println("FireBoss: Calling moveToRoom10Center()");
                moveToRoom10Center();
                isMovingTowardsPlayer = false;
                System.out.println("FireBoss: Moving to center, velocity=" + velocity);
            }
        } else {
            // Логіка для звичайного DragonBoss (права кімната)
            if (isPlayerInRightRoom()) {
                float distanceToPlayer = position.dst(target.getPosition());
                if (distanceToPlayer > ATTACK_RANGE) {
                    moveTowardsPlayer();
                    isMovingTowardsPlayer = true;
                } else {
                    velocity.set(0, 0);
                    isMovingTowardsPlayer = false;
                    if (attackTimer <= 0) {
                        attack();
                        attackTimer = ATTACK_COOLDOWN;
                    }
                }
            } else {
                velocity.set(0, 0);
                isMovingTowardsPlayer = false;
            }
        }
        if (phaseTimer > 3.0f) {
            useAbility();
            phaseTimer = 0f;
        }
    }

    private boolean isPlayerInRightRoom() {
        if (target == null) return false;
        // Room bounds for right room (level 1)
        float roomLeft = 835f;
        float roomRight = 835f + 310f;
        float roomBottom = 260f;
        float roomTop = 260f + 165f;
        Vector2 playerPos = target.getPosition();
        float playerWidth = target.getWidth();
        float playerHeight = target.getHeight();
        return playerPos.x + playerWidth > roomLeft &&
               playerPos.x < roomRight &&
               playerPos.y + playerHeight > roomBottom &&
               playerPos.y < roomTop;
    }

    private boolean isPlayerInRoom10() {
        if (target == null) return false;
        // Room 10 bounds
        float roomLeft = 495f;
        float roomRight = 780f;
        float roomBottom = 575f;
        float roomTop = 732f;
        Vector2 playerPos = target.getPosition();
        float playerWidth = target.getWidth();
        float playerHeight = target.getHeight();
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

    private void constrainToRightRoom() {
        float roomLeft = 835f;
        float roomRight = 835f + 310f;
        float roomBottom = 260f;
        float roomTop = 260f + 165f;
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

    private void constrainToRoom10() {
        float roomLeft = 495f;
        float roomRight = 780f;
        float roomBottom = 575f;
        float roomTop = 732f;
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

        // Розмір зброї (масштабуємо для боса через scale)
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

    public Rectangle getBossWeaponBounds() {
        if (weapon != null) {
            return weapon.getBounds();
        }
        return null;
    }

    public boolean isAttacking() {
        return isAttacking;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setHealth(int health) {
        this.health = health;
    }
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }
    public void setSpeed(float speed) {
        this.speed = speed;
    }
    public void setMoney(int money) {
        this.money = money;
    }
    public void setMoneyDrop(int moneyDrop) {
        this.moneyDrop = moneyDrop;
    }

    public void setFireBoss(boolean isFireBoss) {
        this.isFireBoss = isFireBoss;
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
}
