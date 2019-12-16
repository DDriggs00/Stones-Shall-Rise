package com.devindriggs.stonesshallrise;

import com.badlogic.gdx.physics.box2d.World;

public class Golem extends PhysicsActor {

    private Adventurer player;

    public Golem(World world, int locationX, int locationY, Adventurer player) {
        super(world, "enemies/golem.png");
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
    protected void kill() {
        if (currentState != state.dead) {
            currentState = state.dead;
        }
        world.destroyBody(body);
    }

    @Override
    public void onContact() {
        if (currentState != state.dead) {
            player.kill();
        }
    }
}
