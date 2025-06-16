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

    private float scale = 1.0f;

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
    }


    private void initializeLevel2PlayableAreas() {
        playableAreas.clear();

        final float PLAYER_WIDTH = (float) (12.5f);
        final float PLAYER_HEIGHT = (float) (17.0f);

        // Room 1
        playableAreas.add(new Rectangle(
            950.17f + PLAYER_WIDTH,
            45.91f + PLAYER_HEIGHT,
            96.69f - PLAYER_WIDTH,
            63.86f
        ));

        // Passage 1 (горизонтальний)
        playableAreas.add(new Rectangle(
            678.805f,
            65.74f ,
            344.9f,
            21.65f
        ));

        // Room 2
        playableAreas.add(new Rectangle(
            513.32f + PLAYER_WIDTH,
            24.01f ,
            235.62f - PLAYER_WIDTH,
            89.04f
        ));

        // Passage 2
        playableAreas.add(new Rectangle(
            237.92f,
            65.94f ,
            343.24f,
            20.04f
        ));

        // Room 3
        playableAreas.add(new Rectangle(
            216.16f + PLAYER_WIDTH,
            37.38f ,
            90.88f - PLAYER_WIDTH,
            71.71f
        ));

        // Passage 3 (вертикальний)
        playableAreas.add(new Rectangle(
            623.25f,
            78.015f + PLAYER_WIDTH ,
            26.78f,
            174.94f - PLAYER_HEIGHT
        ));

        // Room 4
        playableAreas.add(new Rectangle(
            513.89f + PLAYER_WIDTH,
            224.16f ,
            235.81f - PLAYER_WIDTH,
            162.53f
        ));

        // Passage 4
        playableAreas.add(new Rectangle(
            252.63f,
            293.11f ,
            324.64f,
            25.13f
        ));

        // Room 5
        playableAreas.add(new Rectangle(
            190.37f + PLAYER_WIDTH,
            238.34f,
            143.16f - PLAYER_WIDTH,
            120.62f
        ));

        // Passage 5 (вертикальний)
        playableAreas.add(new Rectangle(
            622.79f,
            299.17f + PLAYER_WIDTH ,
            27.63f,
            411.4f - PLAYER_HEIGHT
        ));

        // Room 6
        playableAreas.add(new Rectangle(
            580.76f + PLAYER_WIDTH,
            625.40f ,
            100.86f - PLAYER_WIDTH,
            57.40f
        ));
    }






    public void setLevel2Areas() {
        // Автоматично згенерувати allowed zones з map2.png
        initializeLevel2PlayableAreas();

        // Зменшуємо швидкість для 2 рівня в 3 рази
        this.speed = 200f / 3f;
    }

    public void setLevel3Areas() {
        playableAreas.clear();

        final float PLAYER_WIDTH = 12.5f;
        final float PLAYER_HEIGHT = 17.0f;

        // Room 1 (top left)
        playableAreas.add(new Rectangle(
            100f + PLAYER_WIDTH,
            500f + PLAYER_HEIGHT,
            150f - PLAYER_WIDTH,
            100f - PLAYER_HEIGHT
        ));

        // Room 2 (top center)
        playableAreas.add(new Rectangle(
            350f + PLAYER_WIDTH,
            500f + PLAYER_HEIGHT,
            150f - PLAYER_WIDTH,
            100f - PLAYER_HEIGHT
        ));

        // Room 3 (top right)
        playableAreas.add(new Rectangle(
            600f + PLAYER_WIDTH,
            500f + PLAYER_HEIGHT,
            150f - PLAYER_WIDTH,
            100f - PLAYER_HEIGHT
        ));

        // Room 4 (middle left)
        playableAreas.add(new Rectangle(
            100f + PLAYER_WIDTH,
            300f + PLAYER_HEIGHT,
            150f - PLAYER_WIDTH,
            100f - PLAYER_HEIGHT
        ));

        // Room 5 (middle center)
        playableAreas.add(new Rectangle(
            350f + PLAYER_WIDTH,
            300f + PLAYER_HEIGHT,
            150f - PLAYER_WIDTH,
            100f - PLAYER_HEIGHT
        ));

        // Room 6 (middle right)
        playableAreas.add(new Rectangle(
            600f + PLAYER_WIDTH,
            300f + PLAYER_HEIGHT,
            150f - PLAYER_WIDTH,
            100f - PLAYER_HEIGHT
        ));

        // Room 7 (bottom left)
        playableAreas.add(new Rectangle(
            100f + PLAYER_WIDTH,
            100f + PLAYER_HEIGHT,
            150f - PLAYER_WIDTH,
            100f - PLAYER_HEIGHT
        ));

        // Room 8 (bottom center)
        playableAreas.add(new Rectangle(
            350f + PLAYER_WIDTH,
            100f + PLAYER_HEIGHT,
            150f - PLAYER_WIDTH,
            100f - PLAYER_HEIGHT
        ));

        // Room 9 (bottom right)
        playableAreas.add(new Rectangle(
            600f + PLAYER_WIDTH,
            100f + PLAYER_HEIGHT,
            150f - PLAYER_WIDTH,
            100f - PLAYER_HEIGHT
        ));

        // Horizontal corridor 1 (connecting top rooms)
        playableAreas.add(new Rectangle(
            250f,
            530f,
            100f,
            40f
        ));

        // Horizontal corridor 2 (connecting middle rooms)
        playableAreas.add(new Rectangle(
            250f,
            330f,
            100f,
            40f
        ));

        // Horizontal corridor 3 (connecting bottom rooms)
        playableAreas.add(new Rectangle(
            250f,
            130f,
            100f,
            40f
        ));

        // Vertical corridor 1 (connecting left rooms)
        playableAreas.add(new Rectangle(
            150f,
            200f,
            50f,
            100f
        ));

        // Vertical corridor 2 (connecting right rooms)
        playableAreas.add(new Rectangle(
            650f,
            200f,
            50f,
            100f
        ));

        // Set speed for level 3 (same as level 2)
        this.speed = 200f / 3f;

        // Output player coordinates to console when moving
        System.out.println("Player position: " + this.position.x + ", " + this.position.y);
    }

    public void setLevel1Areas() {
        initializePlayableAreas();
        // Повертаємо нормальну швидкість для 1 рівня
        this.speed = 200f;
    }

    /**
     * Автоматично будує playableAreas для рівня 2 на основі прозорості map2.png
     */


    private boolean isPositionValid(float x, float y) {
        Rectangle playerBounds = new Rectangle(x, y, this.width, this.height);

        // Перевіряємо, чи координати гравця в межах карти
        if (x < 0 || y < 0 || x > 1280 || y > 720) {
            return false;
        }

        for (Rectangle area : playableAreas) {
            if (area.overlaps(playerBounds)) {
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

            // Output player coordinates to console when moving
            if (this.velocity.x != 0 || this.velocity.y != 0) {
                System.out.println("Player position: " + this.position.x + ", " + this.position.y);
            }

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

        // Розраховуємо позицію зброї боса
        float weaponWidth = boss.getWeapon().getWidth() * 0.5f;  // Зменшуємо розмір зброї
        float weaponHeight = boss.getWeapon().getHeight() * 0.5f;

        float offsetX;
        float offsetY;

        if (boss.isFacingLeft()) {
            offsetX = -27.0f;
            offsetY = -7.0f;
        } else {
            offsetX = 27.0f;
            offsetY = -7.0f;
        }

        float weaponX = boss.getPosition().x + boss.getWidth() / 2.0f + offsetX - weaponWidth / 2.0f;
        float weaponY = boss.getPosition().y + boss.getHeight() / 2.0f + offsetY - weaponHeight / 2.0f;

        // Створюємо менший хітбокс зброї
        return new Rectangle(
            weaponX - 5,  // Зменшуємо розширення хітбокса
            weaponY - 5,
            weaponWidth + 10,  // Зменшуємо розширення хітбокса
            weaponHeight + 10
        );
    }
    public void render(SpriteBatch batch) {
        // Встановлюємо повну непрозорість для всіх наступних об'єктів у цьому batch
        // Це забезпечить, що гравець і його зброя будуть повністю непрозорими.
        batch.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        // Малюємо текстуру гравця, тільки якщо гравець живий
        if (this.texture != null && this.alive) {
            if (this.facingLeft) {
                batch.draw(this.texture, this.position.x + this.width, this.position.y, -this.width, this.height);
            } else {
                batch.draw(this.texture, this.position.x, this.position.y, this.width, this.height);
            }
        } else{
            if(!this.alive) {
                this.texture = new Texture(Gdx.files.internal("images/other/RIP.png"));
                batch.draw(this.texture, this.position.x, this.position.y, this.width, this.height);
            }
        }

        // Малюємо зброю, тільки якщо гравець живий і має зброю
        if (this.alive && this.currentWeapon != null) {
            float offsetX;
            float offsetY;
            float totalRotation;

            if (this.facingLeft) {
                offsetX = -23.0F * scale;
                offsetY = -1.0F * scale;
                totalRotation = 5.0F + this.swordAttackAnimation;
            } else {
                offsetX = 23.0F * scale;
                offsetY = -1.0F * scale;
                totalRotation = this.swordRotation - this.swordAttackAnimation;
            }

            float swordX = this.position.x + this.width / 2.0F + offsetX - this.currentWeapon.getWidth() / 2.0F;
            float swordY = this.position.y + this.height / 2.0F + offsetY - this.currentWeapon.getHeight() / 2.0F;

            this.currentWeapon.render(batch, swordX, swordY, totalRotation);
        }
        // Дуже важливо: повертаємо колір batch на повну непрозорість (білий)
        // після малювання гравця та зброї, щоб не впливати на інші об'єкти.
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
        if (!this.alive) return; // Не отримуємо шкоди якщо вже мертві
        if (this.damageCooldown > 0) return; // Не отримуємо шкоди під час кулдауну

        this.health -= damage;
        this.damageCooldown = DAMAGE_COOLDOWN_TIME; // Встановлюємо кулдаун
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
        float barWidth = 70f * scale; // Ширина смужки
        float barHeight = 8f * scale; // Висота смужки
        float barOffsetY = height;

        // Позиція смужки
        float barX = position.x + (width - barWidth) / 2f;
        float barY = position.y + barOffsetY;

        // Відсоток здоров'я
        float healthPercent = (float) health / (float) maxHealth;

        // Малюємо фон смужки (темно-сірий)
        batch.setColor(0.2f, 0.2f, 0.2f, 0.8f);
        batch.draw(getPixelTexture(), barX - 2f * scale, barY - 2f * scale, barWidth + 4f * scale, barHeight + 4f * scale);

        // Змінюємо колір смужки в залежності від здоров'я (від зеленого до червоного)Add commentMore actions
        batch.setColor(1.0f - healthPercent, healthPercent, 0.0f, 1.0f);
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

    public boolean isFacingdLeft() {
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

    public void setScale(float scale) {
        this.scale = scale;
        this.width = 50.0F * scale;
        this.height = 68.0F * scale;
    }

    public void setHealthBarScale(float scale) {
        this.scale = scale;
    }

    public Weapon getWeapon() {
        return this.currentWeapon;
    }

    public float getDamageCooldown() {
        return this.damageCooldown;
    }

    public void setDamageCooldown(float cooldown) {
        this.damageCooldown = cooldown;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void restoreFullHealth() {
        this.health = this.maxHealth;
    }

    public Rectangle getWeaponBounds() {
        if (currentWeapon == null || currentWeapon.getTexture() == null) return null;

        float weaponWidth = currentWeapon.getWidth() * 0.5f;
        float weaponHeight = currentWeapon.getHeight() * 0.5f;

        float offsetX;
        float offsetY;

        if (facingLeft) {
            offsetX = -23.0f * scale;
            offsetY = -1.0f * scale;
        } else {
            offsetX = 23.0f * scale;
            offsetY = -1.0f * scale;
        }

        float weaponX = position.x + width / 2.0f + offsetX - weaponWidth / 2.0f;
        float weaponY = position.y + height / 2.0f + offsetY - weaponHeight / 2.0f;

        return new Rectangle(
            weaponX - 10, // Expand hitbox by 10 pixels on each side
            weaponY - 10,
            weaponWidth + 20,
            weaponHeight + 20
        );
    }
}
