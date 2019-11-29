package faust.lhipgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import faust.lhipgame.gameentities.Player;
import faust.lhipgame.instances.GameInstance;

public class LHIPGame extends Game {
	SpriteBatch batch;
	World world;
	GameInstance player;
	
	@Override
	public void create () {
		Box2D.init();
		batch = new SpriteBatch();
		player = new GameInstance(new Player());
		world = new World(new Vector2(0,0),true);
		world.createBody(player.getBodyDef());
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		player.draw(batch);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		player.dispose();
	}
}
