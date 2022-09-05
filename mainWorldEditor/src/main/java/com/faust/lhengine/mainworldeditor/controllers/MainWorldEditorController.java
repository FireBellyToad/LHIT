package com.faust.lhengine.mainworldeditor.controllers;

import com.faust.lhengine.game.rooms.RoomModel;
import com.faust.lhengine.game.rooms.RoomPosition;
import com.faust.lhengine.game.rooms.enums.RoomTypeEnum;
import com.faust.lhengine.mainworldeditor.enums.MainWorldEditorScenes;
import com.faust.lhengine.mainworldeditor.mediator.ControllerMediator;
import com.faust.lhengine.utils.Pair;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main World Editor Controller
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class MainWorldEditorController extends AbstractController {

    private final Map<RoomPosition,RoomModel> mainWorldData = new HashMap<>();
    private Pair<Integer,Integer> worldLimit;

    @FXML
    protected ScrollPane roomBoxContainer;

    @FXML
    protected void openModalPopupFromMenu(Event event) throws IOException {
        String modalUserData = (String) ((MenuItem) event.getSource()).getUserData();
        openModalPopup(modalUserData);
    }

    @FXML
    protected void openModalPopupFromNode(Event event) throws IOException {
        String modalUserData = (String) ((Node) event.getSource()).getUserData();
        openModalPopup(modalUserData);
    }

    @FXML
    protected void closeCurrentMainWorld() throws IOException {
        changeScene(MainWorldEditorScenes.MAIN);
    }

    /**
     * Create a new mainWorld given width and height. All rooms will be EMPTY_SPACE
     *
     * @param width
     * @param height
     * @throws IOException
     */
    public void createNewWorld(int width, int height) throws IOException {

        worldLimit = new Pair<>(width,height);
        mainWorldData.clear();

        // add the correct number of room boxes
        for(int x=0; x < width; x++){
            for(int y=0; y < height; y++){
                mainWorldData.put(new RoomPosition(x,y),new RoomModel(new HashMap<>(),RoomTypeEnum.EMPTY_SPACE));
            }
        }

        //Creates a GridPane with a RoomBox in each cell
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        for(Map.Entry<RoomPosition,RoomModel> entry: mainWorldData.entrySet()){
            Node roomBox = FXMLLoader.load(getClass().getResource(MainWorldEditorScenes.ROOM_BOX.getFilename()));
            //mainWorldData as UserData
            roomBox.setUserData(entry);
            gridPane.add(roomBox,entry.getKey().getX(), entry.getKey().getY());
        }

        roomBoxContainer.setContent(gridPane);
        roomBoxContainer.setPannable(true); // it means that the user should be able to pan the viewport by using the mouse.
    }

    /**
     * Sets a new type for roomPosition. Clears all previously set boundaries
     * @param roomPosition
     * @param newType
     */
    public void setNewRoomType(RoomPosition roomPosition, RoomTypeEnum newType) {
        mainWorldData.put(roomPosition, new RoomModel(new HashMap<>(),newType));
    }
}