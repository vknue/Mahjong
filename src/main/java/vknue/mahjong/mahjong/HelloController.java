package vknue.mahjong.mahjong;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import vknue.mahjong.models.*;
import vknue.mahjong.networking.RMI.RemoteChatService;
import vknue.mahjong.utilities.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.stream.Collectors;

public class HelloController implements Initializable {
    private static Game game;
    private static List<Pane> decks;
    private int LastCheckedMessageIndex;
    private static List<Node> components;

    private static RemoteChatService chatServiceStub;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            initVariables();
            initDecks();
            initUI();
            initKeyListeners();
            Registry registry = LocateRegistry.getRegistry(
                    Constants.HOST_NAME,
                    Constants.RMI_PORT);
            chatServiceStub = (RemoteChatService) registry.lookup(Constants.REMOTE_CHAT_OBJECT_NAME);

        } catch (InterruptedException | RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> receiveChatMessages()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();

    }


    private void initUI() {
        BoardUXUtils.blurOutPane(AppParameters.getPlayerType() == PlayerType.SERVER ? pnlMyDeck : pnlOpponent);
    }

    private void initVariables() {
        game = new Game();
        decks = Arrays.asList(pnlMyDeck, pnlOpponent);
        components = Arrays.asList(pnlMyDeck,pnlOpponent,ivDiscarded,pnlInstructions, btnDraw, spLog);
        LastCheckedMessageIndex = 0;
    }

    private void initKeyListeners() {
        taChatBox.setOnKeyPressed(
                (e) -> {
                    if (e.getCode() == KeyCode.ENTER) {
                        sendChatMessage();
                    }
                }
        );
    }

    private void sendChatMessage(){
        String chatMessage = AppParameters.getPlayerType().toString() + " : " + taChatBox.getText();
        try {
            chatServiceStub.sendChatMessage(chatMessage);
            postMessage(new LogMessage(chatMessage, Color.DEEPSKYBLUE.toString()));
            taChatBox.clear();
            LastCheckedMessageIndex++;
        } catch (RemoteException e) {
            e.printStackTrace();
        }catch (IllegalArgumentException e){
        }
    }

    public void receiveChatMessages() {
        List<String> receivedChatMessages;
        try {
            receivedChatMessages = chatServiceStub.getAllChatMessages();
            int originalListSize = receivedChatMessages.size();
            receivedChatMessages = receivedChatMessages.subList(LastCheckedMessageIndex+1, receivedChatMessages.size());
            LastCheckedMessageIndex = originalListSize-1;
            receivedChatMessages.forEach(x -> postMessage(new LogMessage(x, Color.DEEPSKYBLUE.toString())));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void spreadTileForPlayer(List<Tile> player,Pane deck, Tile tile){
        player.add(tile);
        ImageView finalTile = tile.getImage();
        tile.getImage().setOnMouseClicked(event -> discard(finalTile));
        deck.getChildren().add(tile.getImage());
    }

    private void initDecks() throws InterruptedException {
        for (int i = 0; i < 13; i++) {
            spreadTileForPlayer(game.getPlayer1(),pnlMyDeck,game.getBoardTiles().remove(game.getBoardTiles().size() - 1));
            spreadTileForPlayer(game.getPlayer2(),pnlOpponent,game.getBoardTiles().remove(game.getBoardTiles().size() - 1));
        }
        spreadTileForPlayer(game.getPlayer1(),pnlMyDeck,game.getBoardTiles().remove(game.getBoardTiles().size() - 1));
        postMessage(new LogMessage("Starting decks have been initialized. Good Luck!", Color.GREEN.toString()));
        postMessage(new LogMessage("Player 1's turn. Pick a tile to discard.",Color.GREEN.toString()));
        fixTransparency();
        Arrays.asList(pnlMyDeck, pnlOpponent).forEach(BoardUXUtils::sortDeck);
    }
    @FXML
    private ScrollPane spLog; //5
    @FXML
    private  ImageView btnDraw; //4
    @FXML
    private  ImageView ivDiscarded; //2
    @FXML
    private  HBox pnlMyDeck; //0
    @FXML
    private  HBox pnlOpponent;  //1
    @FXML
    private  VBox pnlInstructions; //3
    @FXML
    private TextArea taChatBox;
    @FXML
    private void  onbtnSendChatMessageClicked(){
        sendChatMessage();
    }

    @FXML
    private void onbtnDrawClicked(MouseEvent event) {
        Tile tile = game.getBoardTiles().remove(game.getBoardTiles().size() - 1);
        tile.getImage().setOnMouseClicked(e -> discard(tile.getImage()));
        // Determine the target panel and player based on the 'turn' variable
        Pane targetPanel = (game.getTurn() == 1) ? pnlMyDeck : pnlOpponent;
        List<Tile> playerTiles = (game.getTurn() != 1) ? game.getPlayer2() : game.getPlayer1();
        targetPanel.getChildren().add(tile.getImage());
        targetPanel.setMouseTransparent(false);
        playerTiles.add(tile);
        if (GameUtils.checkWinner(playerTiles.stream()
                .sorted(Comparator.comparing(Tile::getName))
                .collect(Collectors.toList()))) {
            declareWinner();
            NetworkingUtils.sendGameStateToPort(game, (AppParameters.getPlayerType()==PlayerType.SERVER ? Constants.CLIENT_PORT : Constants.SERVER_PORT));
        }
        BoardUXUtils.sortDeck(targetPanel);
        postMessage(new LogMessage("Tile drawn!", Color.GREEN.toString()));
        postMessage(new LogMessage("Player " + game.getTurn() + " discard a tile", Color.GREEN.toString()));
        fixTransparency();
        BoardUXUtils.blurOutPane(AppParameters.getPlayerType() == PlayerType.SERVER ? pnlMyDeck : pnlOpponent);
    }

    private static void declareWinner() {
        GeneralUtils.showMessage("Winner", "We have a winner", "Player " + game.getTurn() + " has won!");
        fixTransparency();
    }

    @FXML
    private void saveGame() {
        try {
            Game.saveState(game, Constants.STATE_FILE_NAME);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void loadGame() {
        try {
            game = Game.restoreState(Constants.STATE_FILE_NAME);
            properlyRestore(game);
            GeneralUtils.showMessage("Succesfull", "Game loaded", "You have succesfully loaded your old game!");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error babiehhhhhhh");
        }
    }

    public static void properlyRestore(Game gameToRestore){
        decks.forEach(BoardUXUtils::clearDeck);
        BoardUXUtils.clearDeck((Pane) components.get(3));
        GameUtils.setUpImages(gameToRestore);
        gameToRestore.getPlayer1().forEach(x -> x.getImage().setOnMouseClicked(e -> discard(x.getImage())));
        gameToRestore.getPlayer2().forEach(x ->  x.getImage().setOnMouseClicked(e -> discard(x.getImage())));
        decks.get(1).getChildren().addAll(gameToRestore.getPlayer2().stream().map(Tile::getImage).toList());
        decks.get(0).getChildren().addAll(gameToRestore.getPlayer1().stream().map(Tile::getImage).toList());
        decks.forEach(BoardUXUtils::sortDeck);
        ((ImageView)components.get(2)).setImage(gameToRestore.getDiscardedTile().getImage().getImage());
        components.get(2).setUserData(gameToRestore.getDiscardedTile().getName());
        BoardUXUtils.blurOutPane(AppParameters.getPlayerType() == PlayerType.SERVER ? (Pane)components.get(0) : (Pane)components.get(1));
        gameToRestore.getLog().forEach(HelloController::postMessage);
        game=gameToRestore;
        if (GameUtils.checkWinner(((game.getTurn() != 1) ? game.getPlayer2() : game.getPlayer1()).stream()
                .sorted(Comparator.comparing(Tile::getName))
                .collect(Collectors.toList()))) {
            declareWinner();
        }
        fixTransparency();
    }
    @FXML
    private void generateDocumentation(){
        StringBuilder sb = new StringBuilder();
        List<Class<?>> classes = Arrays.asList(HelloController.class, Game.class, Tile.class, GeneralUtils.class, Constants.class);
        sb
                .append(DocumentationUtils.HTML_OPENING)
                .append(DocumentationUtils.HEADER_OPENING);
        classes.forEach(x -> {
            sb.append(DocumentationUtils.getDocumentationForClass(x));
            sb.append(DocumentationUtils.HORIZONTAL_LINE);
                });
        sb
                .append(DocumentationUtils.HEADER_CLOSING)
                .append(DocumentationUtils.HTML_CLOSING);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Constants.DOCUMENTATION_FILE_NAME))) {
            writer.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        GeneralUtils.showMessage("Done!","Documentation generated!", "Documentation has been succesfully generated");
    }
    @FXML
    private void openDocumentation(){
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", Constants.DOCUMENTATION_FILE_NAME);
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void pong(){
        Tile tile = new Tile();
        tile.setName(ivDiscarded.getUserData().toString());
        ImageView iv = new ImageView(ivDiscarded.getImage());
        iv.setUserData(tile.getName());
        tile.setImage(iv);
        tile.getImage().setOnMouseClicked(e -> discard(tile.getImage()));
        tile.getImage().setFitWidth(Constants.TILE_IMAGE_WIDTH);
        tile.getImage().setFitHeight(Constants.TILE_IMAGE_HEIGHT);
        // Determine the target panel and player based on the 'turn' variable
        Pane targetPanel = (game.getTurn() == 1) ? pnlMyDeck : pnlOpponent;
        List<Tile> playerTiles = (game.getTurn() != 1) ? game.getPlayer2() : game.getPlayer1();
        targetPanel.getChildren().add(tile.getImage());
        targetPanel.setMouseTransparent(false);
        playerTiles.add(tile);
        if(GameUtils.checkWinner(playerTiles.stream()
                .sorted(Comparator.comparing(Tile::getName))
                .collect(Collectors.toList()))){
            declareWinner();
            NetworkingUtils.sendGameStateToPort(game, (AppParameters.getPlayerType()==PlayerType.SERVER ? Constants.CLIENT_PORT : Constants.SERVER_PORT));
        }
        BoardUXUtils.sortDeck(targetPanel);
        postMessage(new LogMessage("Tile drawn! Pong!", Color.GREEN.toString()));
        postMessage(new LogMessage("Player " + game.getTurn() + " discard a tile", Color.GREEN.toString()));
        fixTransparency();
        ivDiscarded.setImage(new Image(HelloApplication.class.getClassLoader().getResourceAsStream(Constants.BACK_IMAGE)));
        BoardUXUtils.blurOutPane(AppParameters.getPlayerType() == PlayerType.SERVER ? pnlMyDeck : pnlOpponent);
    }
    private static void discard(ImageView imageView) {
        ((Pane) imageView.getParent()).getChildren().remove(imageView);
        List<Tile> currentPlayerList = (game.getTurn() == 1) ? game.getPlayer1() : game.getPlayer2();
        for (Iterator<Tile> iterator = currentPlayerList.iterator(); iterator.hasNext(); ) {
            Tile tile = iterator.next();
            if (imageView.getUserData().toString().equals(tile.getName())) {
                iterator.remove();
                break; // Stop after removing the first occurrence
            }
        }
        ((ImageView)components.get(2)).setImage(imageView.getImage());
        components.get(2).setUserData(imageView.getUserData());
        postMessage(new LogMessage("Tile succesfully discarded", Color.GREEN.toString()));
        game.setDiscardedTile(new Tile( components.get(2).getUserData().toString(),new ImageView(((ImageView)components.get(2)).getImage())));
        game.getDiscardedTile().getImage().setUserData(components.get(2).getUserData());
        game.setTurn(3 - game.getTurn()); // Toggle between 1 and 2
        postMessage(new LogMessage("Player " + game.getTurn() + "'s turn. Draw a card.", Color.GREEN.toString()));
        NetworkingUtils.sendGameStateToPort(game, (AppParameters.getPlayerType()==PlayerType.SERVER ? Constants.CLIENT_PORT : Constants.SERVER_PORT));
        fixTransparency();
    }

    private static void postMessage(LogMessage logMessage){
        Label label = new Label(logMessage.getMessage());
        label.setTextFill(Color.valueOf(logMessage.getColor()));
        label.setPadding(new Insets(2, 0, 2, 10));
        ((Pane)components.get(3)).getChildren().add(label);
        game.getLog().add(logMessage);
        ((ScrollPane)(components.get(5))).vvalueProperty().bind(((Pane)(components.get(3))).heightProperty());
    }
    private static void fixTransparency(){
        components.get(4).setMouseTransparent(((((Pane)components.get(1))).getChildren().size()==14 || ((Pane)components.get(0)).getChildren().size()==14 || ((game.getTurn()==2&&AppParameters.getPlayerType()==PlayerType.CLIENT) || (game.getTurn()==1&&AppParameters.getPlayerType()==PlayerType.SERVER))) );
        components.get(0).setMouseTransparent((((Pane)components.get(0)).getChildren().size()==13 || game.getTurn()==2 || AppParameters.getPlayerType() ==  PlayerType.SERVER) );
        components.get(1).setMouseTransparent((((Pane)components.get(1)).getChildren().size()==13 || game.getTurn()==1 || AppParameters.getPlayerType() == PlayerType.CLIENT));
        components.get(2).setMouseTransparent((((Pane)components.get(1)).getChildren().size()==14 || ((Pane)components.get(0)).getChildren().size()==14|| ((game.getTurn()==2&&AppParameters.getPlayerType()==PlayerType.CLIENT) || (game.getTurn()==1&&AppParameters.getPlayerType()==PlayerType.SERVER))) );
    }

}