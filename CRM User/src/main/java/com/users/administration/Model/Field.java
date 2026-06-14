package com.users.administration.Model;

public class Field {
    private long FieldID;
    private String FieldApiName;
    private String FieldDisplayName;
    private String FieldDataType;

    @Override
    public String toString() {
        return "Field{" +
                "FieldID=" + FieldID +
                ", FieldApiName='" + FieldApiName + '\'' +
                ", FieldDisplayName='" + FieldDisplayName + '\'' +
                ", FieldDataType='" + FieldDataType + '\'' +
                '}';
    }

    public long getFieldID() {
        return FieldID;
    }

    public Field(long fieldID, String fieldApiName, String fieldDisplayName, String fieldDataType) {
        FieldID = fieldID;
        FieldApiName = fieldApiName;
        FieldDisplayName = fieldDisplayName;
        FieldDataType = fieldDataType;
    }

    public void setFieldID(long fieldID) {
        FieldID = fieldID;
    }

    public String getFieldApiName() {
        return FieldApiName;
    }

    public void setFieldApiName(String fieldApiName) {
        FieldApiName = fieldApiName;
    }

    public String getFieldDisplayName() {
        return FieldDisplayName;
    }

    public void setFieldDisplayName(String fieldDisplayName) {
        FieldDisplayName = fieldDisplayName;
    }

    public String getFieldDataType() {
        return FieldDataType;
    }

    public void setFieldDataType(String fieldDataType) {
        FieldDataType = fieldDataType;
    }
}
