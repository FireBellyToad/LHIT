package faust.lhitgame.cutscenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
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
import faust.lhitgame.saves.enums.SaveFieldsEnum;
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
    private final Sound nextSound;

    public CutsceneManager(CutsceneEnum cutsceneEnum, AssetManager assetManager, TextLocalizer textLocalizer, final OrthographicCamera camera, SaveFileManager saveFileManager) {
        this.textLocalizer = textLocalizer;
        this.cutsceneKey = cutsceneEnum.getKey();
        this.assetManager = assetManager;
        this.saveFileManager = saveFileManager;
        this.camera = camera;

        lastStep = cutsceneEnum.getStepsNumber();

        assetManager.load("sounds/SFX_UIGeneric13.ogg", Sound.class);
        assetManager.finishLoading();
        nextSound = assetManager.get("sounds/SFX_UIGeneric13.ogg");

        longTextHandler = new LongTextHandler(textLocalizer, cutsceneEnum.getKey());

    }

    /**
     * Init cutscene params and actors
     */
    public void initCutscene() {
        longTextHandler.loadFonts(assetManager);
        textLocalizer.loadTextFromLanguage();

        try {
            tiledScene = new TmxMapLoader().load("cutscenes/" + cutsceneKey + "_1.tmx");
            tiledSceneRenderer = new OrthogonalTiledMapRenderer(tiledScene);
            // Set camera for rendering
            tiledSceneRenderer.setView(camera);
            populateActors();
        } catch (Exception e) {
            Gdx.app.log("DEBUG", "Problem while loading cutscene, using blank");
        }
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
        if (Objects.nonNull(tiledSceneRenderer))
            tiledSceneRenderer.render();
        batch.end();

        actors.forEach(actor -> actor.draw(batch, stateTime));

        batch.begin();
        longTextHandler.drawCurrentText(batch);
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
        nextSound.play();
        longTextHandler.goToNextStep();

        if (isFinished()) {
            return;
        }

        tiledSceneRenderer = null;

        // If not finished, load next scene and actors
        try {
            tiledScene = new TmxMapLoader().load("cutscenes/" + cutsceneKey + "_" + (longTextHandler.getCurrentStep() + 1) + ".tmx");
            tiledSceneRenderer = new OrthogonalTiledMapRenderer(tiledScene);
            // Set camera for rendering
            tiledSceneRenderer.setView(camera);
            populateActors();

        } catch (Exception e) {
            Gdx.app.log("DEBUG", "Problem while loading cutscene" +"cutscenes/" + cutsceneKey + "_" + (longTextHandler.getCurrentStep() + 1) + ".tmx, using blank");
            if (!actors.isEmpty()) {
                actors.clear();
            }
        }
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

        //Player special params
        Map<String, Object> mapFromSaveFile = saveFileManager.loadRawValues();
        boolean playerHasArmor = Objects.nonNull(mapFromSaveFile) && (boolean) mapFromSaveFile.get(SaveFieldsEnum.ARMOR.getFieldName());
        boolean playerHasLance = Objects.nonNull(mapFromSaveFile) && (int) mapFromSaveFile.get(SaveFieldsEnum.LANCE.getFieldName()) > 1;

        //extract mapObject properties and create simpleActor
        actorsMapObjects.forEach(obj -> {
            GameEntity entity = null;
            GameBehavior behavior = null;
            DirectionEnum direction = null;
            Set<SimpleActorParametersEnum> params = new HashSet<>();

            if (obj.getProperties().containsKey("mustFlicker") && (boolean) obj.getProperties().get("mustFlicker")) {
                params.add(SimpleActorParametersEnum.MUST_FLICKER);
            }

            if (obj.getName().equals(PlayerEntity.class.getSimpleName())) {
                params.add(SimpleActorParametersEnum.IS_SHADED);
                if (playerHasArmor) {
                    params.add(SimpleActorParametersEnum.PLAYER_HAS_ARMOR);
                }
                if (playerHasLance) {
                    params.add(SimpleActorParametersEnum.PLAYER_HAS_LANCE);

                }
                entity = new PlayerEntity(assetManager);
                behavior = GameBehavior.valueOf((String) obj.getProperties().get("behavior"));
                if (obj.getProperties().containsKey("direction")) {
                    direction =  DirectionEnum.valueOf((String) obj.getProperties().get("direction"));
                }else{
                    direction = DirectionEnum.UNUSED;
                }
            } else if (obj.getName().equals(HiveEntity.class.getSimpleName())) {
                entity = new HiveEntity(assetManager);
                behavior = GameBehavior.valueOf((String) obj.getProperties().get("behavior"));
                direction = DirectionEnum.UNUSED;
            } else if (obj.getName().equals(PortalEntity.class.getSimpleName())) {
                entity = new PortalEntity(assetManager);
                behavior = GameBehavior.IDLE;
                direction = DirectionEnum.UNUSED;
            } else if (obj.getName().equals(DecorationEntity.class.getSimpleName())) {
                DecorationsEnum decoType = DecorationsEnum.valueOf((String) obj.getProperties().get("type"));
                Objects.requireNonNull(decoType);
                entity = new DecorationEntity(decoType, assetManager);
            } else if (obj.getName().equals(POIEntity.class.getSimpleName())) {
                POIEnum poiType = POIEnum.valueOf((String) obj.getProperties().get("type"));
                Objects.requireNonNull(poiType);
                entity = new POIEntity(poiType, assetManager);
            } else if (obj.getName().equals(TutorialEntity.class.getSimpleName())) {
                entity = new TutorialEntity(assetManager);
                behavior = GameBehavior.valueOf((String) obj.getProperties().get("behavior"));
                direction = DirectionEnum.valueOf((String) obj.getProperties().get("direction"));
                if (Objects.isNull(direction)) {
                    direction = DirectionEnum.UNUSED;
                }
            }

            Objects.requireNonNull(entity);

            actors.add(new SimpleActor(entity, behavior, direction,
                    (float) obj.getProperties().get("x"),
                    (float) obj.getProperties().get("y"), params));
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
