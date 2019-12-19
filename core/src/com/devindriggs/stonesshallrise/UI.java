package com.devindriggs.stonesshallrise;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

class UI {
    Stage uiStage;

    private int score = 0;

    private MainGame game;

    private Label scoreLabel;
    private Image life1, life2, life3;
    private static Label.LabelStyle labelStyle;

    UI(MainGame game) {
        prepareFont();
        this.game = game;
        Viewport viewport = new FitViewport(MainGame.SCREEN_WIDTH, MainGame.SCREEN_HEIGHT, new OrthographicCamera());

        uiStage = new Stage(viewport, game.batch);
        scoreLabel = new Label( Integer.toString(score), labelStyle );
        scoreLabel.setPosition(20, 220);
        uiStage.addActor(scoreLabel);

        Texture heartImage = new Texture("text/heart.png");

        life1 = new Image(heartImage);
        life1.setPosition(260, 220);
        uiStage.addActor(life1);

        life2 = new Image(heartImage);
        life2.setPosition(280, 220);
        uiStage.addActor(life2);

        life3 = new Image(heartImage);
        life3.setPosition(300, 220);
        uiStage.addActor(life3);
    }

    private void prepareFont() {
        // parameters for generating a custom bitmap font
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font/PressStart2P.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameters = new FreeTypeFontGenerator.FreeTypeFontParameter();

        fontParameters.size = 14;
        fontParameters.color = Color.WHITE;
//        fontParameters.borderWidth = 2;
        fontParameters.borderColor = Color.BLACK;
        fontParameters.borderStraight = true;
//        fontParameters.minFilter = Texture.TextureFilter.Linear;
//        fontParameters.magFilter = Texture.TextureFilter.Linear;

        BitmapFont font = fontGenerator.generateFont(fontParameters);

        labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
    }

    void update(float delta) {
    }

    void addScore(int points) {
        score += points;
        scoreLabel.setText(Integer.toString(score));
    }

    void setHealth(int health) {
        switch (health) {
            case 0:
                life1.remove();
            case 1:
                life2.remove();
            case 2:
                life3.remove();
        }
    }

    void triggerWin() {
        Sound sound = Gdx.audio.newSound(Gdx.files.internal("sound/win.mp3"));
        sound.play();
        showMessage( "text/you-win.png", -1 );
        LevelScreen screen = (LevelScreen) game.getScreen();
        screen.backgroundMusic.stop();
    }
    void triggerNextLevel() {
        Sound sound = Gdx.audio.newSound(Gdx.files.internal("sound/nextLevel.mp3"));
        sound.play();
        showMessage( "text/NextLevel.png", -1 );
        LevelScreen screen = (LevelScreen) game.getScreen();
        screen.backgroundMusic.stop();
    }

    void triggerLose() {
        showMessage( "text/you-lose.png", -1 );
    }

    // To show message indefinitely, use -1 for time.
    // time is in seconds, with -1 being infinite
    private void showMessage(String message, float time) {
        BaseActor messageActor = new BaseActor( 160,120, uiStage );
        messageActor.loadTexture( message );
        messageActor.centerAtPosition( 160,120 );
        messageActor.setOpacity( 100 );
        uiStage.addActor(messageActor);
    }
}
