package com.example.demo.Service;

import com.example.demo.Controller.RecordController;
import com.example.demo.Model.Field;
import com.example.demo.Repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
public class RecordService {
    @Autowired
    RecordRepository control;

    public List<Map<String, Object>> GetModuleRecords(String ModuleApiName) throws SQLException {
        return control.getModuleRecords(ModuleApiName);
    }

    public void CreateRecord(String ModuleApi,Map<String, String> data) throws SQLException {
        control.CreateRecord(ModuleApi,data);
    }

    public Map<String,String> GetRecordByID(String moduleApiName, String recordID) throws SQLException {
        return control.GetRecordByID(moduleApiName,recordID);
    }


    public Map<String,String> UpdateRecord(String moduleApiName, String recordID, Map<String,String> record) throws SQLException {
        return control.UpdateRecord(moduleApiName,recordID,record);
    }
}
