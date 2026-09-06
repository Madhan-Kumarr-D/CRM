package org.example;

import org.example.model.Item;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@ImportResource("classpath:spring.xml")
public class App 
{
    public static void main( String[] args )
    {
//        ApplicationContext app = new ClassPathXmlApplicationContext("spring.xml");
//        Dev obj = (Dev)app.getBean("dev");
//        Lap obj2 = (Lap)app.getBean("lap");
//        obj.project();
//        System.out.println(obj2.age);
        SpringApplication.run(App.class, args);
//        Item item_1 = (Item) app.getBean("item");
//        item_1.Print(item_1);
    }
}
