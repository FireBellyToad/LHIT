package faust.lhipgame.hud;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.gameentities.GameEntity;
import faust.lhipgame.gameentities.SpriteEntity;
import faust.lhipgame.instances.impl.PlayerInstance;

import java.util.Objects;

/**
 * Hud class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class Hud {
    private SpriteEntity lifeMeterTexture;
    private final Vector2 meterPosition = new Vector2(5, LHIPGame.GAME_HEIGHT-10);

    public Hud(PlayerInstance player) {
        Objects.requireNonNull(player);
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

        TextureRegion frame = lifeMeterTexture.getFrame(0);
        for (int r = 0; r < player.getDamageDelta(); r++) {
            batch.draw(frame, meterPosition.x+(r*frame.getRegionWidth()), meterPosition.y);
        }


        //Rivedere
    }
}
