package com.pixelteam.adventures.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pixelteam.adventures.entities.Character;

public class MeleeWeapon extends Weapon {
    private float range;
    private float swingAngle;
    private boolean attacking;
    private float attackRotation;
    private float attackTimer;
    private Texture texture;
    private float scale = 1.0f;
    private Vector2 position = new Vector2();
    private static final float WEAPON_WIDTH = 16f;  // Зменшуємо ширину зброї
    private static final float WEAPON_HEIGHT = 16f; // Зменшуємо висоту зброї

    public MeleeWeapon(String name, int damage, float attackSpeed, String texturePath) {
        this.name = name;
        this.damage = damage;
        this.attackSpeed = attackSpeed;
        this.level = 1;
        this.type = WeaponType.MELEE;
        this.attacking = false;
        this.attackRotation = 0f;
        this.attackTimer = 0f;

        // Try to load the texture from the specified path
        try {
            System.out.println("Trying to load texture from path: " + texturePath);
            if (Gdx.files.internal(texturePath).exists()) {
                this.texture = new Texture(Gdx.files.internal(texturePath));
                // Assign to parent class's texture field
                super.texture = this.texture;
                System.out.println("Texture loaded successfully from: " + texturePath);
            } else {
                System.out.println("Texture not found at: " + texturePath + ", trying alternative paths");
                // Try alternative paths
                String[] alternativePaths = {
                    "sword.png",
                    "images/weapons/sword.png",
                    "lwjgl3/build/resources/main/images/weapons/sword.png",
                    "assets/images/weapons/sword.png"
                };

                boolean textureLoaded = false;
                for (String path : alternativePaths) {
                    System.out.println("Trying alternative path: " + path);
                    if (Gdx.files.internal(path).exists()) {
                        this.texture = new Texture(Gdx.files.internal(path));
                        // Assign to parent class's texture field
                        super.texture = this.texture;
                        System.out.println("Texture loaded successfully from alternative path: " + path);
                        textureLoaded = true;
                        break;
                    } else {
                        System.out.println("Texture not found at alternative path: " + path);
                    }
                }

                // If no texture was loaded, create a fallback texture
                if (!textureLoaded) {
                    System.out.println("Creating fallback texture");
                    // Create a 1x1 white texture as fallback
                    com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(32, 32, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
                    pixmap.setColor(1, 0, 0, 1); // Red color for visibility
                    pixmap.fill();
                    this.texture = new Texture(pixmap);
                    // Assign to parent class's texture field
                    super.texture = this.texture;
                    pixmap.dispose();
                    System.out.println("Fallback texture created");
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading texture: " + e.getMessage());
            e.printStackTrace();

            // Create a fallback texture in case of error
            System.out.println("Creating fallback texture after error");
            try {
                com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(32, 32, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
                pixmap.setColor(1, 0, 0, 1); // Red color for visibility
                pixmap.fill();
                this.texture = new Texture(pixmap);
                // Assign to parent class's texture field
                super.texture = this.texture;
                pixmap.dispose();
                System.out.println("Fallback texture created after error");
            } catch (Exception e2) {
                System.out.println("Error creating fallback texture: " + e2.getMessage());
                e2.printStackTrace();
            }
        }

        System.out.println("Texture after loading: " + this.texture);

        // Set dimensions based on loaded texture, or use defaults
        if (this.texture != null) {
            if (this.level == 2) {
                this.width = 10;
                this.height = 20;
            } else {
                this.width = 30;
                this.height = 48;
            }
        } else {
            // Set default dimensions even if texture is null
            System.out.println("Texture is null, setting default dimensions");
            this.width = 30;
            this.height = 48;
        }

        this.range = 100;
        this.swingAngle = 90; // Degrees
    }

    @Override
    public void attack(Character user, Vector2 target) {
        // Implement melee attack logic
        // For now, just a placeholder
    }

    @Override
    public void upgrade() {
        level++;
        damage += 5;
        attackSpeed += 0.1f;
    }

    public float getRange() {
        return range;
    }

    public float getSwingAngle() {
        return swingAngle;
    }

    @Override
    public void dispose() {
        if (super.texture != null) {
            super.texture.dispose();
        }
    }

    /**
     * Updates the weapon state
     * @param deltaTime Time since last update
     */
    public void update(float deltaTime) {
        if (attacking) {
            // Update attack animation
            attackTimer += deltaTime;

            // Calculate rotation based on attack progress
            float attackDuration = 0.3f; // 300ms for full attack
            float progress = Math.min(attackTimer / attackDuration, 1.0f);

            // Swing from -30 to 90 degrees during attack
            attackRotation = -30f + progress * 120f;

            // End attack after duration
            if (attackTimer >= attackDuration) {
                attacking = false;
                attackTimer = 0f;
                attackRotation = 0f;
            }
        }
    }

    /**
     * Starts a weapon attack
     */
    public void startAttack() {
        attacking = true;
        attackTimer = 0f;
    }

    /**
     * Checks if weapon is currently attacking
     * @return true if attacking, false otherwise
     */
    public boolean isAttacking() {
        return attacking;
    }

    /**
     * Gets the current attack rotation angle
     * @return rotation angle in degrees
     */
    public float getAttackRotation() {
        return attackRotation;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public float getAttackSpeed() {
        return attackSpeed;
    }

    public void setAttackSpeed(float attackSpeed) {
        this.attackSpeed = attackSpeed;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        // Update dimensions when level changes
        if (this.level == 2) {
            this.width = 10;
            this.height = 20;
        } else {
            this.width = 30;
            this.height = 48;
        }
    }

    public WeaponType getType() {
        return type;
    }

    public void setType(WeaponType type) {
        this.type = type;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
        // Assign to parent class's texture field
        super.texture = texture;
    }

    public void setRange(float range) {
        this.range = range;
    }

    public void setSwingAngle(float swingAngle) {
        this.swingAngle = swingAngle;
    }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }

    public void setAttackRotation(float attackRotation) {
        this.attackRotation = attackRotation;
    }

    public void setAttackTimer(float attackTimer) {
        this.attackTimer = attackTimer;
    }

    public float getAttackTimer() {
        return attackTimer;
    }

    public Rectangle getBounds() {
        if (super.texture != null) {
            // Використовуємо фіксовані розміри для колізії
            float centerX = position.x + (super.texture.getWidth() * scale) / 2;
            float centerY = position.y + (super.texture.getHeight() * scale) / 2;
            return new Rectangle(
                centerX - (WEAPON_WIDTH * scale) / 2,
                centerY - (WEAPON_HEIGHT * scale) / 2,
                WEAPON_WIDTH * scale,
                WEAPON_HEIGHT * scale
            );
        }
        return null;
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setScale(float scale) {
        this.scale = scale;

        // Adjust dimensions based on level and scale
        if (this.level == 2 || this.level == 3) {
            // Smaller dimensions for levels 2 and 3
            this.width = 10f * scale;
            this.height = 20f * scale;
        } else {
            // Default dimensions for level 1
            this.width = 30f * scale;
            this.height = 48f * scale;
        }
    }

    public float getScale() {
        return scale;
    }
}
