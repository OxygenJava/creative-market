package com.creative.listen;


import com.alibaba.fastjson.JSON;
import com.creative.domain.commodityHomePage;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class rabbitMqListen {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @RabbitListener(queues = "topic_search_queue")
    public void receive(String json){
        commodityHomePage commodityHomePage = JSON.parseObject(json, commodityHomePage.class);
        IndexRequest indexRequest = new IndexRequest("app_seacher");
        indexRequest.id(commodityHomePage.getId().toString());
        indexRequest.source(json, XContentType.JSON);
        try {
            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("处理es消息队列失败：error => "+ e);
        }
        System.out.println("处理es消息队列成功：id => " + commodityHomePage.getId());
    }
}
