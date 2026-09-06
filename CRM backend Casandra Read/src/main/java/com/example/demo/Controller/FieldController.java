package com.example.demo.Controller;

import com.example.demo.Model.Field;
import com.example.demo.Service.FieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/v1")
public class FieldController {

    @Autowired
    FieldService field;

    @GetMapping("/field/{ModuleApiName}")
    public List<Field> getModuleField(@PathVariable String ModuleApiName){
        System.out.println("SUCCESS FULLY ENTERED THE CONSOLE MODULE API NAME - "+ModuleApiName);
        return field.getModuleField(ModuleApiName);
    }
//    @PostMapping("/field/{ModuleApiName}")
//    public Map<String,String> createModuleField(@PathVariable String ModuleApiName,@RequestBody Map<String,String> Field_data){
//        return field.createModuleField(ModuleApiName,Field_data);
//    }
//
//    @PutMapping("/field/{ModuleApiName}")
//    public Field updateModuleField(@PathVariable String ModuleApiName,@RequestBody Field Field_data){
//        return field.updateModuleField(ModuleApiName,Field_data);
//    }
//
//    @DeleteMapping("/field/{ModuleApiName}/{FieldID}")
//    public String deleteModuleField(@PathVariable String ModuleApiName,@PathVariable Long FieldID){
//        return field.deleteModuleField(ModuleApiName,FieldID);
//    }

}
