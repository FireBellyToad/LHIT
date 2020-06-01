package faust.lhipgame.hud;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.gameentities.GameEntity;
import faust.lhipgame.gameentities.LivingEntity;
import faust.lhipgame.gameentities.SpriteEntity;
import faust.lhipgame.instances.LivingInstance;
import faust.lhipgame.instances.impl.PlayerInstance;

import java.util.Objects;

/**
 * Hud class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class Hud {
    private SpriteEntity lifeMeterTexture;
    private final Vector2 meterPosition = new Vector2(2.5f, LHIPGame.GAME_HEIGHT-10);

    public Hud() {

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

    }


    public void drawHud(SpriteBatch batch, PlayerInstance player) {
        Objects.requireNonNull(batch);

        // Draw Health meter
        TextureRegion frame;
        for (int r = 0; r < ((LivingInstance) player).getResistance(); r++) {
            frame = lifeMeterTexture.getFrame(r < player.getDamageDelta()? 0 : GameEntity.FRAME_DURATION );
            batch.draw(frame, meterPosition.x+(r*frame.getRegionWidth()), meterPosition.y);
        }

    }
}
