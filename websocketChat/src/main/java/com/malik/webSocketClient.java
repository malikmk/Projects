package com.malik;

import org.glassfish.tyrus.client.ClientManager;

import javax.websocket.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;

@ClientEndpoint
public class webSocketClient {

    private static CountDownLatch latch;

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected:...." + session.getId());
        try {
            session.getBasicRemote().sendText("Hello, Can we chat?");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @OnMessage
    public String onMessage(String message, Session session) {
        BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));

        try {
            System.out.println("Received Message from Server:...." + message + "....in the session.....:" + session.getId());
            String userInput = bufferRead.readLine();
            return userInput;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("Session:...." + session.getId() + "....is closing because of the following reason: " + closeReason.toString());

        latch.countDown();
    }

    public void sendMessage(Session session, String message){
        try{
            session.getBasicRemote().sendText(message);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        latch = new CountDownLatch(1);

        ClientManager client = ClientManager.createClient();
        try {
            client.connectToServer(webSocketClient.class, new URI("ws://localhost:8025/websockets/chat"));
            latch.await();

        } catch (DeploymentException | URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }





}