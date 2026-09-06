package com.example.demo.Service;

import com.example.demo.Model.Module;
import com.example.demo.Repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
public class ModuleService {
    @Autowired
    ModuleRepository module;
    public List<Module> getModules() throws SQLException {
        return module.getModules();
    }

    public List<Map<String, Object>> getModulesByID(long ModuleID) throws SQLException {
        return module.getModulesByID(ModuleID);
    }

    public Module createModule(Module ModulePayload) throws SQLException {
        return module.createModule(ModulePayload);
    }

    public Module deleteModule(Long ModuleID) throws SQLException {
        Module moduleData = module.getModulesByIDAlone(ModuleID);
        if (module == null) throw new IllegalArgumentException("Module not found: " + ModuleID);
        return module.deleteModule(moduleData);
    }

    public Module getModulesByIDAlone(Long moduleID) throws SQLException {

        return module.getModulesByIDAlone(moduleID);
    }
}
