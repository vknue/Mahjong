package vknue.mahjong.mahjong;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import vknue.mahjong.models.*;
import vknue.mahjong.utilities.BoardUXUtils;
import vknue.mahjong.utilities.DocumentationUtils;
import vknue.mahjong.utilities.GameUtils;
import vknue.mahjong.utilities.GeneralUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;


public class HelloController implements Initializable {
    private static Game game;
    private static List<Pane> decks;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        game = new Game();
        decks = Arrays.asList(pnlMyDeck, pnlOpponent);
        try {
            initDecks();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        BoardUXUtils.blurOutPane(pnlMyDeck);
    }

    private void initDecks() throws InterruptedException {
        Tile tile;
        for (int i = 0; i < 13; i++) {
            //
            tile = game.getBoardTiles().remove(game.getBoardTiles().size() - 1);
            game.getPlayer1().add(tile);
            ImageView finalTile = tile.getImage();
            tile.getImage().setOnMouseClicked(event -> discard(finalTile));
            pnlMyDeck.getChildren().add(tile.getImage());
            //
            tile = game.getBoardTiles().remove(game.getBoardTiles().size() - 1);
            game.getPlayer2().add(tile);
            pnlOpponent.getChildren().add(tile.getImage());
            ImageView finalTile2 = tile.getImage();
            tile.getImage().setOnMouseClicked(event -> discard(finalTile2));
        }
        tile = game.getBoardTiles().remove(game.getBoardTiles().size() - 1);
        game.getPlayer1().add(tile);
        ImageView finalTile = tile.getImage();
        tile.getImage().setOnMouseClicked(event -> discard(finalTile));
        pnlMyDeck.getChildren().add(tile.getImage());
        postMessage("Starting decks have been initialized. Good Luck!");
        postMessage("Player 1's turn. Pick a tile to discard.");
        fixTransparency();
        Arrays.asList(pnlMyDeck, pnlOpponent).forEach(deck -> BoardUXUtils.sortDeck(deck));
    }

    @FXML
    private  ImageView btnDraw;
    @FXML
    private  ImageView ivDiscarded;
    @FXML
    private  HBox pnlMyDeck;
    @FXML
    private  HBox pnlOpponent;
    @FXML
    private  VBox pnlInstructions;

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
                .sorted(Comparator.comparing(o -> o.getName()))
                .collect(Collectors.toList()))) {
            declareWinner();
        }
        BoardUXUtils.sortDeck(targetPanel);
        postMessage("Tile drawn!");
        postMessage("Player " + game.getTurn() + " discard a tile");
        fixTransparency();
    }

    private void declareWinner() {
        GeneralUtils.showMessage("Winner", "We have a winner", "Player " + game.getTurn() + " has won!");
        postMessage("Player " + game.getTurn() + " has collected a winning hand");
        postMessage("Game over!");
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
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error babiehhhhhhh");
        }
    }

    public  void properlyRestore(Game game){
        decks.forEach(deck -> BoardUXUtils.clearDeck(deck));
        game.getPlayer1().forEach(x -> x.getImage().setOnMouseClicked(e -> discard(x.getImage())));
        game.getPlayer2().forEach(x ->  x.getImage().setOnMouseClicked(e -> discard(x.getImage())));
        decks.forEach(panel -> panel.getChildren().addAll(game.getPlayer2().stream().map(Tile::getImage).collect(Collectors.toList())));
        decks.forEach(deck -> BoardUXUtils.sortDeck(deck));
        ivDiscarded.setImage(game.getDiscardedTile().getImage().getImage());
        ivDiscarded.setUserData(game.getDiscardedTile().getName());
        fixTransparency();
        game.getLog().forEach(x -> {
            Label label = new Label(x);
            label.setTextFill(Color.GREEN);
            pnlInstructions.getChildren().add(label);
        });
        GeneralUtils.showMessage("Succesfull", "Game loaded", "You have succesfully loaded your old game!");

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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(Constants.DOCUMENTATION_FILE_NAME)))) {
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
                .sorted(Comparator.comparing(o -> o.getName()))
                .collect(Collectors.toList()))){
            declareWinner();
        }
        BoardUXUtils.sortDeck(targetPanel);
        postMessage("Tile drawn! Pong!");
        postMessage("Player " + game.getTurn() + " discard a tile");
        fixTransparency();
        ivDiscarded.setImage(new Image(HelloApplication.class.getClassLoader().getResourceAsStream(Constants.BACK_IMAGE)));
    }
    private  void discard(ImageView imageView) {
        ((Pane) imageView.getParent()).getChildren().remove(imageView);
        List<Tile> currentPlayerList = (game.getTurn() == 1) ? game.getPlayer1() : game.getPlayer2();
        for (Iterator<Tile> iterator = currentPlayerList.iterator(); iterator.hasNext(); ) {
            Tile tile = iterator.next();
            if (imageView.getUserData().toString().equals(tile.getName())) {
                iterator.remove();
                break; // Stop after removing the first occurrence
            }
        }
        ivDiscarded.setImage(imageView.getImage());
        ivDiscarded.setUserData(imageView.getUserData());
        postMessage("Tile successfully discarded!");
        game.setDiscardedTile(new Tile( ivDiscarded.getUserData().toString(),new ImageView(ivDiscarded.getImage())));
        game.getDiscardedTile().getImage().setUserData(ivDiscarded.getUserData());
        game.setTurn(3 - game.getTurn()); // Toggle between 1 and 2
        postMessage("Player " + game.getTurn() + "'s turn. Draw a card.");
        fixTransparency();
    }

    private  void postMessage(String message){
        Label label = new Label(message);
        label.setTextFill(Color.GREEN);
        pnlInstructions.getChildren().add(label);
        game.getLog().add(message);
    }
    private  void fixTransparency(){
        btnDraw.setMouseTransparent((pnlOpponent.getChildren().size()==14 || pnlMyDeck.getChildren().size()==14));
        pnlMyDeck.setMouseTransparent((pnlMyDeck.getChildren().size()==13 || game.getTurn()==2));
        pnlOpponent.setMouseTransparent((pnlOpponent.getChildren().size()==13 || game.getTurn()==1));
        ivDiscarded.setMouseTransparent((pnlOpponent.getChildren().size()==14 || pnlMyDeck.getChildren().size()==14));
    }

}