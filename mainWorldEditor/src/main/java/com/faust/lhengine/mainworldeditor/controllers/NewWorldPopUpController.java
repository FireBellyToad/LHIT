package com.faust.lhengine.mainworldeditor.controllers;

import com.faust.lhengine.game.rooms.enums.RoomTypeEnum;
import com.faust.lhengine.mainworldeditor.enums.MainWorldEditorScenes;
import com.faust.lhengine.mainworldeditor.mediator.ControllerMediator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.List;

/**
 * New World PopUp Controller
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class NewWorldPopUpController  extends AbstractController {

    @FXML
    private TextField widthField;

    @FXML
    private TextField heightField;

    @FXML
    protected void createNewMainWorld() throws IOException {

        final int widthValue = Integer.parseInt(widthField.getText());
        final int heightValue = Integer.parseInt(heightField.getText());

        //FIXME add validation

        //Order is important! First load the view, then create the world so that the UI components are already injected
        ControllerMediator.getInstance().changeScene(MainWorldEditorController.class,MainWorldEditorScenes.EDITING);
        ControllerMediator.getInstance().mainWorldEditorControllerCreateNewWorld(widthValue,heightValue);

        //Close popup
        closeStage();
    }
}
