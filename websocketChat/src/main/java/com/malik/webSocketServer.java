package com.malik;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.glassfish.tyrus.server.Server;

public class webSocketServer {
    public static void main(String[] args){
        runServer();
    }

    public static void runServer(){
        Server server = new Server("localhost",8025,"/websockets", websocketChatServer.class);

        try{
            server.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Press any Key to stop server....");
            reader.readLine();

        }catch(Exception e) {
            throw new RuntimeException(e);
        } finally {
            server.stop();
        }


    }


}
