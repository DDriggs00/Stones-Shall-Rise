package com.devindriggs.stonesshallrise;

import com.badlogic.gdx.physics.box2d.World;

public class Golem extends PhysicsActor {

    public Golem(World world, int locationX, int locationY) {
        super(world, "enemies/golem.png");
        short collisionBits = MainGame.COLLISION_BIT_DEFAULT | MainGame.COLLISION_BIT_PLAYER | MainGame.COLLISION_BIT_POWERUP | MainGame.COLLISION_BIT_COIN | MainGame.COLLISION_BIT_TERRAIN;
        super.createPhysicsBody(.95f, 1.85f, locationX, locationY, "Adventurer", MainGame.COLLISION_BIT_ENEMY, collisionBits);
        super.createIdleSprite(0, 0, 64, 64, 5, 0.25f);
        super.createWalkSprite(0, 64, 64, 64, 5, 0.5f);
        super.createRunSprite(0, 64, 64, 64, 5, 0.3f);
//        super.createJumpSprite(50, 2*37, 50, 37, 3, 0.15f);
//        super.createFallSprite(50, 3*37, 50, 37, 2, 0.15f);
        super.createAtkSprite(0, 128, 64, 64, 5, 0.1f);
        super.createDieSprite(0, 256, 64, 64, 10, 0.15f);

    }

    @Override
    protected void kill() {

    }
}
