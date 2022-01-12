package com.faust.lhitgame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.faust.lhitgame.LHITGame;
import com.faust.lhitgame.saves.impl.DesktopSaveFileManager;

public class DesktopLauncher {
	private static final int SCALE_FACTOR = 6;

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "LHIT";
		config.resizable = false;
		config.width = LHITGame.GAME_WIDTH * SCALE_FACTOR;
		config.height = LHITGame.GAME_HEIGHT * SCALE_FACTOR;
		//if parameter w is set, go windowed
		config.fullscreen = false;//!Arrays.stream(arg).anyMatch(stringarg -> "w".equals(stringarg) || "windowed".equals(stringarg));
		new LwjglApplication(new LHITGame(new DesktopSaveFileManager()), config);
	}
}
