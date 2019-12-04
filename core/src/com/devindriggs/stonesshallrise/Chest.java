package com.devindriggs.stonesshallrise;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

public class Chest extends BaseItem {
    private UI ui;
    public Chest(UI ui, World world1, TiledMap tiledMap, Rectangle rectangle1) {
        super(world1, tiledMap, rectangle1, MainGame.COLLISION_BIT_COIN);
        this.ui = ui;
        fixture.setUserData(this);
    }

    @Override
    public void onContact() {
        destroy();
        ui.triggerWin();
    }
}
