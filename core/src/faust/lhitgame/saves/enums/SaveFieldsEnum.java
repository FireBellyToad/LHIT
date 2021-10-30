package faust.lhitgame.saves.enums;

/**
 * Enum for save fields
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public enum SaveFieldsEnum {

    LANCE("lance"),
    MORGENGABES("morgengabes"),
    ARMOR("armor"),
    DAMAGE("damage"),
    HERBS_AVAILABLE("herbsAvailable"),
    HERBS_FOUND("herbsFound"),
    PLAYER_INFO("playerInfo", new SaveFieldsEnum[]{LANCE,MORGENGABES,ARMOR,DAMAGE,HERBS_AVAILABLE,HERBS_FOUND}),

    X("x"),
    Y("y"),
    CASUAL_NUMBER("casualNumber"),
    SAVED_FLAGS("savedFlags"),
    ROOMS("rooms",new SaveFieldsEnum[]{X,Y,CASUAL_NUMBER,SAVED_FLAGS});

    private String fieldName;
    private SaveFieldsEnum[] subFields;

    SaveFieldsEnum(String fieldName) {
        this(fieldName, null);
    }

    SaveFieldsEnum(String fieldName, SaveFieldsEnum[] subFields) {
        this.fieldName = fieldName;
        this.subFields = subFields;
    }

    public String getFieldName() {
        return fieldName;
    }

    public SaveFieldsEnum[] getSubFields() {
        return subFields;
    }
}
