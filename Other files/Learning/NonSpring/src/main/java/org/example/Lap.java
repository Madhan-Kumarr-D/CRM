package org.example;

public class Lap {
    int age;
    Lap(){
        System.out.println("in lap class");
    }
    Lap(int age){
        this.age = age;
        System.out.println("in lap class param");
    }
    public void mall(){
        System.out.println("in mall");
    }
}
