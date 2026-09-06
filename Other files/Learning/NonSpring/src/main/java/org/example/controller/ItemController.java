package org.example.controller;

import org.example.model.Item;
import org.example.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ItemController {
//    public void setService(ItemService service) {
//        this.service = service;
//    }
//@Autowired
    private ItemService service;
//    public ItemController(){
//    }
    public ItemController(ItemService service){
        this.service = service;
    }

@GetMapping("/")
public List<Item> getItems(){
    return service.getItems();
}
}
