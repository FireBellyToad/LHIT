module com.faust.lhengine.mainworldeditor {
    requires javafx.controls;
    requires javafx.fxml;
    requires core;


    opens com.faust.lhengine.mainworldeditor to javafx.fxml;
    exports com.faust.lhengine.mainworldeditor;
}