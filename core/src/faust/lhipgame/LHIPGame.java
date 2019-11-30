package faust.lhipgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import faust.lhipgame.gameentities.Player;
import faust.lhipgame.instances.GameInstance;
import faust.lhipgame.instances.PlayerInstance;
import faust.lhipgame.world.WorldManager;

public class LHIPGame extends Game {

	SpriteBatch batch;
	WorldManager worldManager;
	PlayerInstance player;
	OrthographicCamera camera;
	Box2DDebugRenderer box2DDebugRenderer;
	
	@Override
	public void create () {
		Box2D.init();

		camera = new OrthographicCamera();
		camera.setToOrtho(false,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		worldManager = new WorldManager();

		// Creating player and making it available to input processor
		player = new PlayerInstance(new Player());
		Gdx.input.setInputProcessor((InputProcessor) player);

		batch = new SpriteBatch();
		box2DDebugRenderer = new Box2DDebugRenderer();

		worldManager.insertPlayerIntoWorld(player,150,180);
	}

	@Override
	public void render () {
		worldManager.doStep();

		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		batch.setProjectionMatrix(camera.combined);

		batch.begin();
		player.draw(batch);
		batch.end();

		box2DDebugRenderer.render(worldManager.getWorld(), camera.combined.scl(32f));
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		player.dispose();
		worldManager.dispose();
		box2DDebugRenderer.dispose();
	}
}
