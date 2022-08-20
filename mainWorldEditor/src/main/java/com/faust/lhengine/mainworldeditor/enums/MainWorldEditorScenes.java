package com.faust.lhengine.mainworldeditor.enums;

/**
 * All application Scenes enum
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public enum MainWorldEditorScenes {

    MAIN("main-view"),
    EDITING("editing-view"),
    NEW_WORLD_POPUP("new-popup-view");

    private final String filename;

    MainWorldEditorScenes(String filename){
        this.filename = filename;
    }

    public String getFilename(){
        return filename + ".fxml";
    }
}
