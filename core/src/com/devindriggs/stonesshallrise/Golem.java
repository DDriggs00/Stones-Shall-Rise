package com.devindriggs.stonesshallrise;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class Golem extends PhysicsActor {

    private Adventurer player;
    private int maxVel = 5;
    private float accel = 0.2f;
    private boolean direction;
    private float directionTime = 0;

    Golem(World world, int locationX, int locationY, Adventurer player) {
        super(world, "enemies/golem.png", 3);
        short collisionBits = MainGame.COLLISION_BIT_DEFAULT | MainGame.COLLISION_BIT_PLAYER | MainGame.COLLISION_BIT_POWERUP | MainGame.COLLISION_BIT_COIN | MainGame.COLLISION_BIT_TERRAIN;
        super.createPhysicsBody(1.7f, 2.4f, locationX, locationY, this, MainGame.COLLISION_BIT_ENEMY, collisionBits);
        textureOffsetY =.3f;

        super.createIdleSprite(0, 0, 48, 48, 5, 0.15f);
        super.createWalkSprite(0, 48, 48, 48, 5, 0.5f);
        super.createRunSprite(0, 48, 48, 48, 5, 0.3f);
//        super.createJumpSprite(50, 2*37, 50, 37, 3, 0.15f);
//        super.createFallSprite(50, 3*37, 50, 37, 2, 0.15f);
        super.createAtkSprite(0, 96, 48, 48, 5, 0.1f);
        super.createDieSprite(0, 192, 48, 48, 10, 0.15f);

        this.player = player;
    }

    @Override
    public void update(float delta) throws IOException {
        super.update(delta);
        directionTime += delta;
        if (directionTime > 1) {
            directionTime = 0;
            direction = ThreadLocalRandom.current().nextInt(0, 2) == 1;
        }
        if (direction) {
            flipped = true;
            if (body.getLinearVelocity().x >= -maxVel) {
                body.applyLinearImpulse(new Vector2(-1 * accel, 0), body.getWorldCenter(), true);
            }
        }
        else {
            flipped = true;
            if (body.getLinearVelocity().x <= maxVel) {
                body.applyLinearImpulse(new Vector2(accel, 0), body.getWorldCenter(), true);
            }
        }
    }

    protected void hurt() throws IOException {
        super.hurt();
        Sound sound;
        if (health > 0) {
            sound = Gdx.audio.newSound(Gdx.files.internal("sound/golemHurt.mp3"));
        }
        else {
            sound = Gdx.audio.newSound(Gdx.files.internal("sound/golemDie.mp3"));
        }
        sound.play();
    }

    @Override
    protected void kill() {
        if (currentState != state.dead) {
            currentState = state.dead;
        }
        world.destroyBody(body);
    }

    @Override
    public void onContact() throws IOException {
        if (currentState != state.dead) {
            player.hurt();
        }
    }
}
