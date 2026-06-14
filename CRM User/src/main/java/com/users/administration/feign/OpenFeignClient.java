package com.users.administration.feign;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.*;
import com.users.administration.Model.Field;
import java.util.List;



@EnableFeignClients("backend")
public interface OpenFeignClient {

    @GetMapping("/field/{ModuleApiName}")
    public List<Field> getModuleField(@PathVariable String ModuleApiName);
    @PostMapping("/field/{ModuleApiName}")
    public Field createModuleField(@PathVariable String ModuleApiName,@RequestBody Field Field_data);
    @PutMapping("/field/{ModuleApiName}")
    public Field updateModuleField(@PathVariable String ModuleApiName,@RequestBody Field Field_data);
    @DeleteMapping("/field/{ModuleApiName}/{FieldID}")
    public String deleteModuleField(@PathVariable String ModuleApiName,@PathVariable Long FieldID);
}
