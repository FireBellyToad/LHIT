package faust.lhipgame.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.gameentities.GameEntity;
import faust.lhipgame.gameentities.SpriteEntity;
import faust.lhipgame.instances.LivingInstance;
import faust.lhipgame.instances.impl.PlayerInstance;
import faust.lhipgame.text.manager.TextManager;

import java.util.Objects;

/**
 * Hud class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class Hud {
    private SpriteEntity hudTexture;

    private TextManager textManager;

    private final Vector2 meterPosition = new Vector2(2.5f, LHIPGame.GAME_HEIGHT-10);
    private final Vector2 healthKitCountPosition = new Vector2(LHIPGame.GAME_WIDTH-10, LHIPGame.GAME_HEIGHT-4);
    private final Vector2 morgengabeCountPosition = new Vector2(LHIPGame.GAME_WIDTH-50, LHIPGame.GAME_HEIGHT-4);

    private final ShapeRenderer backgroundBox = new ShapeRenderer();
    private static final Color back = new Color(0x222222ff);

    public Hud(TextManager textManager) {

        this.textManager = textManager;
        this.hudTexture = new SpriteEntity(new Texture("sprites/hud.png")) {
            @Override
            protected int getTextureColumns() {
                return 4;
            }

            @Override
            protected int getTextureRows() {
                return 1;
            }
        };

    }


    public void drawHud(SpriteBatch batch, PlayerInstance player, OrthographicCamera camera){
        Objects.requireNonNull(batch);

        //Black Background
        batch.begin();
        backgroundBox.setColor(back);
        backgroundBox.setProjectionMatrix(camera.combined);
        backgroundBox.begin(ShapeRenderer.ShapeType.Filled);
        backgroundBox.rect(0, LHIPGame.GAME_HEIGHT, LHIPGame.GAME_WIDTH, -12);
        backgroundBox.end();
        batch.end();

        batch.begin();
        // Draw Health meter (red crosses for each hitpoit remaining, hollow ones for each damage point)
        TextureRegion frame;
        for (int r = 0; r < ((LivingInstance) player).getResistance(); r++) {
            frame = hudTexture.getFrame(r < player.getDamageDelta()?
                    HudIconsEnum.LIFE_METER_EMPTY.ordinal() :
                    HudIconsEnum.LIFE_METER_FULL.ordinal() * GameEntity.FRAME_DURATION );
            batch.draw(frame, meterPosition.x+(r*frame.getRegionWidth()), meterPosition.y);
        }

        //Morgengabes found count
        batch.draw(hudTexture.getFrame( HudIconsEnum.MORGENGABE.ordinal() * GameEntity.FRAME_DURATION),
                morgengabeCountPosition.x-10,
                morgengabeCountPosition.y-6);

        textManager.getMainFont().draw(batch,
                String.valueOf(player.getFoundMorgengabe()),
                morgengabeCountPosition.x,
                morgengabeCountPosition.y);


        //Healthkits found count
        batch.draw(hudTexture.getFrame( HudIconsEnum.HEALTH_KIT.ordinal() * GameEntity.FRAME_DURATION),
                healthKitCountPosition.x-10,
                healthKitCountPosition.y-6);

        textManager.getMainFont().draw(batch,
                String.valueOf(player.getAvailableHealthKits()),
                healthKitCountPosition.x,
                healthKitCountPosition.y);

        batch.end();


    }
}
