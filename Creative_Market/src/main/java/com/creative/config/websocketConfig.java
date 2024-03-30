package com.creative.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class websocketConfig {
    //注入一个serverEndpointExporter，该Bean会自动注册使用ServerEndpoint注解中明的websocket  endpoint
    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }

}
