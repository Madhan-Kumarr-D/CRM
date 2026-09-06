package com.users.administration.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import com.users.administration.Model.Field;
import java.util.List;

@FeignClient(name = "backend")
public interface OpenFeignClient {

    @GetMapping("/field/{ModuleApiName}")
    public List<Field> getModuleField(@PathVariable("ModuleApiName") String ModuleApiName);

    @PostMapping("/field/{ModuleApiName}")
    public Field createModuleField(@PathVariable("ModuleApiName") String ModuleApiName, @RequestBody Field Field_data);

    @PutMapping("/field/{ModuleApiName}")
    public Field updateModuleField(@PathVariable("ModuleApiName") String ModuleApiName, @RequestBody Field Field_data);

    @DeleteMapping("/field/{ModuleApiName}/{FieldID}")
    public String deleteModuleField(@PathVariable("ModuleApiName") String ModuleApiName, @PathVariable("FieldID") Long FieldID);
}