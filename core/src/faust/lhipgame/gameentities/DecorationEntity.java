package faust.lhipgame.gameentities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import faust.lhipgame.gameentities.enums.DecorationsEnum;

import java.util.Arrays;
import java.util.Objects;

public class DecorationEntity extends SpriteEntity {

    public DecorationEntity(DecorationsEnum decorationType) {
        super(new Texture("sprites/decorations_sheet.png"),decorationType.ordinal());
    }

    @Override
    protected void initAnimation(int rowNumber) {
        TextureRegion[] frames = Arrays.copyOfRange(getFramesFromTexture(), rowNumber, getTextureColumns() * (rowNumber+1));
        this.animation = new Animation<TextureRegion>(FRAME_DURATION, frames);
    }

    @Override
    protected int getTextureColumns() {
        return 1;
    }

    @Override
    protected int getTextureRows() {
        return 2;
    }
}
