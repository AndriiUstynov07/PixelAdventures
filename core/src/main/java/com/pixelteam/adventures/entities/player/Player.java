package com.pixelteam.adventures.entities.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pixelteam.adventures.entities.Character;
import com.pixelteam.adventures.entities.enemies.Boss;
import com.pixelteam.adventures.items.Armor;
import com.pixelteam.adventures.utils.Stats;
import com.pixelteam.adventures.weapons.Weapon;
import java.util.ArrayList;
import java.util.List;

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

    // Межі ігрового поля (кімнати та коридори)
    private List<Rectangle> playableAreas;

    // Список босів для перевірки колізій
    private List<Boss> bosses;

    public Player(float x, float y) {
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0.0F, 0.0F);
        this.width = 64.0F;
        this.height = 64.0F;
        this.health = 100;
        this.maxHealth = 100;
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

        // Ініціалізація ігрових зон
        initializePlayableAreas();
    }

    private void initializePlayableAreas() {
        playableAreas = new ArrayList<>();

        // Розміри екрану: 1280x720
        // Створюємо одну велику зону, що включає всі кімнати та коридори

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
            180,  // width - до правої кімнати
            24    // height - висота коридору
        ));

        // Права кімната (коричнева підлога)
        playableAreas.add(new Rectangle(
            835,  // x - права позиція
            260,  // y - відступ від низу
            310,  // width - ширина коричневої зони
            165   // height - висота коричневої зони
        ));
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
        this.controller.update();

        // Зберігаємо попередню позицію
        float previousX = this.position.x;
        float previousY = this.position.y;

        // Оновлюємо позицію
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

    public void render(SpriteBatch batch) {
        if (this.texture != null) {
            batch.draw(this.texture, this.position.x, this.position.y, this.width, this.height);
        }

        if (this.currentWeapon != null && this.currentWeapon.getTexture() != null) {
            float offsetX;
            float offsetY;
            float totalRotation;
            if (this.facingLeft) {
                offsetX = -23.0F;
                offsetY = -1.0F;
                totalRotation = 5.0F + this.swordAttackAnimation;
            } else {
                offsetX = 23.0F;
                offsetY = -1.0F;
                totalRotation = this.swordRotation - this.swordAttackAnimation;
            }

            float swordX = this.position.x + this.width / 2.0F + offsetX - this.currentWeapon.getWidth() / 2.0F;
            float swordY = this.position.y + this.height / 2.0F + offsetY - this.currentWeapon.getHeight() / 2.0F;
            batch.draw(this.currentWeapon.getTexture(), swordX, swordY, this.currentWeapon.getWidth() / 2.0F, this.currentWeapon.getHeight() / 2.0F, this.currentWeapon.getWidth(), this.currentWeapon.getHeight(), 1.0F, 1.0F, totalRotation, 0, 0, this.currentWeapon.getTexture().getWidth(), this.currentWeapon.getTexture().getHeight(), false, false);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(this.position.x, this.position.y, this.width, this.height);
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
            offsetX = -23.0F;
            offsetY = -1.0F;
            totalRotation = 5.0F + this.swordAttackAnimation;
        } else {
            offsetX = 23.0F;
            offsetY = -1.0F;
            totalRotation = this.swordRotation - this.swordAttackAnimation;
        }

        float swordX = this.position.x + this.width / 2.0F + offsetX - this.currentWeapon.getWidth() / 2.0F;
        float swordY = this.position.y + this.height / 2.0F + offsetY - this.currentWeapon.getHeight() / 2.0F;

        // Create a larger hitbox for the sword to make collision detection more forgiving
        Rectangle swordHitbox = new Rectangle(
            swordX - 10, // Expand hitbox by 10 pixels on each side
            swordY - 10,
            this.currentWeapon.getWidth() + 20,
            this.currentWeapon.getHeight() + 20
        );

        // Check collision with bosses
        for (Boss boss : bosses) {
            if (boss.isAlive() && boss.getBounds().overlaps(swordHitbox)) {
                boss.takeDamage(this.currentWeapon.getDamage());
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
        this.health -= damage;
        if (this.health <= 0) {
            this.die();
        }
    }

    public void die() {
        this.alive = false;
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
}
