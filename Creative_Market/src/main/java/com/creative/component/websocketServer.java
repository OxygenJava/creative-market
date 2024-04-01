package com.creative.component;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@ServerEndpoint( "/websocket/{username}")
@Component
public class websocketServer {

    private static final Logger log= LoggerFactory.getLogger(websocketServer.class);

    //记录当前在线连接数
    private static final Map<String, Session> sessionMap=new HashMap<>();

    private static final Map<String, Session> sessionMap1=new HashMap<>();


    private static final Map<String, Session> sessionMap2=new HashMap<>();


    private static final Session session=null;

    private static final ArrayList<String> list=new ArrayList<>();



    static {
        sessionMap1.put("zhang",session);
        sessionMap1.put("admin",session);
        sessionMap1.put("wang",session);
    }

    //连接建立成功调用的方法
    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username){

        sessionMap.put(username,session);


        log.info("有新用户加入，username={}，当前在线人数为：{}",username,sessionMap.size());
        JSONObject result=new JSONObject();
        JSONArray array=new JSONArray();
        JSONObject result1=new JSONObject();
        JSONArray array1=new JSONArray();
        result.set("onlineusers",array);
        result1.set("offlineusers",array1);
        for (Object key : sessionMap.keySet()) {
            JSONObject jsonObject=new JSONObject();
            jsonObject.set("username",key);
            array.add(jsonObject);
        }

        list.add("0");

        if(sessionMap1.containsKey(list.get(0))){
            sendMessage(sessionMap2.keySet().toString(),sessionMap.get(username));

        }

        sessionMap2.clear();

        sessionMap1.remove(username);

        for (String s : sessionMap1.keySet()) {
            JSONObject jsonObject2=new JSONObject();
            jsonObject2.set("username",s);
            array1.add(jsonObject2);
        }

        sendAllMessage(JSONUtil.toJsonStr(result));//后台发送消息给所有的客户端
        sendAllMessage(JSONUtil.toJsonStr(result1));



    }

    //连接关闭调用的方法
    @OnClose
    public void onClose(Session session, @PathParam("username") String username){
        sessionMap1.put(username,session);
        sessionMap.remove(username);

        log.info("有一连接关闭，移除username={}的用户session，当前在线人数为：{}",username,sessionMap.size());
    }

    //收到客户端消息后调用的方法
    //后台收到客户端发送过来的消息
    //message是客户端发送过来的消息
    //onMessage是一个小希的中转站

    @OnMessage
    public void onMessage(String message,Session session,@PathParam("username") String username){
        log.info("服务端收到用户username={}的消息：{}",username,message);

        JSONObject jsonObject = JSONUtil.parseObj(message);
        String toUsername = jsonObject.getStr("to");//to表示发送给哪个用户，比如 admin
        String text = jsonObject.getStr("text");//表示发送的消息文本
        Session toSession = sessionMap.get(toUsername.toString());//根据to（用户名）来获取session，再通过session发送消息文本

        if(toSession!=null){
            JSONObject jsonObject1=new JSONObject();
            jsonObject1.set("from",username);
            jsonObject1.set("text",text);
            this.sendMessage(jsonObject1.toString(),toSession);
            log.info("发送给用户username={}，消息：{}",toUsername,jsonObject1.toString());
        }
        else {
            JSONObject jsonObject1=new JSONObject();
            jsonObject1.set("from",username);
            jsonObject1.set("text",text);
//            this.sendMessage(jsonObject1.toString(),toSession1);
//            String from= jsonObject1.getStr("from");
//            String text1 = jsonObject1.getStr("text");
//            String xiaoxi="from:"+from+","+"text:"+text1;
            sessionMap2.put(jsonObject1.toString(),session);
            list.set(0,toUsername);
            log.info("发送给用户username={}，消息：{}",toUsername,jsonObject1.toString()+""+list);
        }

    }

    //服务端发送消息给客户端
    private void sendMessage(String message,Session toSession){
        try
        {
            log.info("服务端给客户端[{}]发送消息{}",toSession.getId(),message);
            toSession.getBasicRemote().sendText(message);
        }catch (Exception e){
            log.error("服务端发送消息给客户端失败",e);
        }
    }

    @OnError
    public void onError(Session session,Throwable error){
        log.error("发生错误");
        error.printStackTrace();
    }


    //服务端发送消息给所有客户端
    private void sendAllMessage(String message){
        try {
            for (Session session : sessionMap.values()) {
                log.info("服务端给客户端[{}]发送消息{}",session.getId(),message);
                session.getBasicRemote().sendText(message);
            }
        }catch (Exception e){
            log.error("服务端发送消息给客户端失败",e);
        }


}

}
