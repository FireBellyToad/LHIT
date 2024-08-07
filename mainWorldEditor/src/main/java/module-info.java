module com.faust.lhengine.mainworldeditor.controllers {
    requires javafx.controls;
    requires javafx.fxml;
    requires core;
    requires jsonbeans;


    opens com.faust.lhengine.mainworldeditor.controllers to javafx.fxml;
    exports com.faust.lhengine.mainworldeditor.controllers;
    exports com.faust.lhengine.mainworldeditor;
    opens com.faust.lhengine.mainworldeditor to javafx.fxml;

    //jsonbeans opens
    opens com.faust.lhengine.mainworldeditor.model to jsonbeans;
}