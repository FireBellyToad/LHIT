package com.faust.lhengine.mainworldeditor;

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

        ControllerMediator.getInstance().mainWorldEditorControllerCreateNewWorld(widthValue,heightValue);
        ControllerMediator.getInstance().changeScene(MainWorldEditorController.class,MainWorldEditorScenes.EDITING);

        //Close popup
        closeStage();
    }
}
