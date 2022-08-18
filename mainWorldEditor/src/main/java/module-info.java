module com.faust.lhengine.mainworldeditor {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.faust.lhengine.mainworldeditor to javafx.fxml;
    exports com.faust.lhengine.mainworldeditor;
}