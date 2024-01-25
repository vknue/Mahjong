package vknue.mahjong.utilities;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import vknue.mahjong.mahjong.Constants;
import vknue.mahjong.mahjong.GameMoveType;
import vknue.mahjong.mahjong.HelloApplication;
import vknue.mahjong.models.*;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class GameUtils {
    private GameUtils() {
    }

    public static void setUpImages(Game game) {
        InputStream inputStream = HelloApplication.class.getClassLoader().getResourceAsStream("vknue/mahjong/images/tiles/" + game.getDiscardedTile().getName() + ".png");
        assert inputStream != null;
        game.getDiscardedTile().setImage(new ImageView(new Image(inputStream)));
        game.getDiscardedTile().getImage().setUserData(game.getDiscardedTile().getName());
        game.getPlayer1().forEach(x -> {
            x.setImage(new ImageView());
            x.getImage().setFitWidth(Constants.TILE_IMAGE_WIDTH);
            x.getImage().setFitHeight(Constants.TILE_IMAGE_HEIGHT);
            x.getImage().setUserData(x.getName());
            InputStream is = HelloApplication.class.getClassLoader().getResourceAsStream("vknue/mahjong/images/tiles/" + x.getName() + ".png");
            assert is != null;
            x.getImage().setImage(new Image(is));
        });
        game.getPlayer2().forEach(x -> {
            x.setImage(new ImageView());
            x.getImage().setFitWidth(Constants.TILE_IMAGE_WIDTH);
            x.getImage().setFitHeight(Constants.TILE_IMAGE_HEIGHT);
            x.getImage().setUserData(x.getName());
            InputStream is = HelloApplication.class.getClassLoader().getResourceAsStream("vknue/mahjong/images/tiles/" + x.getName() + ".png");
            assert is != null;
            x.getImage().setImage(new Image(is));
        });
        game.getBoardTiles().forEach(x -> {
            x.setImage(new ImageView());
            x.getImage().setFitWidth(Constants.TILE_IMAGE_WIDTH);
            x.getImage().setFitHeight(Constants.TILE_IMAGE_HEIGHT);
            x.getImage().setUserData(x.getName());
            InputStream is = HelloApplication.class.getClassLoader().getResourceAsStream("vknue/mahjong/images/tiles/" + x.getName() + ".png");
            assert is != null;
            x.getImage().setImage(new Image(is));
        });
    }

    public static boolean checkWinner(List<Tile> sorted) {
        List<String> namesList = sorted.stream()
                .map(Tile::getName)
                .sorted()
                .toList();
        int pairs = 0, sets = 0;
        for (int i = 0; i < namesList.size(); i++) {
            String t1 = namesList.get(i);
            //Checking the index, so we don't get out of bounds error
            String t2 = (i + 1 < namesList.size()) ? namesList.get(i + 1) : "";
            String t3 = (i + 2 < namesList.size()) ? namesList.get(i + 2) : "Nothing";
            //Check if there is a pair of identical tiles
            if ((t1.equals(t2) && t2.equals(t3)) || ((t1.substring(0, 1).equalsIgnoreCase(t2.substring(0, 1)) && t1.substring(0, 1).equalsIgnoreCase(t3.substring(0, 1))) && (t1.matches(".*\\d$") && t2.matches(".*\\d$") && t3.matches(".*\\d$") && Integer.parseInt(t2.substring(t2.length() - 1)) - Integer.parseInt(t1.substring(t1.length() - 1)) == 1 && Integer.parseInt(t3.substring(t3.length() - 1)) - Integer.parseInt(t2.substring(t2.length() - 1)) == 1))) {
                sets++;
                i += 2;
            } else if (t1.equals(t2)) {
                pairs++;
                i++;
            } else {
                return false;
            }
        }
        return pairs * 2 + sets * 3 == 14;
    }

    public static Tile getPongImage(ImageView ivDiscarded) {
        Tile tile = new Tile();
        tile.setName(ivDiscarded.getUserData().toString());
        ImageView iv = new ImageView(ivDiscarded.getImage());
        iv.setUserData(tile.getName());
        tile.setImage(iv);
        tile.getImage().setFitWidth(Constants.TILE_IMAGE_WIDTH);
        tile.getImage().setFitHeight(Constants.TILE_IMAGE_HEIGHT);
        return tile;
    }

}