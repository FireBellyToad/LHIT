package faust.lhitgame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import faust.lhitgame.LHITGame;

public class DesktopLauncher {
	private static final int SCALE_FACTOR = 4;

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "LHIT";
		config.resizable = false;
		config.width = LHITGame.GAME_WIDTH * SCALE_FACTOR;
		config.height = LHITGame.GAME_HEIGHT * SCALE_FACTOR;
		new LwjglApplication(new LHITGame(), config);
	}
}
