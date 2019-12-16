package com.devindriggs.stonesshallrise;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class LevelScreen implements Screen
{
    // reference to root game
    private MainGame game;

    private String levelName;

    // reference to user interface
    private UI ui;

    int mapWidth;
    int mapHeight;

    private TiledMap map;
    private TmxMapLoader mapLoader;
    private OrthogonalTiledMapRenderer renderer;

    private OrthographicCamera camera;
    private Viewport viewport;

    private World world;

    private Adventurer player;
    private Golem golem;

    Music backgroundMusic;

    private Box2DDebugRenderer testRender;

    public LevelScreen(MainGame game, String levelName) {
        // Initialize base game object
        this.game = game;

        // copy levelName
        this.levelName = levelName;

        // Initialize UI object
        ui = new UI(game);

        // Setup camera
        camera = new OrthographicCamera();
        viewport = new FitViewport(MainGame.SCREEN_WIDTH / MainGame.PIXELS_PER_METER, MainGame.SCREEN_HEIGHT / MainGame.PIXELS_PER_METER, camera);
        camera.position.set(MainGame.SCREEN_WIDTH / 2f / MainGame.PIXELS_PER_METER, MainGame.SCREEN_HEIGHT / 2f / MainGame.PIXELS_PER_METER, 0);

        // Render map
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("maps/" + levelName + ".tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MainGame.PIXELS_PER_METER);

        // get map dimensions
        mapWidth = map.getProperties().get("width", Integer.class);
        mapHeight = map.getProperties().get("height", Integer.class);

        // Initialize world
        world = new World(new Vector2(0, MainGame.g), true);

        // Initialize player
        player = new Adventurer(ui, world, 1, 1);

        // Initialize enemies
        golem = new Golem(world, 9, 5, player);

        // DEBUG RENDERER
        testRender = new Box2DDebugRenderer();

        world.setContactListener(new WorldContactListener());

        // Create physics tiles for the map tiles
        prepareMap();

        // Setup sound
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sound/Labyrinth-Of-Time.ogg"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(.5f);
        backgroundMusic.play();

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // update physics
        update(delta);

        // Clear screen
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_CLEAR_VALUE);

        // Render map
        renderer.render();
        testRender.render(world, camera.combined);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // Render actors
        player.draw(game.batch);    // player
        golem.draw(game.batch);

        game.batch.end();

        // Render user interface (score, etc.)
        game.batch.setProjectionMatrix(ui.uiStage.getCamera().combined);
        ui.uiStage.draw();

    }

    public void update(float delta) {

        world.step(1/60f, 6, 2);

        if (player.dead) {
            backgroundMusic.stop();
        }

        // Update all actors
        player.update(delta);
        golem.update(delta);
        ui.update(delta);

        if (player.body.getPosition().x < MainGame.SCREEN_WIDTH / 2f / MainGame.PIXELS_PER_METER) {
            camera.position.x = MainGame.SCREEN_WIDTH / 2f / MainGame.PIXELS_PER_METER;
        }
        else if (player.body.getPosition().x > mapWidth - (MainGame.SCREEN_WIDTH / 2f / MainGame.PIXELS_PER_METER)) {
            camera.position.x = mapWidth - MainGame.SCREEN_WIDTH / 2f / MainGame.PIXELS_PER_METER;
        }
        else if (!player.dead) {
            camera.position.x = player.body.getPosition().x;
        }

        if (player.body.getPosition().y < MainGame.SCREEN_HEIGHT / 2f / MainGame.PIXELS_PER_METER) {
            camera.position.y = MainGame.SCREEN_HEIGHT / 2f / MainGame.PIXELS_PER_METER;
        }
        else if (player.body.getPosition().y > mapHeight - (MainGame.SCREEN_HEIGHT / 2f / MainGame.PIXELS_PER_METER)) {
            camera.position.y = mapHeight - MainGame.SCREEN_HEIGHT / 2f / MainGame.PIXELS_PER_METER;
        }
        else if (!player.dead) {
            camera.position.y = player.body.getPosition().y;
        }
        camera.update();
        renderer.setView(camera);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    public void prepareMap() {
        BodyDef bodydef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fd = new FixtureDef();
        Body body;

        for(MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bodydef.type = BodyDef.BodyType.StaticBody;
            bodydef.position.set((rect.getX() + rect.getWidth() / 2) / MainGame.PIXELS_PER_METER, (rect.getY() + rect.getHeight() / 2) / MainGame.PIXELS_PER_METER);

            body = world.createBody(bodydef);

            shape.setAsBox(rect.getWidth() / 2 / MainGame.PIXELS_PER_METER, rect.getHeight() / 2 / MainGame.PIXELS_PER_METER);
            fd.shape = shape;
            body.createFixture(fd);
        }
        for(MapObject object : map.getLayers().get(2).getObjects().getByType(PolygonMapObject.class)){
            Polygon polygon = ((PolygonMapObject) object).getPolygon();

            float[] temp = polygon.getVertices();
            for (int i = 0; i < temp.length; i++) {
                temp[i] = temp[i] / MainGame.PIXELS_PER_METER;
            }
            PolygonShape polygonShape = new PolygonShape();
            polygonShape.set(temp);

            bodydef.type = BodyDef.BodyType.StaticBody;
            bodydef.position.set(polygon.getX() / MainGame.PIXELS_PER_METER, polygon.getY() / MainGame.PIXELS_PER_METER);

            body = world.createBody(bodydef);

            fd.shape = polygonShape;
            body.createFixture(fd);
        }

        for(MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)){
            // Smallest coins
            new Coin(ui, world, map, ((RectangleMapObject) object).getRectangle(), 5);
        }

        for(MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)){
            // Smallest coins
            new Coin(ui, world, map, ((RectangleMapObject) object).getRectangle(), 25);
        }

        for(MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)){
            // coins
            new Coin(ui, world, map, ((RectangleMapObject) object).getRectangle(), 100);
        }

        for(MapObject object : map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)){
            // coins
            new Coin(ui, world, map, ((RectangleMapObject) object).getRectangle(), 1000);
        }

        for(MapObject object : map.getLayers().get(8).getObjects().getByType(PolygonMapObject.class)){
            new Spike(ui, world, map, ((PolygonMapObject) object).getPolygon(), player);
        }
        for(MapObject object : map.getLayers().get(9).getObjects().getByType(RectangleMapObject.class)){
            new Chest(ui, world, map, ((RectangleMapObject) object).getRectangle());
        }
    }


}