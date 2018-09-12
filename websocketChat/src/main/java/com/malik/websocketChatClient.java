package com.malik;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.glassfish.tyrus.client.ClientManager;


public class websocketChatClient {

    @OnOpen
    public void onOpen(Session session){
        System.out.println("Connected:...."+session.getId());
        try{
            session.getBasicRemote().sendText("Handshake");

        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }


    @OnMessage
    public String onMessage(String message, Session session){
        BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));

        try{
            System.out.println("Received Message from Server:...."+ message +"....in the session.....:"+ session.getId());
            String userInput = bufferRead.readLine();
            return userInput;
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason){
        System.out.println("Session:...."+ session.getId() +"....is closing because of the following reason: "+ closeReason.toString());
    }

}
