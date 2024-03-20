package com.creative;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
//@EnableTransactionManagement
public class CreativeMarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(CreativeMarketApplication.class, args);
    }

}
