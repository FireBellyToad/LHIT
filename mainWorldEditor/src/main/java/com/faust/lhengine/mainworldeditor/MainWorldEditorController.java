package com.faust.lhengine.mainworldeditor;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class MainWorldEditorController {
    @FXML
    private ComboBox<String> terrainTypesCombobox;

    @FXML
    protected void createNewMainWorld() {
        System.out.println("New mainworld");

    }
}