package com.pixelteam.adventures.entities.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

public class PlayerController {
    private Player player;
    private Vector2 direction;
    private boolean attacking;

    public PlayerController(Player player) {
        this.player = player;
        this.direction = new Vector2();
        this.attacking = false;
    }

    public void update() {
        // Reset direction
        direction.set(0, 0);

        // WASD movement
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            direction.y = 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            direction.y = -1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            direction.x = -1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            direction.x = 1;
        }

        // Normalize direction if moving diagonally
        if (direction.len2() > 0) {
            direction.nor();
            player.move(direction);
        } else {
            // Stop player movement when no keys are pressed
            player.move(direction); // direction is (0,0) here
        }

        // Left mouse button attack
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            attacking = true;
            player.attack();
        } else {
            attacking = false;
        }
    }

    public boolean isAttacking() {
        return attacking;
    }
}
