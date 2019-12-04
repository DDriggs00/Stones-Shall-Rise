package com.devindriggs.stonesshallrise;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

public class Coin extends BaseItem {
    private int value;
    private UI ui;

    public Coin(UI ui, World world1, TiledMap tiledMap, Rectangle rectangle1, int value) {
        super(world1, tiledMap, rectangle1, MainGame.COLLISION_BIT_COIN);
        this.value = value;
        this.ui = ui;
        fixture.setUserData(this);
    }

    @Override
    public void onContact() {
        ui.addScore(value);
        Sound sound = Gdx.audio.newSound(Gdx.files.internal("sound/coin05.mp3"));
        sound.play();
        destroy();
    }
}
