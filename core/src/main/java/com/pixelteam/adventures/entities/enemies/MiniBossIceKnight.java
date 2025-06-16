package com.pixelteam.adventures.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pixelteam.adventures.entities.player.Player;
import com.pixelteam.adventures.utils.Stats;
import com.pixelteam.adventures.weapons.MeleeWeapon;

public class MiniBossIceKnight extends Boss {
    private static final float MINI_BOSS_SIZE = 35f;
    private static final int MINI_BOSS_HEALTH = 250;
    private static final float MINI_BOSS_SPEED = 35f;
    private static final float ATTACK_RANGE = 50f;
    private static final float ATTACK_COOLDOWN = 1.5f;
    private static final float WEAPON_SWING_COOLDOWN = 0.8f;
    private static final float COLLISION_STUN_DURATION = 0.1f;

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

    public MiniBossIceKnight(float x, float y) {
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
        this.width = MINI_BOSS_SIZE;
        this.height = MINI_BOSS_SIZE;
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
    }

    @Override
    public void update(float deltaTime) {
        if (target != null) {
            update(deltaTime, target);
        } else {
            if (!alive || !active) return;

            if (attackTimer > 0) {
                attackTimer -= deltaTime;
            }

            if (damageCooldown > 0) {
                damageCooldown -= deltaTime;
            }

            if (weaponSwingTimer > 0) {
                weaponSwingTimer -= deltaTime;
            }

            if (collisionStunTimer > 0) {
                collisionStunTimer -= deltaTime;
                if (collisionStunTimer <= 0) {
                    isStunnedFromCollision = false;
                }
            }

            updateWeaponAnimation(deltaTime);

            if (weapon != null) {
                weapon.update(deltaTime);
            }

            if (!isStunnedFromCollision) {
                position.add(velocity.x * deltaTime, velocity.y * deltaTime);
            }

            constrainToRoom();
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

    public void update(float deltaTime, Player player) {
        if (!alive || !active) return;

        this.target = player;
        this.phaseTimer += deltaTime;

        if (health <= maxHealth * 0.6f && phase == 1) {
            changePhase();
        }

        if (attackTimer > 0) {
            attackTimer -= deltaTime;
        }

        if (damageCooldown > 0) {
            damageCooldown -= deltaTime;
        }

        if (collisionStunTimer > 0) {
            collisionStunTimer -= deltaTime;
            if (collisionStunTimer <= 0) {
                isStunnedFromCollision = false;
            }
        }

        if (weaponSwingTimer > 0) {
            weaponSwingTimer -= deltaTime;
        } else {
            if (weapon != null) {
                weapon.startAttack();
                isAttacking = true;
                weaponSwingTimer = WEAPON_SWING_COOLDOWN;
            }
        }

        updateWeaponAnimation(deltaTime);

        if (weapon != null) {
            weapon.update(deltaTime);
        }

        if (!isStunnedFromCollision) {
            updateAI(deltaTime);
        } else {
            velocity.set(0, 0);
        }

        if (!isStunnedFromCollision) {
            position.add(velocity.x * deltaTime, velocity.y * deltaTime);
        }

        constrainToRoom();
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

    private void updateAI(float deltaTime) {
        if (target == null) return;

        float distanceToPlayer = position.dst(target.getPosition());

        if (isPlayerInRoom()) {
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
            moveToRoomCenter();
            isMovingTowardsPlayer = false;
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

        float weaponWidth = weapon.getWidth() * 0.17f;
        float weaponHeight = weapon.getHeight() * 0.35f;

        float offsetX;
        float offsetY;
        float totalRotation;

        if (facingLeft) {
            offsetX = -13.5f;
            offsetY = -3.5f;
            totalRotation = weaponRotation + weaponAttackAnimation;
        } else {
            offsetX = 13.5f;
            offsetY = -3.5f;
            totalRotation = weaponRotation - weaponAttackAnimation;
        }

        float weaponX = position.x + width / 2.0f + offsetX - weaponWidth / 2.0f;
        float weaponY = position.y + height / 2.0f + offsetY - weaponHeight / 2.0f;

        batch.draw(weapon.getTexture(),
            weaponX, weaponY,
            weaponWidth / 2.0f, weaponHeight / 2.0f,
            weaponWidth, weaponHeight,
            0.5f, 1.0f,
            totalRotation,
            0, 0,
            weapon.getTexture().getWidth(), weapon.getTexture().getHeight(),
            false, false);
    }

    private void renderHealthBar(SpriteBatch batch) {
        float barWidth = 35f;
        float barHeight = 4f;
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

            if (weapon != null) {
                weapon.startAttack();
                isAttacking = true;
            }
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
        float collisionMargin = 5f;
        return new Rectangle(
            position.x + collisionMargin,
            position.y + collisionMargin,
            width - 2 * collisionMargin,
            height - 2 * collisionMargin
        );
    }

    public Rectangle getBossWeaponBounds() {
        if (weapon == null || weapon.getTexture() == null) return null;

        float weaponWidth = weapon.getWidth() * 0.35f;
        float weaponHeight = weapon.getHeight() * 0.35f;

        float offsetX;
        float offsetY;

        if (facingLeft) {
            offsetX = -13.5f;
            offsetY = -3.5f;
        } else {
            offsetX = 13.5f;
            offsetY = -3.5f;
        }

        float weaponX = position.x + width / 2.0f + offsetX - weaponWidth / 2.0f;
        float weaponY = position.y + height / 2.0f + offsetY - weaponHeight / 2.0f;

        return new Rectangle(
            weaponX,
            weaponY,
            weaponWidth,
            weaponHeight
        );
    }

    public boolean isAttacking() {
        return this.isAttacking;
    }

    public void onCollisionWithPlayer() {
        collisionStunTimer = COLLISION_STUN_DURATION;
        isStunnedFromCollision = true;
        velocity.set(0, 0);
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
