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
import faust.lhipgame.gameentities.enums.GameBehavior;
import faust.lhipgame.instances.AnimatedInstance;
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

    private final Vector2 meterPosition = new Vector2(2.5f, LHIPGame.GAME_HEIGHT - 10);
    private final Vector2 healthKitCountPosition = new Vector2(LHIPGame.GAME_WIDTH - 10, LHIPGame.GAME_HEIGHT - 4);
    private final Vector2 holyLancePiecesPosition = new Vector2(LHIPGame.GAME_WIDTH - 30, LHIPGame.GAME_HEIGHT - 4);
    private final Vector2 morgengabeCountPosition = new Vector2(LHIPGame.GAME_WIDTH - 50, LHIPGame.GAME_HEIGHT - 4);

    private final ShapeRenderer backgroundBox = new ShapeRenderer();
    private static final Color back = new Color(0x222222ff);

    //Healing timer bar
    private final ShapeRenderer cornerBox = new ShapeRenderer();
    private static final Color corner = new Color(0xffffffff);

    public Hud(TextManager textManager) {

        this.textManager = textManager;
        this.hudTexture = new SpriteEntity(new Texture("sprites/hud.png")) {
            @Override
            protected int getTextureColumns() {
                return HudIconsEnum.values().length;
            }

            @Override
            protected int getTextureRows() {
                return 1;
            }
        };

    }


    public void drawHud(SpriteBatch batch, PlayerInstance player, OrthographicCamera camera) {
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
        for (int r = 0; r < player.getResistance(); r++) {
            frame = hudTexture.getFrame(r < player.getDamageDelta() ?
                    HudIconsEnum.LIFE_METER_FULL.ordinal() :
                    HudIconsEnum.LIFE_METER_EMPTY.ordinal() * GameEntity.FRAME_DURATION);
            batch.draw(frame, meterPosition.x + (r * frame.getRegionWidth()), meterPosition.y);
        }

        //Morgengabes found count
        batch.draw(hudTexture.getFrame(HudIconsEnum.MORGENGABE.ordinal() * GameEntity.FRAME_DURATION),
                morgengabeCountPosition.x - 10,
                morgengabeCountPosition.y - 6);

        textManager.getMainFont().draw(batch,
                String.valueOf(player.getFoundMorgengabes()),
                morgengabeCountPosition.x,
                morgengabeCountPosition.y);

        //Holy lance pieces found count
        batch.draw(hudTexture.getFrame(HudIconsEnum.LANCE.ordinal() * GameEntity.FRAME_DURATION),
                holyLancePiecesPosition.x - 10,
                holyLancePiecesPosition.y - 6);

        textManager.getMainFont().draw(batch,
                String.valueOf(player.getHolyLancePieces()),
                holyLancePiecesPosition.x,
                holyLancePiecesPosition.y);


        //Healthkits found count
        batch.draw(hudTexture.getFrame(HudIconsEnum.HEALTH_KIT.ordinal() * GameEntity.FRAME_DURATION),
                healthKitCountPosition.x - 10,
                healthKitCountPosition.y - 6);

        textManager.getMainFont().draw(batch,
                String.valueOf(player.getAvailableHealthKits()),
                healthKitCountPosition.x,
                healthKitCountPosition.y);

        batch.end();

        //Draw Healing timer bar if player is curing himself
        if (GameBehavior.KNEE.equals(player.getCurrentBehavior()) && Objects.nonNull(player.getIsHealingTimer())) {
            Vector2 playerPosition = player.getBody().getPosition();
            //Black Corner
            batch.begin();
            backgroundBox.setColor(back);
            backgroundBox.setProjectionMatrix(camera.combined);
            backgroundBox.begin(ShapeRenderer.ShapeType.Filled);
            backgroundBox.rect(playerPosition.x, playerPosition.y+17, 10,  5);
            backgroundBox.end();

            //White bar
            long deltaTimer = (player.getIsHealingTimer().getExecuteTimeMillis() - (System.nanoTime() / 1000000) )/1000;

            cornerBox.setColor(corner);
            cornerBox.setProjectionMatrix(camera.combined);
            cornerBox.begin(ShapeRenderer.ShapeType.Filled);
            cornerBox.rect(playerPosition.x + 1, playerPosition.y + 18,2 + (deltaTimer * 2), 3);
            cornerBox.end();
            batch.end();
        }


    }
}
