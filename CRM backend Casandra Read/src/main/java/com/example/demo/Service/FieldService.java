package com.example.demo.Service;

import com.example.demo.Model.Field;
import com.example.demo.Repository.FieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FieldService {
    @Autowired
    FieldRepository field;
    public List<Field> getModuleField(String ModuleApiName) {
        System.out.println("SUCCESS FULLY ENTERED THE CONSOLE MODULE API NAME [ inside service layer ] - "+ModuleApiName);
        return field.getModuleField(ModuleApiName);
    }

    public Map<String,String> createModuleField(String moduleApiName, Map<String,String> fieldData) {
        if (!moduleApiName.matches("^[a-zA-Z0-9_]{1,50}$")) {
            throw new IllegalArgumentException("Invalid module API name for security.");
        }
        // Validate new column name: Must be safe for column names
        if (!fieldData.get("fieldApiName").matches("^[a-zA-Z0-9_]{1,20}$")) {
            throw new IllegalArgumentException("Invalid field API name.");
        }
        return field.createModuleField(moduleApiName,fieldData);
    }

    public Field updateModuleField(String moduleApiName, Field fieldData) {
    return field.updateModuleField(moduleApiName,fieldData);
    }

    public String deleteModuleField(String moduleApiName, Long fieldID) {
        return field.deleteModuleField(moduleApiName,fieldID);
    }
}
