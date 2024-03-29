package com.faust.lhitgame.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.faust.lhitgame.LHITGame;
import com.faust.lhitgame.saves.impl.DesktopSaveFileManager;

import java.util.Arrays;

public class DesktopLauncher {
	private static final int SCALE_FACTOR = 6;

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "LHIT";
		config.resizable = false;
		config.width = LHITGame.GAME_WIDTH * SCALE_FACTOR;
		config.height = LHITGame.GAME_HEIGHT * SCALE_FACTOR;
		//if parameter w is set, go windowed
		config.fullscreen = !Arrays.stream(arg).anyMatch(stringarg -> "w".equals(stringarg) || "windowed".equals(stringarg));
		config.addIcon("icon.png", Files.FileType.Internal);
		new LwjglApplication(new LHITGame(false, new DesktopSaveFileManager()), config);
	}
}
