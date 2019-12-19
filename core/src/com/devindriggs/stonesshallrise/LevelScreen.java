package com.devindriggs.stonesshallrise;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;

public class LevelScreen implements Screen
{
    // reference to root game
    private MainGame game;

    private String levelName;

    // reference to user interface
    private UI ui;

    private int mapWidth;
    private int mapHeight;

    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private OrthographicCamera camera;
    private Viewport viewport;

    private World world;

    private Adventurer player;
    private Array<Golem> golems;

    Music backgroundMusic;

    private int spawnX, spawnY;

    private boolean levelOver = false;
    private boolean levelWon = false;

//    private Box2DDebugRenderer testRender;

    LevelScreen(MainGame game, String levelName) throws IOException {
        this(game, levelName, 1, 1);
    }
    private LevelScreen(MainGame game, String levelName, int spawnX, int spawnY) throws IOException {
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
        TmxMapLoader mapLoader = new TmxMapLoader();
        map = mapLoader.load("maps/" + levelName + ".tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MainGame.PIXELS_PER_METER);

        // get map dimensions
        mapWidth = map.getProperties().get("width", Integer.class);
        mapHeight = map.getProperties().get("height", Integer.class);

        // Initialize world
        world = new World(new Vector2(0, MainGame.g), true);

        // Initialize player
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        player = new Adventurer(ui, world, spawnX, spawnY, this);

        // Initialize enemies (read from CSV)
        golems = new Array<>();
        BufferedReader csvReader = new BufferedReader(new FileReader("maps/" + levelName + ".csv"));
        String row;
        while ((row = csvReader.readLine()) != null) {
            String[] data = row.split(",");
            if (Integer.parseInt(data[0]) > -1 && Integer.parseInt(data[1]) > -1) {
                golems.add(new Golem(world, Integer.parseInt(data[0]), Integer.parseInt(data[1]), player));
            }
        }
        csvReader.close();

        // DEBUG RENDERER
//        testRender = new Box2DDebugRenderer();

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
        try {
            update(delta);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Clear screen
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_CLEAR_VALUE);

        // Render map
        renderer.render();
//        testRender.render(world, camera.combined);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // Render actors
        player.draw(game.batch);    // player
        for (Golem golem: golems) {
            golem.draw(game.batch);
        }

        game.batch.end();

        // Render user interface (score, etc.)
        game.batch.setProjectionMatrix(ui.uiStage.getCamera().combined);
        ui.uiStage.draw();

    }

    private void update(float delta) throws IOException {

        world.step(1/60f, 6, 2);

        if (levelOver && Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            if (getNextLevel() != null) {
                if (getNextLevel().equals(levelName)) {
                    game.setScreen(new LevelScreen(game, getNextLevel(), spawnX, spawnY));
                }
                else {
                    game.setScreen(new LevelScreen(game, getNextLevel()));
                }
            }
        }

        // Update all actors
        player.update(delta);
        for (Golem golem: golems) {
            golem.update(delta);
        }
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

    private void prepareMap() {
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

        for(MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            // Checkpoints
            new Checkpoint(world, map, ((RectangleMapObject) object).getRectangle(), this);
        }

        for(MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)){
            // Smallest coins
            new Coin(ui, world, map, ((RectangleMapObject) object).getRectangle(), 5);
        }

        for(MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)){
            // Small coins
            new Coin(ui, world, map, ((RectangleMapObject) object).getRectangle(), 25);
        }

        for(MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)){
            // coins
            new Coin(ui, world, map, ((RectangleMapObject) object).getRectangle(), 100);
        }

        for(MapObject object : map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)){
            // big coins
            new Coin(ui, world, map, ((RectangleMapObject) object).getRectangle(), 1000);
        }

        for(MapObject object : map.getLayers().get(8).getObjects().getByType(PolygonMapObject.class)){
            new Spike(ui, world, map, ((PolygonMapObject) object).getPolygon(), player);
        }
        for(MapObject object : map.getLayers().get(9).getObjects().getByType(RectangleMapObject.class)){
            new Chest(world, map, ((RectangleMapObject) object).getRectangle(), this);
        }
    }

    private String getNextLevel() throws IOException {
        if (!levelWon) {
            return levelName;
        }
        String row;
        BufferedReader levelListReader = new BufferedReader(new FileReader("maps/levels.csv"));
        while ((row = levelListReader.readLine()) != null) {
            if (row.equalsIgnoreCase(levelName)) {
                String row2;
                if ((row2 = levelListReader.readLine()) != null) {
                    return row2;
                }
                else {
                    return null;
                }
            }
        }
        return null;
    }

    void triggerNextLevel(boolean isWin) throws IOException {
        levelOver = true;
        levelWon = isWin;
        backgroundMusic.stop();
        if (!isWin) {
            ui.triggerLose();
        }
        if (isWin) {
            if (getNextLevel() == null) {
                ui.triggerWin();
                Sound sound = Gdx.audio.newSound(Gdx.files.internal("sound/nextLevel.mp3"));
                sound.play();
            }
            else {
                ui.triggerNextLevel();
                Sound sound = Gdx.audio.newSound(Gdx.files.internal("sound/nextLevel.mp3"));
                sound.play();
            }
        }
    }

    void setSpawn(int x, int y) {
        spawnX = x;
        spawnY = y;
    }
}