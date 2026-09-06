package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import com.example.demo.Controller.HomeController;

    @SpringBootApplication
    public class Fert {

        public static void main(String[] args) {

            ApplicationContext app = SpringApplication.run(com.example.demo.Fert.class, args);
            HomeController obj = app.getBean(HomeController.class);
        }

    }
