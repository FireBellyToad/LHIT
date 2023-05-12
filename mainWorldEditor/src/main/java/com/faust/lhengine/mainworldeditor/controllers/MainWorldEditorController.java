package com.faust.lhengine.mainworldeditor.controllers;

import com.esotericsoftware.jsonbeans.Json;
import com.faust.lhengine.game.rooms.RoomModel;
import com.faust.lhengine.game.rooms.RoomPosition;
import com.faust.lhengine.game.rooms.enums.RoomTypeEnum;
import com.faust.lhengine.mainworldeditor.enums.MainWorldEditorScenes;
import com.faust.lhengine.mainworldeditor.model.MainWorldData;
import com.faust.lhengine.mainworldeditor.serialization.MainWorldSerializer;
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

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Main World Editor Controller
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class MainWorldEditorController extends AbstractController {

    private MainWorldData mainWorldData;
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
        mainWorldData.clear();
        changeScene(MainWorldEditorScenes.EDITING);
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
        mainWorldData = new MainWorldData();

        // add the correct number of room boxes
        for(int x=0; x < width; x++){
            for(int y=0; y < height; y++){
                mainWorldData.terrains.put(new RoomPosition(x,y),new RoomModel(new HashMap<>(),RoomTypeEnum.EMPTY_SPACE));
            }
        }

        populateRoomBoxContainer();
    }

    /**
     * Sets a new type for roomPosition. Clears all previously set boundaries
     * @param roomPosition
     * @param newType
     */
    public void setNewRoomType(RoomPosition roomPosition, RoomTypeEnum newType) {
        System.out.println("New room of type " + newType.name() + " at " + roomPosition);
        mainWorldData.terrains.put(roomPosition, new RoomModel(new HashMap<>(),newType));
    }

    private void saveMapToFile(File file) throws FileNotFoundException {

        var writer = new PrintWriter(file);
        writer.println(mainWorldData.toJson());
        writer.close();
    }

    @FXML
    protected void loadMainWorldFromFile() throws IOException {

        //Open file chooser with save
        final var fileChooser = new FileChooser();
        final Stage stage = (Stage) rootVbox.getScene().getWindow();

        //Set extension filter for text files
        var extFilter = new FileChooser.ExtensionFilter("Json file (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        var file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            loadMapFromFile(file);
        }
    }

    /**
     *
     * @param file
     * @throws IOException
     */
    public void loadMapFromFile(File file) throws IOException {
        Json jsonParser = new Json();
        jsonParser.setSerializer(MainWorldData.class, new MainWorldSerializer());
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            mainWorldData = jsonParser.fromJson(MainWorldData.class,reader.lines().collect(Collectors.joining("\n")));

            int width = 0;
            int height =0;

            for(Map.Entry<RoomPosition, RoomModel> t : mainWorldData.terrains.entrySet()){

                width = width < t.getKey().getX() ? t.getKey().getX()  : width;
                height = height < t.getKey().getY() ? t.getKey().getY()  : height;

            }

            worldLimit = new Pair<>(width+1,height+1);

            populateRoomBoxContainer();

        } catch (Exception e) {
            throw e;
        }
    }

    private void populateRoomBoxContainer() throws IOException {

        //Creates a GridPane with a RoomBox in each cell
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        FXMLLoader loader;
        for(Map.Entry<RoomPosition,RoomModel> entry: mainWorldData.terrains.entrySet()){
            loader = new FXMLLoader(getClass().getResource(MainWorldEditorScenes.ROOM_BOX.getFilename()));
            Node roomBox = loader.load();
            RoomBoxController controller = loader.getController();
            controller.setRoomData(entry);
            //Use Hight - y for rendering to respect the bottom-left origin of the mainworld IN GAME
            System.out.println("worldLimit.getSecond()- entry.getKey().getY() : "+ (worldLimit.getSecond()- entry.getKey().getY()));
            gridPane.add(roomBox,entry.getKey().getX(),worldLimit.getSecond()- entry.getKey().getY());
        }



        roomBoxContainer.setContent(gridPane);
        roomBoxContainer.setPannable(true); // it means that the user should be able to pan the viewport by using the mouse.
    }

}