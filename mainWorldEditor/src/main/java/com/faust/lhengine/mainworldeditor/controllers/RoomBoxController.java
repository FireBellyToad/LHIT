package com.faust.lhengine.mainworldeditor.controllers;

import com.faust.lhengine.game.rooms.RoomModel;
import com.faust.lhengine.game.rooms.enums.RoomTypeEnum;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.util.List;

/**
 * Main World Editor Controller
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class RoomBoxController extends AbstractController{

    RoomModel roomModel;

    @FXML
    private ComboBox<RoomTypeEnum> terrainTypesCombobox;

    @FXML
    protected void populateTerrainTypes(){

        if(terrainTypesCombobox.getItems().isEmpty()){
            System.out.println("Load all terrains");
            terrainTypesCombobox.setItems(FXCollections.observableList(List.of(RoomTypeEnum.values())));
        }
    }

}
