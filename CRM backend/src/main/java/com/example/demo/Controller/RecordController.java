package com.example.demo.Controller;
import java.sql.SQLException;
import java.util.*;

import com.example.demo.Model.Field;
import com.example.demo.Service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
// centrall error handler
// implement DTO
// implement import jakarta.validation.constraints.*;// @NotBlank//@Size//@Pattern//@Email//@Min//@Max
// allow listing implementation for api names
// 13
@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/v1")
public class RecordController {

    @Autowired
RecordService service;

@GetMapping("/records/{ModuleApi}")
    public List<Map<String, Object>> GetRecords(@PathVariable String ModuleApi) throws SQLException {
    return service.GetModuleRecords(ModuleApi);
}

@PostMapping("/records/{ModuleApi}")
    public void CreateRecord(@PathVariable String ModuleApi,@RequestBody Map<String,String> data) throws SQLException {
    service.CreateRecord(ModuleApi,data);
}
@GetMapping("/records/{ModuleApiName}/{RecordID}")
    public Map<String,String> GetRecordByID(@PathVariable String ModuleApiName, @PathVariable String RecordID) throws SQLException {
    return service.GetRecordByID(ModuleApiName,RecordID);
}
@PutMapping("/records/{ModuleApiName}/{RecordID}")
public Map<String,String> UpdateRecord(@PathVariable String ModuleApiName, @PathVariable String RecordID,@RequestBody Map<String,String> Record) throws SQLException {
    return service.UpdateRecord(ModuleApiName,RecordID,Record);
}
}
