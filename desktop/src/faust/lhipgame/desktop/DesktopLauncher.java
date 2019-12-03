package faust.lhipgame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import faust.lhipgame.LHIPGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "LHIP";
		config.height = 144;
		config.width = 160;
		new LwjglApplication(new LHIPGame(), config);
	}
}
