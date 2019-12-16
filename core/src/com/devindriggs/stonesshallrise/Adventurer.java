package com.devindriggs.stonesshallrise;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

public class Adventurer extends PhysicsActor {

    private float atkTime = .5f; // duration of attack
    float attackTimeRemain = 0;
    private boolean running = false;

    private UI ui;

    public Adventurer(UI ui, World world, int locationX, int locationY) {
        super(world, "player/adventurer-v1.5-Sheet.png");
        short collisionBits = MainGame.COLLISION_BIT_DEFAULT | MainGame.COLLISION_BIT_ENEMY | MainGame.COLLISION_BIT_POWERUP | MainGame.COLLISION_BIT_COIN | MainGame.COLLISION_BIT_TERRAIN;
        createPhysicsBody(.95f, 1.85f, locationX, locationY, "Adventurer", MainGame.COLLISION_BIT_PLAYER, collisionBits);
        textureOffsetY = .1f;
        createIdleSprite(0, 0, 50, 37, 1, 0.25f);
        createWalkSprite(50, 37, 50, 37, 6, 0.25f);
        createRunSprite(50, 37, 50, 37, 6, 0.15f);
        createJumpSprite(50, 2*37, 50, 37, 3, 0.15f);
        createFallSprite(50, 3*37, 50, 37, 2, 0.15f);
        createAtkSprite(0, 6*37, 50, 37, 7, 0.08f);
        createDieSprite(0, 9*37, 50, 37, 4, 0.25f);

        this.ui = ui;
    }

    public void update(float delta) {

        setState(delta);
        if (!dead && isGrounded() && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            body.applyLinearImpulse(new Vector2(0, MainGame.JUMP_POWER), body.getWorldCenter(), true);
            Sound sound = Gdx.audio.newSound(Gdx.files.internal("sound/jump01.mp3"));
            sound.play();

        }
        if (!dead && Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            flipped = false;
            float maxVel, acceleration;
            if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                maxVel = MainGame.MAX_VEL_RUN;
                acceleration = MainGame.ACCELERATION_RUN;
                running = true;
            }
            else {
                maxVel = MainGame.MAX_VEL;
                acceleration = MainGame.ACCELERATION;
                running = false;
            }
            if (body.getLinearVelocity().x <= maxVel) {
                body.applyLinearImpulse(new Vector2(acceleration, 0), body.getWorldCenter(), true);
            }
        }
        if (!dead && Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            flipped = true;
            float maxVel, acceleration;
            if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                maxVel = MainGame.MAX_VEL_RUN;
                acceleration = MainGame.ACCELERATION_RUN;
                running = true;
            }
            else {
                maxVel = MainGame.MAX_VEL;
                acceleration = MainGame.ACCELERATION;
                running = false;
            }
            if (body.getLinearVelocity().x >= -maxVel) {
                body.applyLinearImpulse(new Vector2(-1 * acceleration, 0), body.getWorldCenter(), true);
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.J)) {
            attack();
        }
        super.update(delta);
    }

    @Override
    protected void kill() {
        dead = true;
        Sound sound;
        if (body.getPosition().y < -1) {
            sound = Gdx.audio.newSound(Gdx.files.internal("sound/falling1.mp3"));
        }
        else {
            sound = Gdx.audio.newSound(Gdx.files.internal("sound/powerdown07.mp3"));
        }

        sound.play();
        ui.triggerLose();
    }

    @Override
    public void onContact() {}

    private void attack() {
        if (currentState != state.attacking) {
            attackTimeRemain = atkTime;
            currentState = state.attacking;

            Array<Body> bodies = new Array<Body>();
            world.getBodies(bodies);
            if (bodies.size == 0) {
                return;
            }

            for(Body b: bodies) {
                Vector2 v = b.getPosition();
                if (b.getFixtureList() == null || b.getFixtureList().size == 0) return;
                Object obj = b.getFixtureList().first().getUserData();

                // TODO better math here
                if(Math.abs(v.x - body.getPosition().x) < 3 && Math.abs(v.y - body.getPosition().y) < 3 ) {
                    if (obj != null && PhysicsActor.class.isAssignableFrom(obj.getClass())) {
                        ((PhysicsActor)obj).kill();
                    }
                }
            }

        }
    }

    private void setState(float delta) {
        if (dead) {
            currentState = state.dead;
        }
        else if ( attackTimeRemain > 0) {
            attackTimeRemain -= delta;
            currentState = state.attacking;
        }
        else if (!isGrounded() && body.getLinearVelocity().y > 1) {
            currentState = state.jumping;
        }
        else if (!isGrounded() && body.getLinearVelocity().y < 1) {
            currentState = state.falling;
        }
        else if (isMoving() != 0) {
            if (running) {
                currentState = state.running;
            }
            else {
                currentState = state.walking;
            }
        }
        else {
            currentState = state.idle;
        }
    }

    // Physics constructor
    @Override
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

        EdgeShape wpn = new EdgeShape();
        wpn.set(new Vector2(-5 / 16f, 5 / 16f), new Vector2(5 / 16f, -5 / 16f));
        fd.shape = wpn;
        fd.isSensor = true;
        body.createFixture(fd).setUserData("sword");
    }
}
