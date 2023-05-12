package com.faust.lhengine.mainworldeditor.mediator;

import com.faust.lhengine.game.gameentities.enums.DirectionEnum;
import com.faust.lhengine.game.rooms.RoomPosition;
import com.faust.lhengine.game.rooms.enums.RoomTypeEnum;
import com.faust.lhengine.mainworldeditor.controllers.AbstractController;
import com.faust.lhengine.mainworldeditor.controllers.MainWorldEditorController;
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
    private static final String SEPARATOR = "|-|";

    private final Map<String, AbstractController> controllersMap = new HashMap<>();

    /**
     * Singleton implementation
     *
     * @return current instance
     */
    public static ControllerMediator getInstance() {
        return INSTANCE;
    }

    /**
     * Register a controller, which then can be used in a mediation
     *
     * @param controller
     */
    public void registerController(final AbstractController controller) {
        registerControllerWithUuid(controller, "");
    }

    /**
     * Register a controller, which then can be used in a mediation
     *
     * @param controller
     */
    public void registerControllerWithUuid(final AbstractController controller, final String uuid) {
        Objects.requireNonNull(controller);
        controllersMap.put(uuid + controller.getClass().getSimpleName(), controller);
    }


    /**
     * Change a scene from a registered controller
     *
     * @param clazz    the class of the registered controller
     * @param newScene the new scene
     * @throws IOException
     */
    public void changeScene(final Class<? extends AbstractController> clazz, final MainWorldEditorScenes newScene) throws IOException {
        if (!controllersMap.containsKey(clazz.getSimpleName())) {
            throw new RuntimeException(clazz.getSimpleName() + " is not registered!");
        }

        controllersMap.get(clazz.getSimpleName()).changeScene(newScene);
    }

    /**
     * create a new world passing height and width to a registered MainWorldEditorController
     *
     * @param widthField
     * @param heightField
     */
    public void mainWorldEditorControllerCreateNewWorld(final int widthField, final int heightField) throws IOException {
        if (!controllersMap.containsKey(MainWorldEditorController.class.getSimpleName())) {
            throw new RuntimeException(MainWorldEditorController.class.getSimpleName() + " is not registered!");
        }

        MainWorldEditorController controller = (MainWorldEditorController) controllersMap.get(MainWorldEditorController.class.getSimpleName());
        controller.createNewWorld(widthField, heightField);

    }

    /**
     *
     * @param roomPosition
     * @param newType
     */
    public void mainWorldEditorControllerSetNewRoomType(final RoomPosition roomPosition, final RoomTypeEnum newType) {
        if (!controllersMap.containsKey(MainWorldEditorController.class.getSimpleName())) {
            throw new RuntimeException(MainWorldEditorController.class.getSimpleName() + " is not registered!");
        }

        MainWorldEditorController controller = (MainWorldEditorController) controllersMap.get(MainWorldEditorController.class.getSimpleName());
        controller.setNewRoomType(roomPosition, newType);
    }

    /**
     *
     * @param directionEnum
     * @param roomPosition
     */
    public void mainWorldEditorControllerStartBoundarySelection(final DirectionEnum directionEnum, final RoomPosition roomPosition) {
        if (!controllersMap.containsKey(MainWorldEditorController.class.getSimpleName())) {
            throw new RuntimeException(MainWorldEditorController.class.getSimpleName() + " is not registered!");
        }

        MainWorldEditorController controller = (MainWorldEditorController) controllersMap.get(MainWorldEditorController.class.getSimpleName());
        controller.startBoundarySelection(directionEnum, roomPosition);
    }

    /**
     *
     * @param roomPosition
     */
    public void mainWorldEditorControllerSelectBoundary(RoomPosition roomPosition) {
        if (!controllersMap.containsKey(MainWorldEditorController.class.getSimpleName())) {
            throw new RuntimeException(MainWorldEditorController.class.getSimpleName() + " is not registered!");
        }

        MainWorldEditorController controller = (MainWorldEditorController) controllersMap.get(MainWorldEditorController.class.getSimpleName());
        controller.selectBoundary(roomPosition);
    }
}
