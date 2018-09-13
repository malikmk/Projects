package com.malik;


import javax.websocket.Session;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
    Chat Controller, defined by chatController class, is to sit at one level above the server and the client classes.
    Basically, this needs to hold the Session Map and (Awaiting) User list and may be logging features at some point in
    the development.

 */
public final class chatController {


    // Using ConcurrentHashMap to allow multiple threads use the 'agentChatMaps' without any problems.
    private static  Map<Session, Session> agentChatMaps = new ConcurrentHashMap<>();

    // Keep a temporary structure to hold the user sessions till the time they are not paired with an agent.
    // Once an agent is available to pair up, remove user session from temporary structure and put in the 'agentChatMap'.
    private static LinkedList<Session> tempChatUserList = new LinkedList<>();


    private chatController(){}

    public static void addToAgentChatMaps(Session agent, Session user) {
        agentChatMaps.put(agent, user);
    }

    public static boolean removeFromAgentChatMaps(Session agent, Session user) {
        return agentChatMaps.remove(agent, user);
    }

    public static boolean addToTempChatUserList (Session user){
        try{
            tempChatUserList.add(user);
        }catch (RuntimeException e){
            throw new RuntimeException(e);
        }
        return true;
    }

    public static Session removeFromTempChatUserList (){
        try{
            return tempChatUserList.removeFirst();
        }catch (RuntimeException e){
            throw new RuntimeException(e);
        }
      }
      public static Session findPartnerChatSession(Session session){
          Iterator it = agentChatMaps.entrySet().iterator();
          while(it.hasNext()){
              Map.Entry pair = (Map.Entry) it.next();

              if (pair.getKey() == session)
                  return (Session) pair.getValue();
              else if (pair.getValue() == session)
                  return (Session) pair.getKey();
          }
          return null;
      }

      public static boolean cleanUpAgentChatMap(Session session){
        // If a chat session has finished ( Signal is either of the participants close connection)
          Iterator it = agentChatMaps.entrySet().iterator();
          while(it.hasNext()){
              Map.Entry pair = (Map.Entry) it.next();

              if (pair.getKey() == session)
                  return removeFromAgentChatMaps(session,(Session)pair.getValue());
              else if (pair.getValue() == session)
                  return removeFromAgentChatMaps((Session)pair.getKey(),session);
          }

        // If a user, who was not assigned an agent yet, has left.
          it = tempChatUserList.iterator();
          while(it.hasNext()) {
              if (it.next() == session) {
                  it.remove();
                  return true;
              }
          }
          return false;
      }
}
