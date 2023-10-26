package vknue.mahjong.Models;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import vknue.mahjong.mahjong.HelloApplication;
import vknue.mahjong.mahjong.Utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class Game {

    private List<ImageView> boardTiles;
    private List<ImageView> player1;
    private List<ImageView> player2;

    public Game() {
        boardTiles = new ArrayList<>();
        player1 = new ArrayList<>();
        player2 = new ArrayList<>();
        initBoard();
    }

    private ImageView getTile(String tile){
        InputStream inputStream = HelloApplication.class.getClassLoader().getResourceAsStream("vknue/mahjong/images/tiles/"+tile+".png");
        assert inputStream != null;
        Image image = new Image(inputStream);
        ImageView imageView = new ImageView(image);
        imageView.getProperties().put("name", tile);
        imageView.setFitWidth(91);
        imageView.setFitHeight(120);
        return imageView;
    }

    private void initBoard() {
        String directory = HelloApplication.class.getClassLoader().getResource("vknue/mahjong/images/tiles/Chun.png").getPath();
        String directoryPath = directory.substring(0,directory.lastIndexOf('/'));
        for(String x : Objects.requireNonNull(Utils.getDirectoryFileNames(directoryPath))){
            System.out.println(x);
            for(int i=0;i<4;i++){
                boardTiles.add(getTile(x));
            }
        }
        Collections.shuffle(boardTiles);
    }


    public List<ImageView> getBoardTiles() {
        return boardTiles;
    }

    public List<ImageView> getPlayer1() {
        return player1;
    }

    public List<ImageView> getPlayer2() {
        return player2;
    }

    public void setPlayer1(List<ImageView> player1) {
        this.player1 = player1;
    }

    public void setPlayer2(List<ImageView> player2) {
        this.player2 = player2;
    }
}
