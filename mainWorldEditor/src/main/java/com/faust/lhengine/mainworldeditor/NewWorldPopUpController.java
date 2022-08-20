package com.faust.lhengine.mainworldeditor;

import com.faust.lhengine.mainworldeditor.enums.MainWorldEditorScenes;
import com.faust.lhengine.mainworldeditor.mediator.ControllerMediator;
import javafx.fxml.FXML;

import java.io.IOException;

/**
 * New World PopUp Controller
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class NewWorldPopUpController  extends AbstractController {


    @FXML
    protected void createNewMainWorld() throws IOException {

        ControllerMediator.getInstance().changeScene(MainWorldEditorController.class,MainWorldEditorScenes.EDITING);
    }
}
