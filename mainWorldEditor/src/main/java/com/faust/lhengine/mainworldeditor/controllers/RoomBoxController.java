package com.faust.lhengine.mainworldeditor.controllers;

import com.faust.lhengine.game.rooms.RoomModel;
import com.faust.lhengine.game.rooms.RoomPosition;
import com.faust.lhengine.game.rooms.enums.RoomTypeEnum;
import com.faust.lhengine.mainworldeditor.mediator.ControllerMediator;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.*;

/**
 * Main World Editor Controller
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class RoomBoxController extends AbstractController implements Initializable {

    private RoomModel roomModel;
    private RoomPosition roomPosition;

    @FXML
    private ComboBox<RoomTypeEnum> terrainTypesCombobox;

    @FXML
    protected void onChangeTerrainType(Event event) {

        if (Objects.isNull(roomPosition)) {
            roomPosition = ((Map.Entry<RoomPosition, RoomModel>) rootVbox.getUserData()).getKey();
        }

        ControllerMediator.getInstance().mainWorldEditorControllerSetNewRoomType(roomPosition, terrainTypesCombobox.getValue());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if (terrainTypesCombobox.getItems().isEmpty()) {
            terrainTypesCombobox.setItems(FXCollections.observableList(List.of(RoomTypeEnum.values())));
        }

        terrainTypesCombobox.getSelectionModel().select(RoomTypeEnum.EMPTY_SPACE);

    }

    public void setRoomData(Map.Entry<RoomPosition, RoomModel> entry) {

        roomModel = entry.getValue();
        roomPosition = entry.getKey();
        terrainTypesCombobox.getSelectionModel().select(roomModel.type);
    }
}
