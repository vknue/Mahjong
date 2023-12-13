package vknue.mahjong.networking.RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RemoteChatService extends Remote {

    void sendChatMessage(String chatMessage) throws RemoteException;
    List<String> getAllChatMessages() throws RemoteException;
}


