package org.example.model;

import org.springframework.stereotype.Component;

@Component
public class Item {
    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public long getItem_count() {
        return item_count;
    }

    public void setItem_count(long item_count) {
        this.item_count = item_count;
    }

    private int item_id;
    private String item_name;
    private long item_count;
    public Item (){
        this.item_id = 102;
        this.item_name = "item name not defined";
        this.item_count = 0;
    }
    public Item (int item_id,String item_name,long item_count){
        this.item_id = item_id;
        this.item_name = item_name;
        this.item_count = item_count;
    }
    public void Print(Item obj){
        System.out.println("item ID : "+obj.item_id);
        System.out.println("item name : "+obj.item_name);
        System.out.println("item count : "+obj.item_count);
    }
    
}
