package faust.lhitgame.cutscenes;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import faust.lhitgame.game.gameentities.GameEntity;
import faust.lhitgame.game.gameentities.enums.DecorationsEnum;
import faust.lhitgame.game.gameentities.enums.DirectionEnum;
import faust.lhitgame.game.gameentities.enums.GameBehavior;
import faust.lhitgame.game.gameentities.enums.POIEnum;
import faust.lhitgame.game.gameentities.impl.*;
import faust.lhitgame.game.rooms.enums.MapLayersEnum;
import faust.lhitgame.menu.LongTextHandler;
import faust.lhitgame.saves.SaveFileManager;
import faust.lhitgame.enums.cutscenes.CutsceneEnum;
import faust.lhitgame.utils.TextLocalizer;

import java.util.*;

/**
 * Class for handling a cutscene
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class CutsceneManager implements InputProcessor {

    private final TextLocalizer textLocalizer;
    private final LongTextHandler longTextHandler;
    private final int lastStep;
    private final String cutsceneKey;
    private final AssetManager assetManager;
    private final SaveFileManager saveFileManager;

    private TiledMap tiledScene;
    private OrthogonalTiledMapRenderer tiledSceneRenderer;
    private final List<SimpleActor> actors = new ArrayList<>();
    private final OrthographicCamera camera;

    public CutsceneManager(CutsceneEnum cutsceneEnum, AssetManager assetManager, TextLocalizer textLocalizer, final OrthographicCamera camera, SaveFileManager saveFileManager) {
        this.textLocalizer = textLocalizer;
        this.cutsceneKey = cutsceneEnum.getKey();
        this.assetManager = assetManager;
        this.saveFileManager = saveFileManager;
        this.camera = camera;

        lastStep = cutsceneEnum.getStepsNumber();

        longTextHandler = new LongTextHandler(textLocalizer, cutsceneEnum.getKey());

    }

    /**
     * Init cutscene params and actors
     */
    public void initCutscene() {
        longTextHandler.loadFonts(assetManager);
        textLocalizer.loadTextFromLanguage();

        tiledScene = new TmxMapLoader().load("cutscenes/" + cutsceneKey + "_1.tmx");
        tiledSceneRenderer = new OrthogonalTiledMapRenderer(tiledScene);

        // Set camera for rendering
        tiledSceneRenderer.setView(camera);
        populateActors();
    }

    /**
     * @param batch
     * @param camera
     */
    public void draw(SpriteBatch batch, float stateTime, OrthographicCamera camera) {
        Objects.requireNonNull(batch);
        Objects.requireNonNull(camera);
        //Render map and actors
        batch.begin();
        tiledSceneRenderer.render();
        batch.end();

        actors.forEach(actor -> actor.draw(batch, stateTime));

        batch.begin();
        longTextHandler.drawCurrentintro(batch, camera);
        batch.end();

    }

    /**
     * @return true if cutscene is ended
     */
    public boolean isFinished() {
        return longTextHandler.getCurrentStep() >= lastStep;
    }

    /**
     * Advances the cutscene
     */
    private void advanceCutscene() {

        longTextHandler.goToNextStep();

        if (isFinished()) {
            return;
        }

        // If not finished, load next scene and actors
        tiledScene = new TmxMapLoader().load("cutscenes/" + cutsceneKey + "_" + (longTextHandler.getCurrentStep() + 1) + ".tmx");
        tiledSceneRenderer = new OrthogonalTiledMapRenderer(tiledScene);

        // Set camera for rendering
        tiledSceneRenderer.setView(camera);
        populateActors();
    }

    /**
     * populate actors of the cutscene
     */
    private void populateActors() {
        MapObjects actorsMapObjects = tiledScene.getLayers().get(MapLayersEnum.OBJECT_LAYER.getLayerName()).getObjects();

        // Clear all and create new actors
        if (!actors.isEmpty()) {
            actors.clear();
        }

        Map<String, Object> mapFromSaveFile = saveFileManager.loadRawValues();
        boolean playerHasArmor = Objects.nonNull(mapFromSaveFile) && (boolean) mapFromSaveFile.get("armor");
        boolean playerHasLance = Objects.nonNull(mapFromSaveFile) && (int) mapFromSaveFile.get("lance") > 1;

        //extract mapObject properties and create simpleActor
        actorsMapObjects.forEach(obj -> {
            GameEntity entity = null;
            GameBehavior behavior = null;
            DirectionEnum direction = null;
            boolean isShaded = false;
            List<SimpleActorParametersEnum> params = new ArrayList<>();

            if (obj.getName().equals(PlayerEntity.class.getSimpleName())) {
                isShaded = true;
                if(playerHasArmor){
                    params.add(SimpleActorParametersEnum.PLAYER_HAS_ARMOR);
                }
                if(playerHasLance){
                    params.add(SimpleActorParametersEnum.PLAYER_HAS_LANCE);

                }
                entity = new PlayerEntity(assetManager);
                behavior = GameBehavior.getFromString((String) obj.getProperties().get("behavior"));
                direction = DirectionEnum.getFromString((String) obj.getProperties().get("direction"));
                if (Objects.isNull(direction)) {
                    direction = DirectionEnum.UNUSED;
                }
            } else if (obj.getName().equals(HiveEntity.class.getSimpleName())) {
                entity = new HiveEntity(assetManager);
                behavior = GameBehavior.getFromString((String) obj.getProperties().get("behavior"));
                direction = DirectionEnum.UNUSED;
            } else if (obj.getName().equals(PortalEntity.class.getSimpleName())) {
                entity = new PortalEntity(assetManager);
                behavior = GameBehavior.IDLE;
                direction = DirectionEnum.UNUSED;
            } else if (obj.getName().equals(DecorationEntity.class.getSimpleName())) {
                DecorationsEnum decoType = DecorationsEnum.getFromString((String) obj.getProperties().get("type"));
                Objects.requireNonNull(decoType);
                entity = new DecorationEntity(decoType,assetManager);
            } else if (obj.getName().equals(POIEntity.class.getSimpleName())) {
                POIEnum poiType = POIEnum.getFromString((String) obj.getProperties().get("type"));
                Objects.requireNonNull(poiType);
                entity = new POIEntity(poiType,assetManager);
            } else if (obj.getName().equals(TutorialEntity.class.getSimpleName())) {
                entity = new TutorialEntity(assetManager);
                behavior = GameBehavior.getFromString((String) obj.getProperties().get("behavior"));
                direction = DirectionEnum.getFromString((String) obj.getProperties().get("direction"));
                if (Objects.isNull(direction)) {
                    direction = DirectionEnum.UNUSED;
                }
            }


            Objects.requireNonNull(entity);

            actors.add(new SimpleActor(entity, behavior, direction,
                    (float) obj.getProperties().get("x"),
                    (float) obj.getProperties().get("y"),
                    isShaded, params));
        });
    }


    @Override
    public boolean keyDown(int keycode) {

        switch (keycode) {
            case Input.Keys.X:
            case Input.Keys.K:
            case Input.Keys.ENTER: {
                advanceCutscene();
                break;
            }
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

}
