package com.devindriggs.stonesshallrise;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

public class Checkpoint extends BaseItem {

    private boolean used = false;
    private LevelScreen levelScreen;

    Checkpoint(World world1, TiledMap tiledMap, Rectangle rectangle1, LevelScreen levelScreen) {
        super(world1, tiledMap, rectangle1, MainGame.COLLISION_BIT_COIN);

        this.levelScreen = levelScreen;

        fixture.setUserData(this);
    }

    @Override
    public void onContact() {
        if (!used) {
            used = true;
            levelScreen.setSpawn((int)this.fixture.getBody().getPosition().x, (int)this.fixture.getBody().getPosition().y);
            Sound sound = Gdx.audio.newSound(Gdx.files.internal("sound/checkpoint.mp3"));
            sound.play();
        }
    }
}
