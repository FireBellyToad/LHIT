package com.faust.lhengine.mainworldeditor.controllers;

import com.faust.lhengine.game.rooms.RoomModel;
import com.faust.lhengine.game.rooms.RoomPosition;
import com.faust.lhengine.game.rooms.enums.RoomTypeEnum;
import com.faust.lhengine.mainworldeditor.enums.MainWorldEditorScenes;
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

    public void createNewWorld(int widthField, int heightField) throws IOException {
        FXMLLoader.load(getClass().getResource(MainWorldEditorScenes.EDITING.getFilename()));

        worldLimit = new Pair<>(widthField,heightField);
        mainWorldData.clear();

        // add the correct number of room boxes
        for(int x=0; x < widthField; x++){
            for(int y=0; y < heightField; y++){
                mainWorldData.put(new RoomPosition(x,y),new RoomModel(new HashMap<>(),RoomTypeEnum.EMPTY_SPACE));
            }
        }

        final GridPane root = new GridPane();
        root.setHgap(5);
        root.setVgap(5);

        for(Map.Entry<RoomPosition,RoomModel> entry: mainWorldData.entrySet()){
            Node roomBox = FXMLLoader.load(getClass().getResource(MainWorldEditorScenes.ROOM_BOX.getFilename()));
            roomBox.setUserData(entry);
            root.add(roomBox,entry.getKey().getX(), entry.getKey().getY());
        }

        roomBoxContainer.setContent(root);
        roomBoxContainer.setPannable(true); // it means that the user should be able to pan the viewport by using the mouse.

        System.out.println(worldLimit);
    }
}