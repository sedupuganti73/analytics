package com.bnsf.analytics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RestController;

import groovy.util.logging.Log4j;



@Log4j
@RestController
@EnableAutoConfiguration
@SpringBootApplication
@EnableScheduling
public class App  {
	
	
	
    public static void main( String[] args ) {
    	SpringApplication.run(App.class, args);
    }
    
    
}
