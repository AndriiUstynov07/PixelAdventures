package com.pixelteam.adventures.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pixelteam.adventures.entities.player.Player;
import com.pixelteam.adventures.utils.Stats;
import com.pixelteam.adventures.weapons.MeleeWeapon;

public class MiniBossIceKnight extends Boss {
    private static final float MINI_BOSS_SIZE = 80f; // Reduced from 105f to 80f
    private static final int MINI_BOSS_HEALTH = 300;
    private static final float MINI_BOSS_SPEED = 40f;
    private static final float ATTACK_RANGE = 50f;
    private static final float ATTACK_COOLDOWN = 1.5f;
    private static final float WEAPON_SWING_COOLDOWN = 0.8f;
    private static final float COLLISION_STUN_DURATION = 0.1f;
    private static final float MOVE_INTERVAL = 1.0f; // 1 second moving
    private static final float PAUSE_INTERVAL = 0.25f; // 0.25 seconds pause

    private float attackTimer;
    private Player target;
    private boolean isMovingTowardsPlayer;
    private float phaseTimer;
    private int swordHitCount;
    private float damageCooldown;
    private MeleeWeapon weapon;
    private float weaponSwingTimer;
    private boolean facingLeft;
    private float weaponRotation;
    private boolean isAttacking;
    private float weaponAttackAnimation;
    private boolean weaponSwingDirection;
    private static Texture pixelTexture;

    private float collisionStunTimer;
    private boolean isStunnedFromCollision;
    private float moveTimer;
    private boolean isMoving;

    public MiniBossIceKnight(float x, float y) {
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
        this.width = MINI_BOSS_SIZE;
        this.height = MINI_BOSS_SIZE;
        System.out.println("Ice Knight size: " + this.width + "x" + this.height);
        this.health = MINI_BOSS_HEALTH;
        this.maxHealth = MINI_BOSS_HEALTH;
        this.speed = MINI_BOSS_SPEED;
        this.alive = true;
        this.active = true;
        this.money = 500;
        this.moneyDrop = 500;
        this.stats = new Stats();

        this.phase = 1;
        this.attackTimer = 0f;
        this.isMovingTowardsPlayer = false;
        this.phaseTimer = 0f;
        this.swordHitCount = 0;
        this.damageCooldown = 0f;
        this.weaponSwingTimer = 0f;
        this.facingLeft = false;
        this.weaponRotation = 0f;
        this.isAttacking = false;
        this.weaponAttackAnimation = 0f;
        this.weaponSwingDirection = true;

        this.collisionStunTimer = 0f;
        this.isStunnedFromCollision = false;
        this.moveTimer = 0f;
        this.isMoving = true;
    }

    @Override
    public void update(float deltaTime) {
        if (target != null) {
            update(deltaTime, target);
        } else {
            if (!alive || !active) return;

            updateTimers(deltaTime);
            updateWeaponAnimation(deltaTime);
            updateWeapon(deltaTime);
            updatePosition(deltaTime);
            constrainToRoom();
        }
    }

    private void updateTimers(float deltaTime) {
        if (attackTimer > 0) attackTimer -= deltaTime;
        if (damageCooldown > 0) damageCooldown -= deltaTime;
        if (weaponSwingTimer > 0) weaponSwingTimer -= deltaTime;
        if (collisionStunTimer > 0) {
            collisionStunTimer -= deltaTime;
            if (collisionStunTimer <= 0) {
                isStunnedFromCollision = false;
            }
        }
    }

    private void updateWeaponAnimation(float deltaTime) {
        if (isAttacking) {
            if (weaponSwingDirection) {
                weaponAttackAnimation += 900.0f * deltaTime;
                if (weaponAttackAnimation >= 110.0f) {
                    weaponAttackAnimation = 110.0f;
                    weaponSwingDirection = false;
                }
            } else {
                weaponAttackAnimation -= 900.0f * deltaTime;
                if (weaponAttackAnimation <= 0.0f) {
                    weaponAttackAnimation = 0.0f;
                    isAttacking = false;
                    weaponSwingDirection = true;
                }
            }
        }
    }

    private void updateWeapon(float deltaTime) {
        if (weapon != null) {
            weapon.update(deltaTime);
        }
    }

    private void updatePosition(float deltaTime) {
        if (!isStunnedFromCollision) {
            position.add(velocity.x * deltaTime, velocity.y * deltaTime);
        }
    }


    public void update(float deltaTime, Player player) {
        if (!alive || !active) return;

        this.target = player;
        this.phaseTimer += deltaTime;

        updateTimers(deltaTime);
        updateWeaponAnimation(deltaTime);
        updateWeapon(deltaTime);

        if (!isStunnedFromCollision) {
            updateAI(deltaTime);
        } else {
            velocity.set(0, 0);
        }

        updatePosition(deltaTime);
        constrainToRoom();
    }

    private void updateAI(float deltaTime) {
        if (target == null) return;

        float distanceToPlayer = position.dst(target.getPosition());

        if (isPlayerInRoom()) {
            moveTimer += deltaTime;

            if (isMoving) {
                if (moveTimer >= MOVE_INTERVAL) {
                    isMoving = false;
                    moveTimer = 0f;
                    velocity.set(0, 0);
                } else if (distanceToPlayer > ATTACK_RANGE) {
                    moveTowardsPlayer();
                }
            } else {
                if (moveTimer >= PAUSE_INTERVAL) {
                    isMoving = true;
                    moveTimer = 0f;
                }
            }

            if (distanceToPlayer <= ATTACK_RANGE && attackTimer <= 0) {
                attack();
                attackTimer = ATTACK_COOLDOWN;
            }
        } else {
            moveToRoomCenter();
        }

        if (phaseTimer > 2.5f) {
            useAbility();
            phaseTimer = 0f;
        }
    }

    private boolean isPlayerInRoom() {
        if (target == null) return false;

        float roomLeft = 385f;
        float roomRight = 385f + 360f;
        float roomBottom = 210f;
        float roomTop = 210f + 215f;

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

    private void moveToRoomCenter() {
        float roomLeft = 385f;
        float roomRight = 385f + 360f;
        float roomBottom = 210f;
        float roomTop = 210f + 215f;

        float centerX = roomLeft + (roomRight - roomLeft) / 2f - width / 2f;
        float centerY = roomBottom + (roomTop - roomBottom) / 2f - height / 2f;

        Vector2 roomCenter = new Vector2(centerX, centerY);
        Vector2 direction = new Vector2(roomCenter).sub(position).nor();

        if (position.dst(roomCenter) > 5f) {
            move(direction);
        } else {
            velocity.set(0, 0);
        }
    }

    private void constrainToRoom() {
        float roomLeft = 385f;
        float roomRight = 385f + 360f;
        float roomBottom = 210f;
        float roomTop = 210f + 215f;

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
    public void move(Vector2 direction) {
        if (!alive || !active || isStunnedFromCollision) return;

        velocity.set(direction).nor().scl(speed);

        if (direction.x < 0) {
            facingLeft = true;
        } else if (direction.x > 0) {
            facingLeft = false;
        }
    }

    public void render(SpriteBatch batch) {
        if (texture != null && alive) {
            if (facingLeft) {
                batch.draw(texture, position.x + width, position.y, -width, height);
            } else {
                batch.draw(texture, position.x, position.y, width, height);
            }

            if (weapon != null) {
                renderWeapon(batch);
            }

            renderHealthBar(batch);
        }
    }

    private void renderWeapon(SpriteBatch batch) {
        if (weapon == null || weapon.getTexture() == null) return;

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
        float barWidth = 70f;
        float barHeight = 8f;
        float barOffsetY = height;

        float barX = position.x + (width - barWidth) / 2f;
        float barY = position.y + barOffsetY;

        float healthPercent = (float) health / (float) maxHealth;

        batch.setColor(0.2f, 0.2f, 0.2f, 0.8f);
        batch.draw(getPixelTexture(), barX - 2f, barY - 2f, barWidth + 4f, barHeight + 4f);

        batch.setColor(1.0f - healthPercent, healthPercent, 0.0f, 1.0f);
        batch.draw(getPixelTexture(), barX, barY, barWidth * healthPercent, barHeight);

        batch.setColor(1f, 1f, 1f, 1f);
    }

    public void attack() {
        if (target == null) return;

        float distanceToPlayer = position.dst(target.getPosition());
        if (distanceToPlayer <= ATTACK_RANGE) {
            target.takeDamage(15);
        }

        if (weapon != null) {
            weapon.startAttack();
            isAttacking = true;
        }
    }

    @Override
    public void useAbility() {
        if (phase == 1) {
            if (target != null) {
                attack();
            }
        } else if (phase == 2) {
            if (target != null) {
                attack();
            }
        }
    }

    @Override
    public void changePhase() {
        phase = 2;
        speed = MINI_BOSS_SPEED * 1.3f;
    }

    @Override
    public void dropLoot() {
        // Implement loot dropping logic
    }

    @Override
    public void takeDamage(int damage) {
        if (damageCooldown <= 0) {
            health -= damage;
            swordHitCount++;
            damageCooldown = 0.3f;

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
        float collisionMargin = 20f;
        return new Rectangle(
            position.x + collisionMargin,
            position.y + collisionMargin,
            width - 2 * collisionMargin,
            height - 2 * collisionMargin
        );
    }

    public Rectangle getBossWeaponBounds() {
        if (weapon == null || weapon.getTexture() == null) return null;

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

        float hitboxMargin = 5f;
        float attackMultiplier = isAttacking ? 1.1f : 1.0f;

        float cos = (float) Math.cos(Math.toRadians(totalRotation));
        float sin = (float) Math.sin(Math.toRadians(totalRotation));

        float centerX = weaponX + weaponWidth / 2;
        float centerY = weaponY + weaponHeight / 2;

        float rotatedWidth = Math.abs(weaponWidth * cos) + Math.abs(weaponHeight * sin);
        float rotatedHeight = Math.abs(weaponWidth * sin) + Math.abs(weaponHeight * cos);

        return new Rectangle(
            centerX - rotatedWidth / 2 - hitboxMargin * attackMultiplier,
            centerY - rotatedHeight / 2 - hitboxMargin * attackMultiplier,
            rotatedWidth + 2 * hitboxMargin * attackMultiplier,
            rotatedHeight + 2 * hitboxMargin * attackMultiplier
        );
    }

    public boolean isAttacking() {
        return this.isAttacking;
    }

    public void onCollisionWithPlayer() {
        collisionStunTimer = COLLISION_STUN_DURATION;
        isStunnedFromCollision = true;
        velocity.set(0, 0);
        
        if (target != null) {
            Vector2 pushDirection = new Vector2(target.getPosition()).sub(position).nor();
            target.getPosition().add(pushDirection.scl(20f));
        }
    }

    public void equipWeapon(MeleeWeapon weapon) {
        this.weapon = weapon;
    }

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

    public boolean isStunnedFromCollision() {
        return this.isStunnedFromCollision;
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
        if (pixelTexture != null) {
            pixelTexture.dispose();
            pixelTexture = null;
        }
    }
}
