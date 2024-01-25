package vknue.mahjong.mahjong;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import vknue.mahjong.models.*;
import vknue.mahjong.networking.RMI.RemoteChatService;
import vknue.mahjong.services.LoadMoveThread;
import vknue.mahjong.services.ReplayMode;
import vknue.mahjong.services.SaveMoveThread;
import vknue.mahjong.utilities.*;

import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
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
            Registry registry = LocateRegistry.getRegistry(
                    Constants.HOST_NAME,
                    Constants.RMI_PORT);
            chatServiceStub = (RemoteChatService) registry.lookup(Constants.REMOTE_CHAT_OBJECT_NAME);
            initTimelines();
            initDecks();
            initUI();
            initKeyListeners();
        } catch (InterruptedException | RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
        new Thread(new LoadMoveThread(lblLatestMove)).start();
    }

    private void initTimelines() {
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
        components = Arrays.asList(pnlMyDeck, pnlOpponent, ivDiscarded, pnlInstructions, btnDraw, spLog);
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

    private void sendChatMessage() {
        String chatMessage = AppParameters.getPlayerType().toString() + " : " + taChatBox.getText();
        try {
            chatServiceStub.sendChatMessage(chatMessage);
            postMessage(new LogMessage(chatMessage, Color.DEEPSKYBLUE.toString()));
            taChatBox.clear();
            LastCheckedMessageIndex++;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void receiveChatMessages() {
        List<String> receivedChatMessages;
        try {
            receivedChatMessages = chatServiceStub.getAllChatMessages();
            int originalListSize = receivedChatMessages.size();
            receivedChatMessages = receivedChatMessages.subList(LastCheckedMessageIndex + 1, receivedChatMessages.size());
            LastCheckedMessageIndex = originalListSize - 1;
            receivedChatMessages.forEach(x -> postMessage(new LogMessage(x, Color.DEEPSKYBLUE.toString())));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void spreadTileForPlayer(List<Tile> player, Pane deck, Tile tile) {
        player.add(tile);
        ImageView finalTile = tile.getImage();
        tile.getImage().setOnMouseClicked(event -> discard(finalTile));
        deck.getChildren().add(tile.getImage());
    }

    private void initDecks() throws InterruptedException {
        for (int i = 0; i < 13; i++) {
            Tile tile1 = game.getBoardTiles().remove(game.getBoardTiles().size() - 1);
            spreadTileForPlayer(game.getPlayer1(), pnlMyDeck, tile1);
            game.getGameMoves().add(new GameMove(PlayerType.SERVER, GameMoveType.DRAW_FROM_TABLE, tile1.getName(), LocalDateTime.now()));
            Tile tile2 = game.getBoardTiles().remove(game.getBoardTiles().size() - 1);
            spreadTileForPlayer(game.getPlayer2(), pnlOpponent, tile2);
            game.getGameMoves().add(new GameMove(PlayerType.CLIENT, GameMoveType.DRAW_FROM_TABLE, tile2.getName(), LocalDateTime.now()));
        }
        Tile tile1 = game.getBoardTiles().remove(game.getBoardTiles().size() - 1);
        spreadTileForPlayer(game.getPlayer1(), pnlMyDeck, tile1);
        game.getGameMoves().add(new GameMove(PlayerType.SERVER, GameMoveType.DRAW_FROM_TABLE, tile1.getName(), LocalDateTime.now()));
        postMessage(new LogMessage("Starting decks have been initialized. Good Luck!", Color.GREEN.toString()));
        postMessage(new LogMessage("Player 1's turn. Pick a tile to discard.", Color.GREEN.toString()));
        BoardUXUtils.fixTransparency(components, game);
        Arrays.asList(pnlMyDeck, pnlOpponent).forEach(BoardUXUtils::sortDeck);
    }

    @FXML
    private ScrollPane spLog; //5
    @FXML
    private ImageView btnDraw; //4
    @FXML
    private ImageView ivDiscarded; //2
    @FXML
    private Label lblLatestMove;
    @FXML
    private HBox pnlMyDeck; //0
    @FXML
    private HBox pnlOpponent;  //1
    @FXML
    private VBox pnlInstructions; //3
    @FXML
    private TextArea taChatBox;

    @FXML
    private void onbtnSendChatMessageClicked() {
        sendChatMessage();
    }

    @FXML
    private void onbtnDrawClicked() {
        Tile tile = game.getBoardTiles().remove(game.getBoardTiles().size() - 1);
        tile.getImage().setOnMouseClicked(e -> discard(tile.getImage()));
        // Determine the target panel and player based on the 'turn' variable
        Pane targetPanel = (game.getTurn() == 1) ? pnlMyDeck : pnlOpponent;
        List<Tile> playerTiles = (game.getTurn() != 1) ? game.getPlayer2() : game.getPlayer1();
        targetPanel.getChildren().add(tile.getImage());
        targetPanel.setMouseTransparent(false);
        playerTiles.add(tile);
        GameMove gameMove = new GameMove(AppParameters.getPlayerType(), GameMoveType.DRAW_FROM_TABLE, tile.getName(), LocalDateTime.now());
        SaveMoveThread smThread = new SaveMoveThread(gameMove);
        game.getGameMoves().add(gameMove);
        new Thread(smThread).start();
        if (GameUtils.checkWinner(playerTiles.stream()
                .sorted(Comparator.comparing(Tile::getName))
                .collect(Collectors.toList()))) {
            declareWinner();
            NetworkingUtils.sendGameStateToPort(game, (AppParameters.getPlayerType() == PlayerType.SERVER ? Constants.CLIENT_PORT : Constants.SERVER_PORT));
        }
        BoardUXUtils.sortDeck(targetPanel);
        postMessage(new LogMessage("Tile drawn!", Color.GREEN.toString()));
        postMessage(new LogMessage("Player " + game.getTurn() + " discard a tile", Color.GREEN.toString()));
        BoardUXUtils.fixTransparency(components, game);
        BoardUXUtils.blurOutPane(AppParameters.getPlayerType() == PlayerType.SERVER ? pnlMyDeck : pnlOpponent);
    }

    private static void declareWinner() {
        GeneralUtils.showMessage("Winner", "We have a winner", "Player " + game.getTurn() + " has won!");
        BoardUXUtils.fixTransparency(components, game);
        ReplayMode.updateLastGame(game.getGameMoves());
    }

    @FXML
    private void saveGame() {
        Game.saveState(game, Constants.STATE_FILE_NAME);
    }

    @FXML
    private void loadGame() {
        game = Game.restoreState(Constants.STATE_FILE_NAME);
        properlyRestore(game);
        GeneralUtils.showMessage("Successful", "Game loaded", "You have successfully loaded your old game!");
    }

    public static void properlyRestore(Game gameToRestore) {
        decks.forEach(BoardUXUtils::clearDeck);
        BoardUXUtils.clearDeck((Pane) components.get(3));
        GameUtils.setUpImages(gameToRestore);
        gameToRestore.getPlayer1().forEach(x -> x.getImage().setOnMouseClicked(e -> discard(x.getImage())));
        gameToRestore.getPlayer2().forEach(x -> x.getImage().setOnMouseClicked(e -> discard(x.getImage())));
        decks.get(1).getChildren().addAll(gameToRestore.getPlayer2().stream().map(Tile::getImage).toList());
        decks.get(0).getChildren().addAll(gameToRestore.getPlayer1().stream().map(Tile::getImage).toList());
        decks.forEach(BoardUXUtils::sortDeck);
        ((ImageView) components.get(2)).setImage(gameToRestore.getDiscardedTile().getImage().getImage());
        components.get(2).setUserData(gameToRestore.getDiscardedTile().getName());
        BoardUXUtils.blurOutPane(AppParameters.getPlayerType() == PlayerType.SERVER ? (Pane) components.get(0) : (Pane) components.get(1));
        gameToRestore.getLog().forEach(HelloController::postMessage);
        game = gameToRestore;
        if (GameUtils.checkWinner(((game.getTurn() != 1) ? game.getPlayer2() : game.getPlayer1()).stream().sorted(Comparator.comparing(Tile::getName)).collect(Collectors.toList()))) {
            declareWinner();
        }
        BoardUXUtils.fixTransparency(components, game);
    }

    @FXML
    private void generateDocumentation() {
        DocumentationUtils.generateDocumentation();
    }

    @FXML
    private void openDocumentation() {
        try {
            new ProcessBuilder("cmd", "/c", Constants.DOCUMENTATION_FILE_NAME).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void startReplayMode() {
        new ReplayMode().start();
    }

    @FXML
    private void pong() {
        Tile tile = GameUtils.getPongImage(ivDiscarded);
        tile.getImage().setOnMouseClicked(e -> discard(tile.getImage()));
        Pane targetPanel = (game.getTurn() == 1) ? pnlMyDeck : pnlOpponent;
        List<Tile> playerTiles = (game.getTurn() != 1) ? game.getPlayer2() : game.getPlayer1();
        targetPanel.getChildren().add(tile.getImage());
        playerTiles.add(tile);
        GameMove gameMove = new GameMove(AppParameters.getPlayerType(), GameMoveType.PONG, tile.getName(), LocalDateTime.now());
        game.getGameMoves().add(gameMove);
        SaveMoveThread smThread = new SaveMoveThread(gameMove);
        new Thread(smThread).start();
        if (GameUtils.checkWinner(playerTiles.stream().sorted(Comparator.comparing(Tile::getName)).collect(Collectors.toList()))) {
            declareWinner();
            NetworkingUtils.sendGameStateToPort(game, (AppParameters.getPlayerType() == PlayerType.SERVER ? Constants.CLIENT_PORT : Constants.SERVER_PORT));
        }
        BoardUXUtils.sortDeck(targetPanel);
        postMessage(new LogMessage("Tile drawn! Pong!", Color.GREEN.toString()));
        postMessage(new LogMessage("Player " + game.getTurn() + " discard a tile", Color.GREEN.toString()));
        BoardUXUtils.fixTransparency(components, game);
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
        GameMove gameMove = new GameMove(AppParameters.getPlayerType(), GameMoveType.DISCARD, imageView.getUserData().toString(), LocalDateTime.now());
        game.getGameMoves().add(gameMove);
        SaveMoveThread smThread = new SaveMoveThread(gameMove);
        new Thread(smThread).start();
        ((ImageView) components.get(2)).setImage(imageView.getImage());
        components.get(2).setUserData(imageView.getUserData());
        postMessage(new LogMessage("Tile successfully discarded", Color.GREEN.toString()));
        game.setDiscardedTile(new Tile(components.get(2).getUserData().toString(), new ImageView(((ImageView) components.get(2)).getImage())));
        game.getDiscardedTile().getImage().setUserData(components.get(2).getUserData());
        game.setTurn(3 - game.getTurn()); // Toggle between 1 and 2
        postMessage(new LogMessage("Player " + game.getTurn() + "'s turn. Draw a card.", Color.GREEN.toString()));
        NetworkingUtils.sendGameStateToPort(game, (AppParameters.getPlayerType() == PlayerType.SERVER ? Constants.CLIENT_PORT : Constants.SERVER_PORT));
        BoardUXUtils.fixTransparency(components, game);
    }

    private static void postMessage(LogMessage logMessage) {
        BoardUXUtils.postMessage(logMessage, components, game);
    }
}