package vknue.mahjong.utilities;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import vknue.mahjong.mahjong.AppParameters;
import vknue.mahjong.models.Game;
import vknue.mahjong.models.LogMessage;
import vknue.mahjong.models.PlayerType;

import java.util.Comparator;
import java.util.List;

public class BoardUXUtils {
    private BoardUXUtils() {
    }

    public static void sortDeck(Pane targetPanel) {
        List<ImageView> sortedImageViews = targetPanel.getChildren().stream()
                .map(node -> (ImageView) node)
                .sorted(Comparator.comparing(o -> o.getUserData().toString()))
                .toList();
        targetPanel.getChildren().clear();
        targetPanel.getChildren().addAll(sortedImageViews);
    }

    public static void clearDeck(Pane pane) {
        pane.getChildren().clear();
    }

    public static void blurOutPane(Pane pane) {
        ObservableList<Node> tiles = pane.getChildren();
        BoxBlur blur = new BoxBlur(50, 50, 12); // Adjust blur radius as needed
        tiles.forEach(tile -> tile.setEffect(blur));
    }

    public static void fixTransparency(List<Node> components, Game game) {
        components.get(4).setMouseTransparent(((((Pane) components.get(1))).getChildren().size() == 14 || ((Pane) components.get(0)).getChildren().size() == 14 || ((game.getTurn() == 2 && AppParameters.getPlayerType() == PlayerType.CLIENT) || (game.getTurn() == 1 && AppParameters.getPlayerType() == PlayerType.SERVER))));
        components.get(0).setMouseTransparent((((Pane) components.get(0)).getChildren().size() == 13 || game.getTurn() == 2 || AppParameters.getPlayerType() == PlayerType.SERVER));
        components.get(1).setMouseTransparent((((Pane) components.get(1)).getChildren().size() == 13 || game.getTurn() == 1 || AppParameters.getPlayerType() == PlayerType.CLIENT));
        components.get(2).setMouseTransparent((((Pane) components.get(1)).getChildren().size() == 14 || ((Pane) components.get(0)).getChildren().size() == 14 || ((game.getTurn() == 2 && AppParameters.getPlayerType() == PlayerType.CLIENT) || (game.getTurn() == 1 && AppParameters.getPlayerType() == PlayerType.SERVER))));
    }


    public static void postMessage(LogMessage logMessage, List<Node> components, Game game){
        Label label = new Label(logMessage.getMessage());
        label.setTextFill(Color.valueOf(logMessage.getColor()));
        label.setPadding(new Insets(2, 0, 2, 10));
        ((Pane) components.get(3)).getChildren().add(label);
        game.getLog().add(logMessage);
        ((ScrollPane) (components.get(5))).vvalueProperty().bind(((Pane) (components.get(3))).heightProperty());
    }



}