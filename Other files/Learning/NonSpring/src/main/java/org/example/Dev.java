package org.example;

public class Dev {
    private Lap lap;

    public Lap getLap() {
        return lap;
    }

    public void setLap(Lap laptop) {
        this.lap = lap;
    }

    Dev(){
        System.out.println("In Dev class");
    }
    Dev(Lap lap){
        this.lap = lap;
    }
    public void project(){
        System.out.println("building the project");
    }
}
