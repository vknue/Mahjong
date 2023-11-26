package vknue.mahjong.mahjong;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import vknue.mahjong.models.Game;
import vknue.mahjong.models.PlayerType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

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
        if(PlayerType.SERVER.name().equals(playerName)) {
            playerLoggedIn = PlayerType.SERVER;
            startServer();
        }else if (PlayerType.CLIENT.name().equals(playerName)) {
            playerLoggedIn = PlayerType.CLIENT;
            startClient();
        }
        launch();
    }

    public static void startServer() {
        acceptRequestsOnPort(Constants.SERVER_PORT);
    }

    public static void startClient() {
        acceptRequestsOnPort(Constants.CLIENT_PORT);
    }

    private static void acceptRequestsOnPort(Integer port) {
        try (ServerSocket serverSocket = new ServerSocket(port)){
            System.err.println("Server listening on port: " + serverSocket.getLocalPort());
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.err.println("Client connected from port: " + clientSocket.getPort());
                new Thread(() ->  processSerializableClient(clientSocket)).start();
            }
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processSerializableClient(Socket clientSocket) {
        try (ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream())){
            //Game game = (Game) ois.readObject();
            //Platform.runLater(() -> HelloController.properlyRestore(game));
            System.out.println("Game board received from the client!");
            oos.writeObject("Confirmed that the game board has been received!");
        } catch (IOException  e) {
            e.printStackTrace();
        }
    }
}