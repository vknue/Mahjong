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
import vknue.mahjong.Models.Game;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class HelloController implements Initializable {

    private static Game game;
    private static int turn = 1 ;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        game = new Game();
        try {
            initDecks();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void initDecks() throws InterruptedException {
        ImageView tile;
        for(int i=0; i<13; i++){
            //
            tile = game.getBoardTiles().remove(game.getBoardTiles().size() - 1);
            game.getPlayer1().add(tile);
            ImageView finalTile = tile;
            tile.setOnMouseClicked(event ->discard(finalTile));
            pnlMyDeck.getChildren().add(tile);
            Thread.sleep(500);
            //
            tile = game.getBoardTiles().remove(game.getBoardTiles().size() - 1);
            game.getPlayer2().add(tile);
            pnlOpponent.getChildren().add(tile);
            ImageView finalTile2 = tile;
            tile.setOnMouseClicked(event ->discard(finalTile2));
            Thread.sleep(500);
        }
        tile = game.getBoardTiles().remove(game.getBoardTiles().size() - 1);
        game.getPlayer1().add(tile);
        ImageView finalTile = tile;
        tile.setOnMouseClicked(event ->discard(finalTile));
        pnlMyDeck.getChildren().add(tile);
        postMessage("Starting decks have been initialized. Good Luck!");
        postMessage("Player 1's turn. Pick a tile to discard.");
        btnDraw.setMouseTransparent(true);
        ivDiscarded.setMouseTransparent(true);
        pnlOpponent.setMouseTransparent(true);
    }

    @FXML
    private ImageView btnDraw;

    @FXML
    private ImageView ivDiscarded;

    @FXML
    private HBox pnlMyDeck;

    @FXML
    private HBox pnlOpponent;

    @FXML
    private VBox pnlInstructions;

    @FXML
    private void onbtnDrawClicked(MouseEvent event) {
        ImageView tile = game.getBoardTiles().remove(game.getBoardTiles().size() - 1);
        tile.setOnMouseClicked(e -> discard(tile));
        // Determine the target panel and player based on the 'turn' variable
        Pane targetPanel = (turn == 1) ? pnlMyDeck : pnlOpponent;
        List<ImageView> playerTiles = (turn != 1) ? game.getPlayer2() : game.getPlayer1();
        targetPanel.getChildren().add(tile);
        targetPanel.setMouseTransparent(false);
        playerTiles.add(tile);
        checkWinner(playerTiles.stream()
                .sorted(Comparator.comparing(o -> o.getProperties().get("name").toString()))
                .collect(Collectors.toList()));
        List<ImageView> sortedImageViews = targetPanel.getChildren().stream()
                .map(node -> (ImageView) node)
                .sorted(Comparator.comparing(o -> o.getProperties().get("name").toString()))
                .toList();
        targetPanel.getChildren().clear();
        targetPanel.getChildren().addAll(sortedImageViews);
        postMessage("Tile drawn!");
        postMessage("Player " + turn + " discard a tile");
        btnDraw.setMouseTransparent(true);
        ivDiscarded.setMouseTransparent(true);
        pnlOpponent.setMouseTransparent(turn!=2);
        pnlMyDeck.setMouseTransparent(turn!=1);
    }
        private void checkWinner(List<ImageView> sorted) {
            List<String> namesList = sorted
                    .stream()
                    .map(imageView -> imageView.getProperties().get("name").toString())
                    .toList();
            int pairs =0, sets=0;
            for(int i = 0; i<namesList.size(); i++){
                String t1 = namesList.get(i);
                //Checking the index so we dont get out of bounds error
                String t2 = (i + 1 < namesList.size()) ? namesList.get(i + 1) : "";
                String t3 = (i + 2 < namesList.size()) ? namesList.get(i + 2) : "Nothing";
                //Check if there is a pair of identical tiles
                if((t1.equals(t2) && t2.equals(t3))
                                || ((t1.substring(0, 1).equalsIgnoreCase(t2.substring(0, 1))
                                &&t1.substring(0, 1).equalsIgnoreCase(t3.substring(0, 1)))
                                && (t1.matches(".*\\d$") && t2.matches(".*\\d$") && t3.matches(".*\\d$") &&
                                Integer.parseInt(t2.substring(t2.length() - 1)) - Integer.parseInt(t1.substring(t1.length() - 1)) == 1 &&
                                Integer.parseInt(t3.substring(t3.length() - 1)) - Integer.parseInt(t2.substring(t2.length() - 1)) == 1))){
                    sets++;
                    i+=2;
                }else if (t1.equals(t2)){
                    pairs++;
                    i++;
                }else{
                    return;
                }
            }
           if(pairs * 2 + sets * 3 == 14){
               Utils.showMessage("Winner","We have a winner","Player " + turn + " has won!");
               postMessage("Player " + turn + " has collected a winning hand");
               postMessage("Game over!");
               btnDraw.setMouseTransparent(true);
               ivDiscarded.setMouseTransparent(true);
               pnlOpponent.setMouseTransparent(true);
               pnlMyDeck.setMouseTransparent(true);
           }
        }

    @FXML
    private void pong(){
        ImageView tile = new ImageView(ivDiscarded.getImage());
        Object nameProperty = ivDiscarded.getProperties().get("name");
        String name = (nameProperty != null) ? nameProperty.toString() : "DefaultName";
        tile.getProperties().put("name", name);
        tile.setOnMouseClicked(e -> discard(tile));
        tile.setFitWidth(91);
        tile.setFitHeight(120);
        // Determine the target panel and player based on the 'turn' variable
        Pane targetPanel = (turn == 1) ? pnlMyDeck : pnlOpponent;
        List<ImageView> playerTiles = (turn != 1) ? game.getPlayer2() : game.getPlayer1();
        targetPanel.getChildren().add(tile);
        targetPanel.setMouseTransparent(false);
        playerTiles.add(tile);
        checkWinner(playerTiles.stream()
                .sorted(Comparator.comparing(o -> o.getProperties().get("name").toString()))
                .collect(Collectors.toList()));
        List<ImageView> sortedImageViews = targetPanel.getChildren().stream()
                .map(node -> (ImageView) node)
                .sorted(Comparator.comparing(o -> o.getProperties().get("name").toString()))
                .toList();
        targetPanel.getChildren().clear();
        targetPanel.getChildren().addAll(sortedImageViews);
        postMessage("Tile drawn! Pong!");
        postMessage("Player " + turn + " discard a tile");
        btnDraw.setMouseTransparent(true);
        ivDiscarded.setMouseTransparent(true);
        ivDiscarded.setImage(new Image(HelloApplication.class.getClassLoader().getResourceAsStream("vknue/mahjong/images/Back.png")));
    }
    private void discard(ImageView imageView) {
        Pane parent = (Pane) imageView.getParent();
        parent.getChildren().remove(imageView);
        List<ImageView> currentPlayerList = (turn == 1) ? game.getPlayer1() : game.getPlayer2();
        currentPlayerList.remove(imageView);
        ivDiscarded.setImage(imageView.getImage());
        ivDiscarded.getProperties().put("name", imageView.getProperties().get("name"));
        postMessage("Tile successfully discarded!");
        turn = 3 - turn; // Toggle between 1 and 2
        postMessage("Player " + turn + "'s turn. Draw a card.");
        btnDraw.setMouseTransparent(false);
        ivDiscarded.setMouseTransparent(false);
        pnlOpponent.setMouseTransparent(true);
        pnlMyDeck.setMouseTransparent(true);
    }
    private void postMessage(String message){
        Label label = new Label(message);
        label.setTextFill(Color.GREEN);
        pnlInstructions.getChildren().add(label);

    }
}