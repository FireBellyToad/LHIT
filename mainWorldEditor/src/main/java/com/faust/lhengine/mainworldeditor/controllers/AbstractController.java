package com.faust.lhengine.mainworldeditor.controllers;

import com.faust.lhengine.mainworldeditor.enums.MainWorldEditorScenes;
import com.faust.lhengine.mainworldeditor.mediator.ControllerMediator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Abstract Controller
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class AbstractController {

    @FXML
    protected Parent rootVbox;

    public AbstractController() {
        ControllerMediator.getInstance().registerController(this);
    }

    /**
     * Change current Scene
     *
     * @param newScreen MainWorldEditorScenes value of the new screen
     * @throws IOException
     */
    public void changeScene (MainWorldEditorScenes newScreen) throws IOException {
        Objects.requireNonNull(newScreen);

        final Parent newSceneRoot = FXMLLoader.load(getClass().getResource(newScreen.getFilename()));

        final Stage stage = (Stage) rootVbox.getScene().getWindow();
        stage.setScene(new Scene(newSceneRoot));
        stage.show();
    }

    /**
     * Opens a modal popup
     *
     * @param modalUserData JavaFX node userData, which must contain a valid MainWorldEditorScenes enum as string
     * @throws IOException
     */
    protected void openModalPopup(String modalUserData) throws IOException {
        Objects.requireNonNull(modalUserData);

        final String filename = MainWorldEditorScenes.valueOf(modalUserData).getFilename();

        final Stage openedModal = new Stage();
        openedModal.initModality(Modality.APPLICATION_MODAL);
        openedModal.initOwner(rootVbox.getScene().getWindow());

        final Scene dialogScene = new Scene(FXMLLoader.load(getClass().getResource(filename)));
        openedModal.setScene(dialogScene);
        openedModal.show();

    }


    /**
     * Closes stage
     */
    @FXML
    protected void closeStage(){
        final Stage stage = (Stage) rootVbox.getScene().getWindow();
        stage.close();
    }
}
