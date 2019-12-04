package com.devindriggs.stonesshallrise.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.devindriggs.stonesshallrise.MainGame;
import com.badlogic.gdx.Game;

public class DesktopLauncher
{
    public static void main (String[] args)
    {
        // To start a LibGDX program, this method:
        // (1) creates an instance of the game
        // (2) creates a new application with game instance and window settings as argument
        Game myGame = new MainGame();
        LwjglApplication launcher = new LwjglApplication( myGame, "The Tower", 800, 600 );
    }
}