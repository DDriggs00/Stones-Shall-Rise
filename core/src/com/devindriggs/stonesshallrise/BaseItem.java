package com.devindriggs.stonesshallrise;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;

public abstract class BaseItem {
    private World world;
    private TiledMap map;
    private Body body;
    Fixture fixture;
    private Rectangle rectangle;
    private Polygon polygon;

    BaseItem(World world1, TiledMap tiledMap, Rectangle rectangle1, short type) {
        world = world1;
        map = tiledMap;
        rectangle = rectangle1;

        BodyDef bodydef = new BodyDef();
        bodydef.type = BodyDef.BodyType.StaticBody;
        bodydef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / MainGame.PIXELS_PER_METER, (rectangle.getY() + rectangle.getHeight() / 2) / MainGame.PIXELS_PER_METER);

        body = world.createBody(bodydef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(rectangle.getWidth() / 2 / MainGame.PIXELS_PER_METER, rectangle.getHeight() / 2 / MainGame.PIXELS_PER_METER);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = type;
        fixtureDef.filter.maskBits = MainGame.COLLISION_BIT_PLAYER | MainGame.COLLISION_BIT_TERRAIN | MainGame.COLLISION_BIT_DEFAULT;
        fixture = body.createFixture(fixtureDef);
    }
    BaseItem(World world1, TiledMap tiledMap, Polygon polygon, short type) {
        world = world1;
        map = tiledMap;
        this.polygon = polygon;
        this.rectangle = polygon.getBoundingRectangle();

        BodyDef bodydef = new BodyDef();
        bodydef.type = BodyDef.BodyType.StaticBody;
        bodydef.position.set(polygon.getX() / MainGame.PIXELS_PER_METER, polygon.getY() / MainGame.PIXELS_PER_METER);

        body = world.createBody(bodydef);
        float[] temp = polygon.getVertices();
        for (int i = 0; i < temp.length; i++) {
            temp[i] = temp[i] / MainGame.PIXELS_PER_METER;
        }
        PolygonShape shape = new PolygonShape();
        shape.set(temp);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = type;
        fixtureDef.filter.maskBits = MainGame.COLLISION_BIT_PLAYER | MainGame.COLLISION_BIT_TERRAIN | MainGame.COLLISION_BIT_DEFAULT;
        fixture = body.createFixture(fixtureDef);
    }

    public abstract void onContact();

    void destroy() {
        Filter filter = new Filter();
        filter.categoryBits = MainGame.COLLISION_BIT_REMOVED;
        fixture.setFilterData(filter);

        TiledMapTileLayer layer = (TiledMapTileLayer)(map.getLayers().get(1));
        TiledMapTileLayer.Cell cell = layer.getCell((int)body.getPosition().x, (int)body.getPosition().y);
        try {
            cell.setTile(null);
        }
        catch (java.lang.NullPointerException ignored) {}
    }
}
