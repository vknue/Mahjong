package vknue.mahjong.utilities;

import vknue.mahjong.mahjong.Constants;
import vknue.mahjong.models.Game;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NetworkingUtils {

    private NetworkingUtils() {}

    public static void sendGameStateToPort(Game gameState, int Port) {
        try (Socket clientSocket = new Socket(Constants.HOST_NAME,
                Port))
        {
            System.err.println("Client is connecting to " + clientSocket.getInetAddress() + ":" +clientSocket.getPort());
            sendSerializableRequest(clientSocket, gameState);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void sendSerializableRequest(Socket client, Game gameState) throws ClassNotFoundException, IOException {
        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
        oos.writeObject(gameState);
        System.out.println("Game board sent to the server!");
    }



}
