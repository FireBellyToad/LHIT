package com.faust.lhengine.mainworldeditor;

import com.faust.lhengine.mainworldeditor.enums.MainWorldEditorScenes;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main World Editor Application
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class MainWorldEditorApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        //Opens up main view
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("controllers/"+MainWorldEditorScenes.EDITING.getFilename())));
        stage.setTitle("LH-Engine Main World Editor");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}