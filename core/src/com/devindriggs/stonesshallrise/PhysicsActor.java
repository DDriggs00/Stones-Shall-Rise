package com.devindriggs.stonesshallrise;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import java.io.IOException;

public abstract class PhysicsActor extends Sprite {

    protected enum state {
        walking, running, idle, attacking, jumping, falling, hurt, dead
    }

    int health;
    private float hurtGraceTime = .5f;
    private float stateTime = 0;
    state currentState = state.idle;
    private state lastState = state.idle;

    World world;
    Body body;

    private float textureOffsetX = 0;
    float textureOffsetY = 0;

    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> runAnimation;
    private Animation<TextureRegion> attackAnimation;
    private Animation<TextureRegion> jumpAnimation;
    private Animation<TextureRegion> fallAnimation;
    private Animation<TextureRegion> dieAnimation;
    private Animation<TextureRegion> hurtAnimation;
    private boolean hasIdleAnim = false;
    private boolean hasWalkAnim = false;
    private boolean hasRunAnim = false;
    private boolean hasAtkAnim = false;
    private boolean hasJumpAnim = false;
    private boolean hasFallAnim = false;
    private boolean hasDieAnim = false;
    private boolean hasHurtAnim = false;

    boolean flipped;

    boolean dead = false;

    protected Fixture fixture;

    // main constructor
    public PhysicsActor(World world, String textureFile) {
        this.world = world;
        Texture texture = new Texture(Gdx.files.internal(textureFile));
        setTexture(texture);
        health = 1;
    }
    // main constructor
    PhysicsActor(World world, String textureFile, int health) {
        this.world = world;
        Texture texture = new Texture(Gdx.files.internal(textureFile));
        setTexture(texture);
        this.health = health;
    }

    // Physics constructor
    void createPhysicsBody(float width, float height, float posX, float posY, Object userData, short categoryBits, short collisionBits) {
        BodyDef bd = new BodyDef();
        bd.position.set(posX, posY);
        bd.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bd);

        FixtureDef fd = new FixtureDef();
        PolygonShape r = new PolygonShape();
        r.setAsBox(width / 2f, height / 2f);
        fd.shape = r;
        fd.filter.categoryBits = categoryBits;
        fd.filter.maskBits = collisionBits;
        body.createFixture(fd).setUserData(userData);
    }

    // Sprite generators
    void createIdleSprite(int startX, int startY, int sizeX, int sizeY, int frames, float frameLength) {
        idleAnimation = generateAnimation(startX, startY, sizeX, sizeY, frames, frameLength);
        hasIdleAnim = true;
    }
    void createWalkSprite(int startX, int startY, int sizeX, int sizeY, int frames, float frameLength) {
        walkAnimation = generateAnimation(startX, startY, sizeX, sizeY, frames, frameLength);
        hasWalkAnim = true;
    }
    void createRunSprite(int startX, int startY, int sizeX, int sizeY, int frames, float frameLength) {
        runAnimation = generateAnimation(startX, startY, sizeX, sizeY, frames, frameLength);
        hasRunAnim = true;
    }
    void createJumpSprite(int startX, int startY, int sizeX, int sizeY, int frames, float frameLength) {
        jumpAnimation = generateAnimation(startX, startY, sizeX, sizeY, frames, frameLength);
        hasJumpAnim = true;
    }
    void createFallSprite(int startX, int startY, int sizeX, int sizeY, int frames, float frameLength) {
        fallAnimation = generateAnimation(startX, startY, sizeX, sizeY, frames, frameLength);
        hasFallAnim = true;
    }
    void createAtkSprite(int startX, int startY, int sizeX, int sizeY, int frames, float frameLength) {
        attackAnimation = generateAnimation(startX, startY, sizeX, sizeY, frames, frameLength);
        hasAtkAnim = true;
    }
    void createDieSprite(int startX, int startY, int sizeX, int sizeY, int frames, float frameLength) {
        dieAnimation = generateAnimation(startX, startY, sizeX, sizeY, frames, frameLength);
        hasDieAnim = true;
    }
    void createHurtSprite(int startX, int startY, int sizeX, int sizeY, int frames, float frameLength) {
        hurtAnimation = generateAnimation(startX, startY, sizeX, sizeY, frames, frameLength);
        hasHurtAnim = true;
    }

    // Update physics and sprites
    public void update(float delta) throws IOException {
        // Kill triggers for all entities
        if (!dead) {
            // prevent double killing
            if (body.getPosition().y < -1) {
                // If falling out of the map
                kill();
            }
        }


        setBounds(0, 0, getRegionWidth() / MainGame.PIXELS_PER_METER, getRegionHeight() / MainGame.PIXELS_PER_METER);
        setPosition(body.getPosition().x - getWidth() / 2 + textureOffsetX, body.getPosition().y - getHeight() / 2 + textureOffsetY);

        if (currentState != lastState) {
            stateTime = 0;
            lastState = currentState;
        }
        else {
            stateTime += delta;
        }

        if (currentState == state.hurt && stateTime >= hurtGraceTime) {
            currentState = state.idle;
        }

        TextureRegion t = getRegion();

        assert t != null;
        if (flipped && !t.isFlipX()) {
            t.flip(true, false);
        }
        else {
            if (!flipped && t.isFlipX()) {
                t.flip(true, false);
            }
        }

        setRegion(t);
    }

    private TextureRegion getRegion() {
        switch (currentState) {
            case idle:
                if (!hasIdleAnim) return null;
                return idleAnimation.getKeyFrame(stateTime, true);
            case walking:
                if (!hasWalkAnim) return null;
                return walkAnimation.getKeyFrame(stateTime, true);
            case running:
                if (!hasRunAnim) return null;
                return runAnimation.getKeyFrame(stateTime, true);
            case jumping:
                if (!hasJumpAnim) return null;
                return jumpAnimation.getKeyFrame(stateTime, false);
            case falling:
                if (!hasFallAnim) return null;
                return fallAnimation.getKeyFrame(stateTime, false);
            case attacking:
                if (!hasAtkAnim) return null;
                return attackAnimation.getKeyFrame(stateTime, false);
            case dead:
                if (!hasDieAnim) return null;
                return dieAnimation.getKeyFrame(stateTime, false);
            case hurt:
                if (hasHurtAnim) return hurtAnimation.getKeyFrame(stateTime, false);
                else return idleAnimation.getKeyFrame(stateTime, true);
            default:
                return null;
        }
    }

    // determines whether actor is grounded
    boolean isGrounded() {
        // TODO allow for vertically moving platforms
        return body.getLinearVelocity().y == 0;
    }

    // determines whether actor is moving
    // returns -1 for left, 0 for not moving, and 1 for right
    int isMoving() {
        // TODO allow for horizontally moving platforms
        if (body.getLinearVelocity().x == 0) {
            return 0;
        }
        else if (body.getLinearVelocity().x > 0) {
            return 1;
        }
        else {
            return -1;
        }
    }

    protected void hurt() throws IOException {

        if (currentState == state.hurt) {
            return;
        }
        lastState = currentState;
        currentState = state.hurt;
        health--;
        if (health <= 0) {
            this.kill();
        }
    }

    protected abstract void kill() throws IOException;

    public abstract void onContact() throws IOException;

    // Internal Helper functions
    private Animation<TextureRegion> generateAnimation(int startX, int startY, int sizeX, int sizeY, int frames, float frameLength) {
        Array<TextureRegion> anim = new Array<>();
        for (int i = 0; i < frames; i++) {
            anim.add(new TextureRegion(getTexture(), startX + sizeX * i, startY, sizeX, sizeY));
        }
        return new Animation<>(frameLength, anim);
    }
}
