package vknue.mahjong.networking.RMI;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RemoteChatServiceImpl implements RemoteChatService {

    private static List<String> chatMessagesList = new ArrayList<>(List.of("Game start"));

    public RemoteChatServiceImpl() {

    }

    @Override
    public void sendChatMessage(String chatMessage) throws RemoteException {
        chatMessagesList.add(chatMessage);
    }

    @Override
    public List<String> getAllChatMessages() throws RemoteException {
        return chatMessagesList;
    }
}
