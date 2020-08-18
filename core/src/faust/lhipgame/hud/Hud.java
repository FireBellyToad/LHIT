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
    private SpriteEntity lifeMeterTexture;  //Life meters
    private SpriteEntity healkitIconTexture; //Heal Kit icon

    private TextManager textManager;

    private final Vector2 meterPosition = new Vector2(2.5f, LHIPGame.GAME_HEIGHT-10);
    private final Vector2 healthKitCountPosition = new Vector2(LHIPGame.GAME_WIDTH-10, LHIPGame.GAME_HEIGHT-4);

    private final ShapeRenderer backgroundBox = new ShapeRenderer();
    private static final Color back = new Color(0x222222ff);

    public Hud(TextManager textManager) {

        this.textManager = textManager;
        this.lifeMeterTexture = new SpriteEntity(new Texture("sprites/health_sheet.png")) {
            @Override
            protected int getTextureColumns() {
                return 2;
            }

            @Override
            protected int getTextureRows() {
                return 1;
            }
        };
        this.healkitIconTexture = new SpriteEntity(new Texture("sprites/health_kit.png")) {
            @Override
            protected int getTextureColumns() {
                return 1;
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
            frame = lifeMeterTexture.getFrame(r < player.getDamageDelta()? 0 : GameEntity.FRAME_DURATION );
            batch.draw(frame, meterPosition.x+(r*frame.getRegionWidth()), meterPosition.y);
        }

        //TODO Health kit icon
        batch.draw(healkitIconTexture.getTexture(), healthKitCountPosition.x-15, healthKitCountPosition.y-10);
        textManager.getMainFont().draw(batch,
                String.valueOf(player.getAvailableHealthKits()),
                healthKitCountPosition.x,
                healthKitCountPosition.y);
        batch.end();


    }
}
