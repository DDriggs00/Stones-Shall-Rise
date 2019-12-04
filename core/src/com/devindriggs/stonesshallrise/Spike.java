package com.devindriggs.stonesshallrise;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.physics.box2d.World;

public class Spike extends BaseItem {
    private UI ui;
    private Adventurer player;
    public Spike(UI ui, World world1, TiledMap tiledMap, Polygon polygon, Adventurer adventurer) {
        super(world1, tiledMap, polygon, MainGame.COLLISION_BIT_COIN);
        this.ui = ui;
        this.player = adventurer;
        fixture.setUserData(this);
    }

    @Override
    public void onContact() {
        player.kill();
    }
}