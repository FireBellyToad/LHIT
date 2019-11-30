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
	public static final float PIXEL_PER_METER = 32f;

	SpriteBatch batch;
	WorldManager worldManager;
	GameInstance player;
	OrthographicCamera camera;
	Box2DDebugRenderer box2DDebugRenderer;
	
	@Override
	public void create () {
		Box2D.init();

		camera = new OrthographicCamera();
		camera.setToOrtho(false,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		worldManager = new WorldManager();
		player = new PlayerInstance(new Player());
		batch = new SpriteBatch();
		box2DDebugRenderer = new Box2DDebugRenderer();
		Gdx.input.setInputProcessor((InputProcessor) player);

		worldManager.insertIntoWorld(player,150,180);
	}

	@Override
	public void render () {
		worldManager.doStep();

		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		batch.setProjectionMatrix(camera.combined);

		batch.begin();
		player.draw(batch);
		batch.end();

		box2DDebugRenderer.render(worldManager.getWorld(), camera.combined.scl(PIXEL_PER_METER));
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		player.dispose();
		worldManager.dispose();
		box2DDebugRenderer.dispose();
	}
}
