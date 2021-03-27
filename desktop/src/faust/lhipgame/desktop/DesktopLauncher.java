package faust.lhipgame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import faust.lhipgame.LHIPGame;

import java.util.Objects;

public class DesktopLauncher {
	private static final int SCALE_FACTOR = 5;

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "LHIP";
		config.resizable = false;
		config.width = LHIPGame.GAME_WIDTH * SCALE_FACTOR;
		config.height = LHIPGame.GAME_HEIGHT * SCALE_FACTOR;
		new LwjglApplication(new LHIPGame(), config);
	}
}
