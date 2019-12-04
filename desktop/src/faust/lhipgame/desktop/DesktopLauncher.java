package faust.lhipgame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import faust.lhipgame.LHIPGame;

import java.util.Objects;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "LHIP";
		new LwjglApplication(new LHIPGame(), config);
	}
}
