package org.example.service;

import org.example.model.Item;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class ItemService {
    List<Item> arr = new ArrayList<>();
    {
        arr.add(new Item());
        arr.add(new Item());
        arr.add(new Item());
        arr.add(new Item());
    }

    public List<Item> getItems() {
        return arr;
    }


}
