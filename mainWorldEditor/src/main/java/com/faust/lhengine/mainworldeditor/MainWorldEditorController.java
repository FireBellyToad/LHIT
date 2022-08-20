package com.faust.lhengine.mainworldeditor;

import com.faust.lhengine.game.rooms.RoomModel;
import com.faust.lhengine.game.rooms.enums.RoomTypeEnum;
import com.faust.lhengine.mainworldeditor.enums.MainWorldEditorScenes;
import com.faust.lhengine.utils.Pair;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;

import java.io.IOException;
import java.util.List;

/**
 * Main World Editor Controller
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class MainWorldEditorController extends AbstractController {

    @FXML
    private ComboBox<RoomTypeEnum> terrainTypesCombobox;

    private List<RoomModel> mainWorldData;
    private Pair<Integer,Integer> worldLimit;

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

    @FXML
    protected void populateTerrainTypes(){

        if(terrainTypesCombobox.getItems().isEmpty()){
            System.out.println("Load all terrains");
            terrainTypesCombobox.setItems(FXCollections.observableList(List.of(RoomTypeEnum.values())));
        }
    }

    public void createNewWorld(int widthField, int heightField) {

        worldLimit = new Pair<>(widthField,heightField);
        System.out.println(worldLimit);
    }
}