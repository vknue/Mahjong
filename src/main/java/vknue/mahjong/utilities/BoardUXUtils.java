package vknue.mahjong.utilities;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.Comparator;
import java.util.List;

public class BoardUXUtils {
    private BoardUXUtils(){}

    public static void sortDeck(Pane targetPanel){
        List<ImageView> sortedImageViews = targetPanel.getChildren().stream()
                .map(node -> (ImageView) node)
                .sorted(Comparator.comparing(o -> o.getUserData().toString()))
                .toList();
        targetPanel.getChildren().clear();
        targetPanel.getChildren().addAll(sortedImageViews);
    }

    public static void clearDeck(Pane pane){
        pane.getChildren().clear();
    }
    public static void blurOutPane(Pane pane){
        ObservableList<Node> tiles = pane.getChildren();
        BoxBlur blur = new BoxBlur(50,50, 12); // Adjust blur radius as needed
        tiles.forEach(tile -> tile.setEffect(blur));
    }


}