package com.faust.lhengine.mainworldeditor.controllers;

import com.faust.lhengine.mainworldeditor.enums.MainWorldEditorScenes;
import com.faust.lhengine.mainworldeditor.mediator.ControllerMediator;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;

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

        //Order is important! First change (and load) the view, then create the world so that the UI components are already injected
        ControllerMediator.getInstance().changeScene(MainWorldEditorController.class,MainWorldEditorScenes.EDITING);
        ControllerMediator.getInstance().mainWorldEditorControllerCreateNewWorld(widthValue,heightValue);

        //Close popup
        closeStage();
    }
}
