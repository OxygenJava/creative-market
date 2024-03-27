package com.creative.rabbitMqConfig;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class topicConfig {
    //创建一个消息队列
    @Bean
    public Queue searchQueue(){
        return new Queue("topic_search_queue");
    }
    //创建一个交换机
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange("topic_exchange");
    }
    //创建一个路由对象
    @Bean
    public Binding bindingTopic(){
        return BindingBuilder.bind(searchQueue()).to(topicExchange()).with("searchRouting");
    }

}
