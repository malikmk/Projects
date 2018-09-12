package com.malik;

import javax.websocket.*;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint(value="/chat")
public class websocketChatServer {

    private static Session client1, client2;
    private static int countSessions = 0;

    @OnOpen
    public void onOpen(Session session){
        System.out.println("Connected:...." + session.getId());
    }

    @OnMessage
    public String onMessage(String message, Session session){
        switch (message) {
            case "quit":
                try {
                    session.close(new CloseReason(CloseCodes.NORMAL_CLOSURE, "Chat Finished!!"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
        }

        // Save sessions for later use
        if(countSessions == 0) {
            client1 = session;
            countSessions += 1;
        }
        else if(countSessions == 1){
            client2 = session;
            countSessions += 1;

        }

        if(message.contains("SENDIT:")){
            if(session.getId().toString().equals(client1.getId().toString())) {
                sendMessage(client2, message);
                System.out.println("CHECK1: Message from Client:...." + session.getId() + "...and...."+ client2.getId()+"....is....." + message);
                return (new String("The server forwarded your message: " + message + " to session: " + client2.getId()));
            }
            else if(session.getId().toString().equals(client2.getId().toString())){
                sendMessage(client1, message);
                System.out.println("CHECK2: Message from Client:...." + session.getId() + "  and...."+ client1.getId()+ "....is....." + message);
                return (new String("The server forwarded your message: " + message + " to session: " + client1.getId()));
            }
            else
            {
                System.out.println("CHECKERROR");
                return null;
            }
        }


        System.out.println("Message from Client:...." + session.getId() + "....is....." + message);
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++");
        return (new String("The server returns your message: " + message + " from session: " + session.getId()));
    }


    @OnClose
    public void onClose(Session session, CloseReason closeReason){
        System.out.println("Session:...."+session.getId()+"....is closing down because of ....."+closeReason.toString());
    }




    public void sendMessage(Session session, String message){

        if(session.isOpen()) {
            System.out.println("sendMessage(): Message out to  Client:...." + session.getId() + "....is....." + message);
            session.getAsyncRemote().sendText(message);
        }
    }

}
