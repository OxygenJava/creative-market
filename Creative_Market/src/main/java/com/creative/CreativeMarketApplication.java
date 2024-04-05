package com.creative;

import com.creative.component.websocketServer;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class CreativeMarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(CreativeMarketApplication.class, args);

    }

}
