package com.example.demo.Controller;

import com.example.demo.Model.Module;
import com.example.demo.Service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/v1")
public class ModuleController {

    @Autowired
    ModuleService module;
    @GetMapping("/modules")
    public List<Module> getModules() throws SQLException {
        return module.getModules();
    }
    @GetMapping("/modules/{ModuleID}")
    public List<Map<String, Object>> getModuleByID(@PathVariable Long ModuleID) throws SQLException {
        return module.getModulesByID(ModuleID);
    }
    @GetMapping("/module/{ModuleID}")
    public Module getModuleByIDAlone(@PathVariable Long ModuleID) throws SQLException {
        return module.getModulesByIDAlone(ModuleID);
    }

    @PostMapping("/modules")
    public Module CreateModule(@RequestBody Module ModulePayload) throws SQLException {
        System.out.println(ModulePayload);
        return module.createModule(ModulePayload);
    }

    @DeleteMapping("/modules/{ModuleID}")
    public Module deleteModule(@PathVariable Long ModuleID) throws SQLException {

        return module.deleteModule(getModuleByIDAlone(ModuleID));
    }

}
