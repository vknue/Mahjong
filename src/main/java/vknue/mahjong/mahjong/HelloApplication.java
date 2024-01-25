package vknue.mahjong.mahjong;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import vknue.mahjong.models.Game;
import vknue.mahjong.models.PlayerType;
import vknue.mahjong.networking.RMI.RemoteChatService;
import vknue.mahjong.networking.RMI.RemoteChatServiceImpl;

import java.rmi.AccessException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.UnicastRemoteObject;

import static javafx.application.Platform.*;

public class HelloApplication extends Application {
    private static PlayerType playerLoggedIn;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), Screen.getPrimary().getVisualBounds().getWidth(), Screen.getPrimary().getVisualBounds().getHeight());
        stage.setTitle(playerLoggedIn == null? Constants.MAHJONG : playerLoggedIn.name());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        String playerName = args.length == 0 ? Constants.MAHJONG : args[0];

            if (!isPortInUse(Constants.SERVER_PORT)) {
                playerLoggedIn = PlayerType.SERVER;
                new Thread(() -> startServer()).start();
                AppParameters.setPlayerType(PlayerType.SERVER);
            } else {
                playerLoggedIn = PlayerType.CLIENT;
                new Thread(() -> startClient()).start();
                AppParameters.setPlayerType(PlayerType.CLIENT);
            }

        launch();
    }

    private static boolean isPortInUse(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            // If the port is available and we're able to create a ServerSocket, it's not in use.
            return false;
        } catch (IOException e) {
            // If the port is already in use, creating a ServerSocket will throw an IOException.
            return true;
        }
    }

    public static void startServer() {
        startRmiServer();
        acceptRequestsOnPort(Constants.SERVER_PORT);
    }

    public static void startClient() { acceptRequestsOnPort(Constants.CLIENT_PORT); }

    private static void acceptRequestsOnPort(Integer port) {
            try (ServerSocket serverSocket = new ServerSocket(port)){
                System.err.println((port == Constants.CLIENT_PORT ? "Client" : "Server") + " is listening on port: " + serverSocket.getLocalPort());
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.err.println("Client connected from port: " + clientSocket.getPort());
                    new Thread(() ->  processSerializableClient(clientSocket)).start();
                }
            }  catch (IOException e) {
                e.printStackTrace();
            }
    }
    private static RemoteChatService remoteChatService;
    public static void startRmiServer() {
        try {
            Registry registry = LocateRegistry.createRegistry(Constants.RMI_PORT);
             remoteChatService = new RemoteChatServiceImpl();
            RemoteChatService skeleton = (RemoteChatService)
                    UnicastRemoteObject.exportObject(remoteChatService,
                            Constants.RANDOM_PORT_HINT);
            registry.rebind(Constants.REMOTE_CHAT_OBJECT_NAME, skeleton);
            System.err.println("Object registered in RMI registry");
        } catch(RemoteException ex) {
            ex.printStackTrace();
        }
    }
    private static void processSerializableClient(Socket clientSocket) {
        try (ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream())){
            Game game = (Game) ois.readObject();
            Platform.runLater(() -> HelloController.properlyRestore(game));
            System.out.println("Game state received from the client!");
            oos.writeObject("Confirmed that the game board has been received!");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}