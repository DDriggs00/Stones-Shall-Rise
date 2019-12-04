package com.devindriggs.stonesshallrise;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

public abstract class PhysicsActor extends Sprite {

    protected enum state {
        walking, running, idle, attacking, jumping, falling, dead
    }
    private float stateTime = 0;
    protected state currentState = state.idle;
    protected state lastState = state.idle;

    protected World world;
    Body body;

    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> runAnimation;
    private Animation<TextureRegion> attackAnimation;
    private Animation<TextureRegion> jumpAnimation;
    private Animation<TextureRegion> fallAnimation;
    private Animation<TextureRegion> dieAnimation;
    private boolean hasIdleAnim = false;
    private boolean hasWalkAnim = false;
    private boolean hasRunAnim = false;
    private boolean hasAtkAnim = false;
    private boolean hasJumpAnim = false;
    private boolean hasFallAnim = false;
    private boolean hasDieAnim = false;

    protected boolean flipped;

    protected boolean dead = false;

    // main constructor
    public PhysicsActor(World world, String textureFile) {
        this.world = world;
        Texture texture = new Texture(Gdx.files.internal(textureFile));
        setTexture(texture);
    }

    // Physics constructor
    void createPhysicsBody(float width, float height, float posX, float posY, String name, short categoryBits, short collisionBits) {
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
        body.createFixture(fd).setUserData(name);
    }

    // Sprite generators
    protected void createIdleSprite(int startX, int startY, int sizeX, int sizeY, int frames, float frameLength) {
        idleAnimation = generateAnimation(startX, startY, sizeX, sizeY, frames, frameLength);
        hasIdleAnim = true;
    }
    protected void createWalkSprite(int startX, int startY, int sizeX, int sizeY, int frames, float frameLength) {
        walkAnimation = generateAnimation(startX, startY, sizeX, sizeY, frames, frameLength);
        hasWalkAnim = true;
    }
    protected void createRunSprite(int startX, int startY, int sizeX, int sizeY, int frames, float frameLength) {
        runAnimation = generateAnimation(startX, startY, sizeX, sizeY, frames, frameLength);
        hasRunAnim = true;
    }
    protected void createJumpSprite(int startX, int startY, int sizeX, int sizeY, int frames, float frameLength) {
        jumpAnimation = generateAnimation(startX, startY, sizeX, sizeY, frames, frameLength);
        hasJumpAnim = true;
    }
    protected void createFallSprite(int startX, int startY, int sizeX, int sizeY, int frames, float frameLength) {
        fallAnimation = generateAnimation(startX, startY, sizeX, sizeY, frames, frameLength);
        hasFallAnim = true;
    }
    protected void createAtkSprite(int startX, int startY, int sizeX, int sizeY, int frames, float frameLength) {
        attackAnimation = generateAnimation(startX, startY, sizeX, sizeY, frames, frameLength);
        hasAtkAnim = true;
    }
    protected void createDieSprite(int startX, int startY, int sizeX, int sizeY, int frames, float frameLength) {
        dieAnimation = generateAnimation(startX, startY, sizeX, sizeY, frames, frameLength);
        hasDieAnim = true;
    }

    // Update physics and sprites
    public void update(float delta) {
        // Kill triggers for all entities
        if (!dead) {
            // prevent double killing
            if (body.getPosition().y < -1) {
                // If falling out of the map
                kill();
            }
        }


        setBounds(0, 0, getRegionWidth() / MainGame.PIXELS_PER_METER, getRegionHeight() / MainGame.PIXELS_PER_METER);
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);

        if (currentState != lastState) {
            stateTime = 0;
            lastState = currentState;
        }
        else {
            stateTime += delta;
        }

        TextureRegion t = getRegion();

        if (flipped && !t.isFlipX()) {
            t.flip(true, false);
        }
        else if (!flipped && t.isFlipX()) {
            t.flip(true, false);
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
            default:
                return null;
        }
    }

    // determines whether actor is grounded
    protected boolean isGrounded() {
        // TODO allow for vertically moving platforms
        if (body.getLinearVelocity().y == 0) {
            return true;
        }
        else {
            return false;
        }
    }

    // determines whether actor is moving
    // returns -1 for left, 0 for not moving, and 1 for right
    protected int isMoving() {
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

    protected abstract void kill();

    // Internal Helper functions
    private Animation<TextureRegion> generateAnimation(int startX, int startY, int sizeX, int sizeY, int frames, float frameLength) {
        Array<TextureRegion> anim = new Array<>();
        for (int i = 0; i < frames; i++) {
            anim.add(new TextureRegion(getTexture(), startX + sizeX * i, startY, sizeX, sizeY));
        }
        return new Animation<>(frameLength, anim);
    }
}
