package com.faust.lhengine.mainworldeditor.controllers;

import com.faust.lhengine.game.rooms.RoomModel;
import com.faust.lhengine.game.rooms.RoomPosition;
import com.faust.lhengine.game.rooms.enums.RoomTypeEnum;
import com.faust.lhengine.mainworldeditor.enums.MainWorldEditorScenes;
import com.faust.lhengine.mainworldeditor.model.MainWorldData;
import com.faust.lhengine.utils.Pair;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Main World Editor Controller
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class MainWorldEditorController extends AbstractController {

    private final MainWorldData mainWorldData = new MainWorldData();
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

    @FXML
    protected void saveCurrentMainWorld() throws FileNotFoundException {

        //Open file chooser with save
        final var fileChooser = new FileChooser();
        final Stage stage = (Stage) rootVbox.getScene().getWindow();

        //Set extension filter for text files
        var extFilter = new FileChooser.ExtensionFilter("Json file (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        var file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            saveMapToFile(file);
        }
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
                mainWorldData.getData().put(new RoomPosition(x,y),new RoomModel(new HashMap<>(),RoomTypeEnum.EMPTY_SPACE));
            }
        }

        //Creates a GridPane with a RoomBox in each cell
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        for(Map.Entry<RoomPosition,RoomModel> entry: mainWorldData.getData().entrySet()){
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
        System.out.println("New room of type " + newType.name() + " at " + roomPosition);
        mainWorldData.getData().put(roomPosition, new RoomModel(new HashMap<>(),newType));
    }

    private void saveMapToFile(File file) throws FileNotFoundException {

        var writer = new PrintWriter(file);
        writer.println(mainWorldData.toJson());
        writer.close();
    }
}