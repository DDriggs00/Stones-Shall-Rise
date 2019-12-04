package com.devindriggs.stonesshallrise;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainGame extends Game
{
    public SpriteBatch batch;

    static final int SCREEN_WIDTH = 320;
    static final int SCREEN_HEIGHT = 240;
    static final float PIXELS_PER_METER = 16;
    static final float MAX_VEL = 6;
    static final float MAX_VEL_RUN = 9;
    static final float ACCELERATION = .2f;
    static final float ACCELERATION_RUN = .4f;
    static final float DECELERATION = .4f;
    static final float g = -30f;
    static final float JUMP_POWER = 15f;

    static final short COLLISION_BIT_DEFAULT    = 0b00000001;
    static final short COLLISION_BIT_PLAYER     = 0b00000010;
    static final short COLLISION_BIT_ENEMY      = 0b00000100;
    static final short COLLISION_BIT_POWERUP    = 0b00001000;
    static final short COLLISION_BIT_COIN       = 0b00010000;
    static final short COLLISION_BIT_TERRAIN    = 0b00100000;
    static final short COLLISION_BIT_REMOVED    = 0b01000000;

    @Override
    public void create() 
    {
        batch = new SpriteBatch();
        setScreen(new MenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

}