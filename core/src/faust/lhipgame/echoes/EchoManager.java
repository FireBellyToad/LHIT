package faust.lhipgame.echoes;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import faust.lhipgame.echoes.enums.EchoType;
import faust.lhipgame.gameentities.AnimatedEntity;
import faust.lhipgame.gameentities.LivingEntity;
import faust.lhipgame.gameentities.SpriteEntity;
import faust.lhipgame.instances.GameInstance;
import faust.lhipgame.instances.impl.DecorationInstance;
import faust.lhipgame.world.manager.WorldManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EchoManager {

    private final WorldManager worldManager;
    private final EchoType echoType;
    private final List<GameInstance> echoActors = new ArrayList<>();

    private boolean showEcho = false;

    public EchoManager(final EchoType echoType, WorldManager worldManager) {
        Objects.requireNonNull(echoType);

        this.echoType = echoType;
        this.worldManager = worldManager;
        this.initializeEchoActors();
    }

    /**
     * Initialize echo actors
     */
    private void initializeEchoActors(){
        switch (this.echoType){
            case TREE_MASSACRE:{
                //TODO DO STUFF
                // ADD DISCIPULI AND VICTIMS
                break;
            }
            case DEAD_RIVER:{
                //TODO DO OTHER STUFF
                // ADD DEADBODIES
                break;
            }
        }
    }

    /**
     * Starts echo
     */
    public void startEcho(){
        this.showEcho = true;
    }

    public void dispose(){
        this.echoActors.forEach(actor -> actor.dispose());
    }

    public List<GameInstance> getEchoActors() {
        return echoActors;
    }

    public boolean isEchoShowing() {
        return showEcho;
    }
}
