package com.pixelteam.adventures.entities.player;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pixelteam.adventures.entities.Character;
import com.pixelteam.adventures.entities.enemies.Boss;
import com.pixelteam.adventures.items.Armor;
import com.pixelteam.adventures.utils.Stats;
import com.pixelteam.adventures.weapons.Weapon;


public class Player extends Character {
    private Inventory inventory;
    private Armor armor;
    private int experience;
    private PlayerController controller;
    private float attackCooldown;
    private boolean isAttacking;
    private float swordRotation;
    private float swordAttackAnimation;
    private boolean facingLeft;
    private static Texture pixelTexture;
    private float damageCooldown; // Кулдаун для запобігання багаторазовому отриманню шкоди
    private static final float DAMAGE_COOLDOWN_TIME = 1.0f; // 1 секунда захисту після отримання шкоди

    // Межі ігрового поля (кімнати та коридори)
    private List<Rectangle> playableAreas;

    // Список босів для перевірки колізій
    private List<Boss> bosses;

    // Scale factors for various attributes
    private float weaponScaleFactor = 1.0f;
    private float speedScaleFactor = 1.0f;
    private float damageScaleFactor = 1.0f;
    private float healthBarScaleFactor = 1.0f;

    public Player(float x, float y) {
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0.0F, 0.0F);
        this.width = 50.0F;
        this.height = 68.0F;
        this.health = 300;
        this.maxHealth = 300;
        this.speed = 200.0F;
        this.alive = true;
        this.active = true;
        this.money = 0;
        this.experience = 0;
        this.stats = new Stats();
        this.inventory = new Inventory(20);
        this.controller = new PlayerController(this);
        this.attackCooldown = 0.0F;
        this.isAttacking = false;
        this.swordRotation = -5.0F;
        this.swordAttackAnimation = 0.0F;
        this.facingLeft = false;
        this.bosses = new ArrayList<>();
        this.damageCooldown = 0.0f; // Ініціалізуємо кулдаун

        // Ініціалізація ігрових зон
        initializePlayableAreas();
    }

    private void initializePlayableAreas() {
        playableAreas = new ArrayList<>();

        // Розміри екрану: 1280x720
        if (healthBarScaleFactor == 1.0f) { // Level 1
            // Ліва кімната (коричнева підлога)
            playableAreas.add(new Rectangle(
                110,   // x - відступ від лівого краю
                260,  // y - відступ від низу
                120,  // width - ширина коричневої зони
                165   // height - висота коричневої зони
            ));

            // Лівий коридор (горизонтальний)
            playableAreas.add(new Rectangle(
                155,  // x - з'єднується з лівою кімнатою
                341,  // y - центр по вертикалі
                315,  // width - до центральної кімнати
                24    // height - висота коридору
            ));

            // Центральна кімната (коричнева підлога)
            playableAreas.add(new Rectangle(
                470,  // x - центральна позиція
                260,  // y - відступ від низу
                120,  // width - ширина центральної кімнати
                165   // height - висота коричневої зони
            ));

            // Правий коридор (горизонтальний)
            playableAreas.add(new Rectangle(
                590,  // x - з'єднується з центральною кімнатою
                341,  // y - центр по вертикалі
                245,  // width - до правої кімнати
                24    // height - висота коридору
            ));

            // Права кімната (коричнева підлога)
            playableAreas.add(new Rectangle(
                835,  // x - права позиція
                260,  // y - відступ від низу
                310,  // width - ширина коричневої зони
                165   // height - висота коричневої зони
            ));
        } else { // Level 2
            // Нижній ряд (ряд спавну) - 5 прямокутників

            // 1. Крайня права нижня кімната (місце спавну гравця 995, 65)
            playableAreas.add(new Rectangle(
                950,    // x
                30,     // y
                150,    // width
                120     // height
            ));

            // 2. Права нижня кімната
            playableAreas.add(new Rectangle(
                800,    // x
                30,     // y
                150,    // width
                120     // height
            ));

            // 3. Центральна нижня кімната
            playableAreas.add(new Rectangle(
                650,    // x
                30,     // y
                150,    // width
                120     // height
            ));

            // 4. Ліва нижня кімната
            playableAreas.add(new Rectangle(
                500,    // x
                30,     // y
                150,    // width
                120     // height
            ));

            // 5. Крайня ліва нижня кімната
            playableAreas.add(new Rectangle(
                350,    // x
                30,     // y
                150,    // width
                120     // height
            ));

            // 6. Вертикальний коридор, що з'єднує нижній ряд з середнім
            playableAreas.add(new Rectangle(
                700,    // x
                150,    // y
                100,    // width
                100     // height
            ));

            // Середній ряд - 3 прямокутники

            // 7. Ліва середня кімната
            playableAreas.add(new Rectangle(
                500,    // x
                250,    // y
                150,    // width
                120     // height
            ));

            // 8. Центральна середня кімната
            playableAreas.add(new Rectangle(
                650,    // x
                250,    // y
                150,    // width
                120     // height
            ));

            // 9. Права середня кімната
            playableAreas.add(new Rectangle(
                800,    // x
                250,    // y
                150,    // width
                120     // height
            ));

            // 10. Вертикальний коридор, що з'єднує середній ряд з верхнім
            playableAreas.add(new Rectangle(
                700,    // x
                370,    // y
                100,    // width
                100     // height
            ));

            // 11. Верхня кімната
            playableAreas.add(new Rectangle(
                650,    // x
                470,    // y
                150,    // width
                120     // height
            ));
        }
    }

    private boolean isPositionValid(float x, float y) {
        Rectangle playerBounds = new Rectangle(x, y, this.width, this.height);

        // Перевіряємо, чи гравець хоча б частково знаходиться в одній з дозволених зон
        for (Rectangle area : playableAreas) {
            if (area.overlaps(playerBounds)) {
                // Додатково перевіряємо колізію з босами
                return !isCollidingWithBosses(playerBounds);
            }
        }
        return false;
    }

    private boolean isCollidingWithBosses(Rectangle playerBounds) {
        for (Boss boss : bosses) {
            if (boss.isAlive() && boss.getBounds().overlaps(playerBounds)) {
                return true;
            }
        }
        return false;
    }

    public void update(float deltaTime) {
        // Оновлюємо кулдаун отримання шкоди
        if (this.damageCooldown > 0.0f) {
            this.damageCooldown -= deltaTime;
        }

        // Перевіряємо колізію зі зброєю боса (тільки якщо гравець живий)
        if (this.alive) {
            checkBossWeaponCollision();
        }

        // Решта існуючого коду update...
        this.controller.update();

        // Зберігаємо попередню позицію
        float previousX = this.position.x;
        float previousY = this.position.y;

        // Оновлюємо позицію тільки якщо гравець живий
        if (this.alive) {
            this.position.add(this.velocity.x * deltaTime, this.velocity.y * deltaTime);

            // Перевіряємо, чи нова позиція валідна
            if (!isPositionValid(this.position.x, this.position.y)) {
                // Якщо нова позиція невалідна, повертаємося до попередньої
                this.position.x = previousX;
                this.position.y = previousY;

                // Спробуємо рухатися тільки по X
                this.position.x += this.velocity.x * deltaTime;
                if (!isPositionValid(this.position.x, this.position.y)) {
                    this.position.x = previousX;

                    // Спробуємо рухатися тільки по Y
                    this.position.y += this.velocity.y * deltaTime;
                    if (!isPositionValid(this.position.x, this.position.y)) {
                        this.position.y = previousY;
                    }
                }
            }
        }

        if (this.attackCooldown > 0.0F) {
            this.attackCooldown -= deltaTime;
        }

        if (this.isAttacking) {
            this.swordAttackAnimation += 1600.0F * deltaTime;

            // Check for boss attacks during the entire attack animation
            if (this.currentWeapon != null) {
                checkBossAttack();
            }

            if (this.swordAttackAnimation > 150.0F) {
                this.swordAttackAnimation = 150.0F;
                this.isAttacking = false;
            }
        } else if (this.swordAttackAnimation > 0.0F) {
            this.swordAttackAnimation -= 1600.0F * deltaTime;
            if (this.swordAttackAnimation < 0.0F) {
                this.swordAttackAnimation = 0.0F;
            }
        }
    }
    private void checkBossWeaponCollision() {
        // Перевіряємо колізію зі зброєю кожного боса
        for (Boss boss : bosses) {
            if (boss.isAlive() && boss.getWeapon() != null) {
                // Отримуємо позицію зброї боса
                Rectangle weaponBounds = getBossWeaponBounds(boss);

                // Перевіряємо колізію з гравцем
                if (weaponBounds != null && this.getBounds().overlaps(weaponBounds)) {
                    // Завдаємо шкоду тільки якщо кулдаун закінчився
                    if (this.damageCooldown <= 0.0f) {
                        this.takeDamage(boss.getWeapon().getDamage());
                        this.damageCooldown = DAMAGE_COOLDOWN_TIME;
                    }
                }
            }
        }
    }

    private Rectangle getBossWeaponBounds(Boss boss) {
        if (boss.getWeapon() == null) return null;

        // Розраховуємо позицію зброї боса (аналогічно до DragonBoss.renderWeapon)
        float weaponWidth = boss.getWeapon().getWidth();
        float weaponHeight = boss.getWeapon().getHeight();

        float offsetX;
        float offsetY;

        if (boss.isFacingLeft()) {
            offsetX = -54.0f;
            offsetY = -15.0f;
        } else {
            offsetX = 62.0f;
            offsetY = -15.0f;
        }

        float weaponX = boss.getPosition().x + boss.getWidth() / 2.0f + offsetX - weaponWidth / 2.0f;
        float weaponY = boss.getPosition().y + boss.getHeight() / 2.0f + offsetY - weaponHeight / 2.0f;

        // Створюємо хітбокс зброї (трохи більший для кращого попадання)
        return new Rectangle(
            weaponX - 10,
            weaponY - 10,
            weaponWidth + 20,
            weaponHeight + 20
        );
    }

    /**
     * Sets the scale factor for the player's weapon
     * @param factor the scale factor to apply
     */
    public void setWeaponScaleFactor(float factor) {
        this.weaponScaleFactor = factor;
    }

    /**
     * Sets the scale factor for the player's movement speed
     * @param factor the scale factor to apply
     */
    public void setSpeedScaleFactor(float factor) {
        this.speedScaleFactor = factor;
        // Update the actual speed
        this.speed = 200.0F * speedScaleFactor;
    }

    /**
     * Sets the scale factor for the player's damage
     * @param factor the scale factor to apply
     */
    public void setDamageScaleFactor(float factor) {
        this.damageScaleFactor = factor;
        // Update weapon damage if available
        if (this.currentWeapon != null) {
            // Since we can't directly modify the weapon's damage (no setter),
            // we'll apply the scale factor when checking for boss attacks
        }
    }

    /**
     * Sets the scale factor for the player's health bar
     * @param factor the scale factor to apply
     */
    public void setHealthBarScaleFactor(float factor) {
        this.healthBarScaleFactor = factor;
    }

    /**
     * Scales all player attributes by the given factor
     * @param factor the scale factor to apply to all attributes
     */
    public void scaleAllAttributes(float factor) {
        // Scale visual dimensions
        float originalWidth = this.width;
        float originalHeight = this.height;
        this.width = originalWidth * factor;
        this.height = originalHeight * factor;

        // Scale weapon
        if (this.currentWeapon != null) {
            this.currentWeapon.setWidth(this.currentWeapon.getWidth() * factor);
            this.currentWeapon.setHeight(this.currentWeapon.getHeight() * factor);
        }
        setWeaponScaleFactor(factor);

        // Scale speed
        setSpeedScaleFactor(factor);

        // Scale damage
        setDamageScaleFactor(factor);

        // Scale health bar
        setHealthBarScaleFactor(factor);

        // Reinitialize playable areas for level 2
        initializePlayableAreas();
    }

    public void render(SpriteBatch batch) {
        // Встановлюємо повну непрозорість для всіх наступних об'єктів у цьому batch
        batch.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        // Малюємо текстуру гравця, тільки якщо гравець живий
        if (this.texture != null && this.alive) {
            if (this.facingLeft) {
                batch.draw(this.texture, this.position.x + this.width, this.position.y, -this.width, this.height);
            } else {
                batch.draw(this.texture, this.position.x, this.position.y, this.width, this.height);
            }
        } else {
            if(!this.alive) {
                this.texture = new Texture(Gdx.files.internal("images/other/RIP.png"));
                batch.draw(this.texture, this.position.x, this.position.y, this.width, this.height);
            }
        }

        // Малюємо зброю, тільки якщо гравець живий і має зброю
        if (this.alive && this.currentWeapon != null && this.currentWeapon.getTexture() != null) {
            float offsetX;
            float offsetY;
            float totalRotation;

            // Визначаємо зміщення та обертання зброї залежно від напрямку гравця
            if (this.facingLeft) {
                if (weaponScaleFactor != 1.0f) {
                    // Налаштування для 2 рівня
                    offsetX = -10.0F * weaponScaleFactor; // Ще ближче до гравця
                    offsetY = -1.0F * weaponScaleFactor;
                } else {
                    // Налаштування для 1 рівня
                    offsetX = -23.0F;
                    offsetY = -1.0F;
                }
                totalRotation = 5.0F + this.swordAttackAnimation;
            } else {
                if (weaponScaleFactor != 1.0f) {
                    // Налаштування для 2 рівня
                    offsetX = 10.0F * weaponScaleFactor; // Ще ближче до гравця
                    offsetY = -1.0F * weaponScaleFactor;
                } else {
                    // Налаштування для 1 рівня
                    offsetX = 23.0F;
                    offsetY = -1.0F;
                }
                totalRotation = this.swordRotation - this.swordAttackAnimation;
            }

            float swordX = this.position.x + this.width / 2.0F + offsetX - (this.currentWeapon.getWidth() * weaponScaleFactor) / 2.0F;
            float swordY = this.position.y + this.height / 2.0F + offsetY - (this.currentWeapon.getHeight() * weaponScaleFactor) / 2.0F;

            // Малюємо текстуру зброї з урахуванням масштабу
            batch.draw(this.currentWeapon.getTexture(),
                swordX, swordY,
                this.currentWeapon.getWidth() * weaponScaleFactor / 2.0F,
                this.currentWeapon.getHeight() * weaponScaleFactor / 2.0F,
                this.currentWeapon.getWidth() * weaponScaleFactor,
                this.currentWeapon.getHeight() * weaponScaleFactor,
                1.0F, 1.0F,
                totalRotation,
                0, 0,
                this.currentWeapon.getTexture().getWidth(),
                this.currentWeapon.getTexture().getHeight(),
                false, false);
        }
        // Повертаємо білий колір для нормального рендерингу інших об'єктів
        batch.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public Rectangle getBounds() {
        return new Rectangle(this.position.x, this.position.y, this.width, this.height);
    }

    public Rectangle getLowerBounds() {
        float lowerHeight = this.height * 0.3f;
        return new Rectangle(this.position.x, this.position.y, this.width, lowerHeight);
    }

    public void attack() {
        if (this.attackCooldown <= 0.0F && this.currentWeapon != null) {
            this.isAttacking = true;
            this.swordAttackAnimation = 0.0F;
            this.attackCooldown = 0.5F;
            Vector2 target = new Vector2((float)Gdx.input.getX(), (float)(Gdx.graphics.getHeight() - Gdx.input.getY()));
            this.currentWeapon.attack(this, target);

            // Перевіряємо чи атакуємо боса
            checkBossAttack();
        }
    }

    private void checkBossAttack() {
        // Calculate sword position and hitbox based on player position, direction, and attack animation
        float offsetX;
        float offsetY;
        float totalRotation;

        if (this.facingLeft) {
            offsetX = -23.0F * weaponScaleFactor;
            offsetY = -1.0F * weaponScaleFactor;
            totalRotation = 5.0F + this.swordAttackAnimation;
        } else {
            offsetX = 23.0F * weaponScaleFactor;
            offsetY = -1.0F * weaponScaleFactor;
            totalRotation = this.swordRotation - this.swordAttackAnimation;
        }

        float swordX = this.position.x + this.width / 2.0F + offsetX - (this.currentWeapon.getWidth() * weaponScaleFactor) / 2.0F;
        float swordY = this.position.y + this.height / 2.0F + offsetY - (this.currentWeapon.getHeight() * weaponScaleFactor) / 2.0F;

        // Create a larger hitbox for the sword to make collision detection more forgiving
        Rectangle swordHitbox = new Rectangle(
            swordX - 10 * weaponScaleFactor, // Expand hitbox by 10 pixels on each side, scaled
            swordY - 10 * weaponScaleFactor,
            (this.currentWeapon.getWidth() + 20) * weaponScaleFactor,
            (this.currentWeapon.getHeight() + 20) * weaponScaleFactor
        );

        // Check collision with bosses
        for (Boss boss : bosses) {
            if (boss.isAlive() && boss.getBounds().overlaps(swordHitbox)) {
                // Apply damage scale factor to weapon damage
                int scaledDamage = (int)(this.currentWeapon.getDamage() * damageScaleFactor);
                boss.takeDamage(scaledDamage);
            }
        }
    }

    public void move(Vector2 direction) {
        this.velocity.set(direction).scl(this.speed);
        if (direction.x < 0.0F) {
            this.facingLeft = true;
        } else if (direction.x > 0.0F) {
            this.facingLeft = false;
        }
    }

    public void takeDamage(int damage) {
        if (!this.alive) return; // Не отримуємо шкоди якщо вже мертві

        this.health -= damage;
        System.out.println("Player took " + damage + " damage. Health: " + this.health + "/" + this.maxHealth);

        if (this.health <= 0) {
            this.health = 0; // Не даємо здоров'ю стати негативним
            this.die();
        }
    }

    public void heal(int amount) {
        if (!this.alive) return;

        this.health += amount;
        if (this.health > this.maxHealth) {
            this.health = this.maxHealth;
        }
    }

    public void die() {
        this.alive = false;
        this.velocity.set(0, 0); // Зупиняємо рух
        System.out.println("Player has died!");
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

    public void renderHealthBar(SpriteBatch batch) {
        // Параметри смужки здоров'я
        float barWidth = 70f;
        float barHeight = 8f;
        float barOffsetY = height + 5f;

        // Застосовуємо масштабування тільки для другого рівня
        if (healthBarScaleFactor != 1.0f) {
            barWidth = 20f; // Ще менший розмір для другого рівня
            barHeight = 2.5f; // Ще менший розмір для другого рівня
            barOffsetY = height + 1.5f;
        }

        // Позиція смужки
        float barX = position.x + (width - barWidth) / 2f;
        float barY = position.y + barOffsetY;

        // Відсоток здоров'я
        float healthPercent = (float) health / (float) maxHealth;

        // Малюємо фон смужки (темно-сірий)
        batch.setColor(0.2f, 0.2f, 0.2f, 0.8f);
        float borderSize = healthBarScaleFactor != 1.0f ? 0.5f : 2f; // Ще менша рамка для другого рівня
        batch.draw(getPixelTexture(), barX - borderSize, barY - borderSize, barWidth + borderSize * 2, barHeight + borderSize * 2);

        // Малюємо смужку здоров'я (червоний колір)
        batch.setColor(0.8f, 0.1f, 0.1f, 1.0f);
        batch.draw(getPixelTexture(), barX, barY, barWidth * healthPercent, barHeight);

        // Повертаємо білий колір для нормального рендерингу інших об'єктів
        batch.setColor(1f, 1f, 1f, 1f);
    }

    public void dispose() {
        if (pixelTexture != null) {
            pixelTexture.dispose();
            pixelTexture = null;
        }
    }

    public void levelUp() {
    }

    public void equipWeapon(Weapon weapon) {
        this.currentWeapon = weapon;
    }

    public void equipArmor(Armor armor) {
        this.armor = armor;
    }

    public PlayerController getController() {
        return this.controller;
    }

    public boolean isAttacking() {
        return this.isAttacking;
    }

    public void setAttacking(boolean attacking) {
        this.isAttacking = attacking;
    }

    public boolean isFacingLeft() {
        return this.facingLeft;
    }

    public void setFacingLeft(boolean facingLeft) {
        this.facingLeft = facingLeft;
    }

    // Метод для налаштування меж карти ззовні (якщо потрібно)
    public void setPlayableAreas(List<Rectangle> areas) {
        this.playableAreas = areas;
    }

    // Метод для отримання поточних меж (для налагодження)
    public List<Rectangle> getPlayableAreas() {
        return this.playableAreas;
    }

    // Методи для роботи з босами
    public void addBoss(Boss boss) {
        this.bosses.add(boss);
    }

    public void removeBoss(Boss boss) {
        this.bosses.remove(boss);
    }

    public void setBosses(List<Boss> bosses) {
        this.bosses = bosses;
    }

    // Метод для перевірки та корекції позиції (використовується після відштовхування)
    public void checkBounds() {
        if (!isPositionValid(this.position.x, this.position.y)) {
            // Повертаємося до найближчої валідної позиції
            findNearestValidPosition();
        }
    }

    private void findNearestValidPosition() {
        float step = 2.0f;

        // Пробуємо знайти найближчу валідну позицію
        for (int radius = 1; radius <= 20; radius++) {
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    float testX = this.position.x + x * step;
                    float testY = this.position.y + y * step;

                    if (isPositionValid(testX, testY)) {
                        this.position.x = testX;
                        this.position.y = testY;
                        return;
                    }
                }
            }
        }
    }

    public Vector2 getPosition() {
        return this.position;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public boolean isAlive() {
        return alive;
    }

    // Setter methods for width and height
    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
