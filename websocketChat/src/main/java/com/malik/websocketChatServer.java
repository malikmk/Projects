package com.malik;


import javax.websocket.*;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;


@ServerEndpoint(value="/chat/{connT}")
public class websocketChatServer {


    // Deprecated in v1.0??
    private static int countSessions = 0;

    @OnOpen
    public void onOpen(@PathParam("connT") String connType, Session session){
        System.out.println("Connected:...." + session.getId());
            switch (connType.trim()) {
                case "quit":
                    try {
                        session.close(new CloseReason(CloseCodes.NORMAL_CLOSURE, "Chat Finished!!"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "USER":
                    // 1. Add the new user looking for agent help to a temporary priority queue.
                    chatController.addToTempChatUserList(session);
                    sendReturnMessage(session, "Please Wait While We Connect You To Our Customer Service Representative!! Thanks");

                    // 2. Send SOS to (Competella's) agent pool.
                    // sendMessageToCompetella
                    // this may actually be something like :
                    // ---
                    // client.connectToServer(webSocketClient.class, new URI("ws://<COMPETELLA_INTERFACE_ADDRESS>/??/??"));
                    // ---
                    // What follows is that the webSocketClient may be used with little modification for this server to start
                    // acting like a client to the agent from competella. Technically it means that the OnOpen, OnMessage and
                    // OnClose methods of the websocketClient would be used under the umbrella of information stored in
                    // chatController datastructures, to connect USER and AGENT connections and carryout chat.
                    break;

                case "AGENT":
                    chatController.addToAgentChatMaps(session, chatController.removeFromTempChatUserList());
                    sendChatMessage(session, "You are now connected to our Representative!!\n Hi, How can I help you!!");
                    break;

            }
    }

    @OnMessage
    public void onMessage( String message, Session session){
        sendChatMessage(session, message);
        return;
    }


    @OnClose
    public void onClose(Session session, CloseReason closeReason){
        System.out.println("Session:...."+session.getId()+"....is closing down because of ....."+closeReason.toString());
        sendChatMessage(session,closeReason.toString());
        closePartnerSession(session);
        chatController.cleanUpAgentChatMap(session);
    }

    void closePartnerSession(Session session){
        Session session2SendTo = chatController.findPartnerChatSession(session);
        try {
            session2SendTo.close();
        }catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void sendChatMessage(Session session, String message){

        Session session2SendTo = chatController.findPartnerChatSession(session);
        if(session2SendTo.isOpen()) {
            // If the message is part of a chat session that this server needs to route correctly to the respective partner.
            System.out.println("sendMessage(): Message out to  Chat Partner:...." + session2SendTo.getId() + "....is....." + message);
            // Just Send It!
            session2SendTo.getAsyncRemote().sendText(message);
        }
    }

    public void sendReturnMessage(Session session, String message){
             session.getAsyncRemote().sendText(message);
    }

}
