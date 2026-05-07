package com.chengxin.warning.service;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws/warning/{adminId}") //前端连接的地址
@Component
public class WaringWebScoketServer {
    //静态变量，用来存所有在线的管理员连接（线程安全）
    private static final ConcurrentHashMap<String, Session> onlineAdmins = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("adminId") String adminId){
        onlineAdmins.put(adminId,session);
        System.out.println("管理员上线:" + adminId + ",当前在线人数" +onlineAdmins.size());


    }
    @OnClose
    public void onClose(@PathParam("adminId") String adminId){
        onlineAdmins.remove(adminId);
        System.out.println("管理员下线" + adminId);

    }
    @OnError
    public void onError(Session session,Throwable error){
        System.out.println("WebScoket 发生错误");
        error.printStackTrace();
    }

    //核心方法
    public static void broadcastWarning(String message){
        for (Session session : onlineAdmins.values()){
            try{
                session.getBasicRemote().sendText(message);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
