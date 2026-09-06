package com.madhan.demoApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class newFile {
    @Autowired
    class1 obj;


    public newFile(class1 obj){
        this.obj = obj;
    }


    @Autowired
    public void newFiles(class1 obj){
        this.obj = obj;
    }

    public void role(){
        obj.grand();
        System.out.println("helloman");
    }
    JSONParser parser = new JSONParser();
    parser.parse()
    
}
