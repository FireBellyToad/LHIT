package com.faust.lhengine.mainworldeditor.mediator;

import com.faust.lhengine.mainworldeditor.AbstractController;
import com.faust.lhengine.mainworldeditor.MainWorldEditorController;
import com.faust.lhengine.mainworldeditor.enums.MainWorldEditorScenes;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;


/**
 * ControllerMediator  for intracontrollers comunication
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class ControllerMediator {

    private static final ControllerMediator INSTANCE = new ControllerMediator();

    private Map<String,AbstractController> controllersMap = new HashMap<>();

    public void registerController(AbstractController controller) {
        Objects.requireNonNull(controller);
        controllersMap.put(controller.getClass().getSimpleName(),controller);
    }

    public void changeScene(Class<? extends AbstractController> clazz, MainWorldEditorScenes newScene) throws IOException {
        if(!controllersMap.containsKey(clazz.getSimpleName())){
            throw new RuntimeException(clazz.getSimpleName() + " is not registered!");
        }

        controllersMap.get(MainWorldEditorController.class.getSimpleName()).changeScene(newScene);
    }

    public static ControllerMediator getInstance(){
        return INSTANCE;
    }
}
