package com.faust.lhengine.mainworldeditor;

import com.faust.lhengine.game.rooms.enums.RoomTypeEnum;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.util.List;

public class MainWorldEditorController {
    @FXML
    private ComboBox<RoomTypeEnum> terrainTypesCombobox;

    @FXML
    protected void createNewMainWorld() {
        System.out.println("New mainworld");

        terrainTypesCombobox.setItems(FXCollections.observableList(List.of(RoomTypeEnum.values())));
    }
}