package com.devindriggs.stonesshallrise;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.io.IOException;

public class MenuScreen implements Screen {

    private MainGame game;

    private Stage menuStage;

    private Image background;
    private Image title;
    private Image controlLink;
    private Image start;
    private Image quit;

    private Viewport viewport;

    MenuScreen(MainGame game) {
        this.game = game;
        viewport = new FitViewport(720, 540, new OrthographicCamera());
        menuStage = new Stage(viewport, game.batch);


        Texture temp = new Texture("backgrounds/cliff_1.jpg");
        background = new Image(temp);
        background.setPosition(0,0);
        menuStage.addActor(background);

        temp = new Texture("text/title.png");
        title = new Image(temp);
        title.setPosition(25,450);
        menuStage.addActor(title);

        temp = new Texture("text/start.png");
        start = new Image(temp);
        start.setPosition(50,50);
        menuStage.addActor(start);

        temp = new Texture("text/controls.png");
        controlLink = new Image(temp);
        controlLink.setPosition(50,150);
        menuStage.addActor(controlLink);

        temp = new Texture("text/quit.png");
        quit = new Image(temp);
        quit.setPosition(410,0);
        menuStage.addActor(quit);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            try {
                game.setScreen( new LevelScreen(game, "intro") );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        game.batch.setProjectionMatrix(menuStage.getCamera().combined);
        menuStage.draw();
    }

    @Override
    public void resize(int width, int height) {

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
}
