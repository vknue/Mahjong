package vknue.mahjong.Models;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import vknue.mahjong.mahjong.Constants;
import vknue.mahjong.mahjong.HelloApplication;
import vknue.mahjong.mahjong.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class Game implements Serializable {

    private List<Tile> boardTiles;
    private List<Tile> player1;
    private List<Tile> player2;
    private List<String> log;
    private Tile discardedTile;
    private int turn;

    public Game() {
        boardTiles = new ArrayList<>();
        player1 = new ArrayList<>();
        player2 = new ArrayList<>();
        log = new ArrayList<>();
        discardedTile = new Tile();
        turn = 1;
        initBoard();
    }

    public List<String> getLog() {
        return log;
    }



    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    private ImageView getTileImage(String name){
        InputStream inputStream = HelloApplication.class.getClassLoader().getResourceAsStream("vknue/mahjong/images/tiles/"+name+".png");
        assert inputStream != null;
        Image image = new Image(inputStream);
        ImageView imageView = new ImageView(image);
        imageView.setUserData(name);
        imageView.setFitWidth(91);
        imageView.setFitHeight(120);
        return imageView;
    }

    private void initBoard() {
        String directory = HelloApplication.class.getClassLoader().getResource("vknue/mahjong/images/tiles/Chun.png").getPath();
        String directoryPath = directory.substring(0,directory.lastIndexOf('/'));
        for(String x : Objects.requireNonNull(Utils.getDirectoryFileNames(directoryPath))){
            for(int i=0;i<4;i++){
                Tile tile = new Tile();
                tile.setName(x);
                tile.setImage(getTileImage(x));
                boardTiles.add(tile);
            }
        }
        Collections.shuffle(boardTiles);
    }


    public List<Tile> getBoardTiles() {
        return boardTiles;
    }

    public List<Tile> getPlayer1() {
        return player1;
    }

    public List<Tile> getPlayer2() {
        return player2;
    }





    public static void saveState(Game object, String filename) throws IOException {
        try (FileOutputStream fileOut = new FileOutputStream(filename);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(object);
        }
    }

    public static Game restoreState(String filename) throws IOException, ClassNotFoundException {
        try (FileInputStream fileIn = new FileInputStream(filename);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            Game game = (Game) in.readObject();
            InputStream inputStream = HelloApplication.class.getClassLoader().getResourceAsStream("vknue/mahjong/images/tiles/"+game.getDiscardedTile().getName()+".png");
            assert inputStream != null;
            game.getDiscardedTile().setImage(new ImageView(new Image(inputStream)));
            game.getDiscardedTile().getImage().setUserData(game.discardedTile.getName());
            game.getPlayer1().forEach( x -> {
                x.setImage(new ImageView());
                x.getImage().setFitWidth(Constants.TILE_IMAGE_WIDTH);
                x.getImage().setFitHeight(Constants.TILE_IMAGE_HEIGHT);
                x.getImage().setUserData(x.getName());
                InputStream is = HelloApplication.class.getClassLoader().getResourceAsStream("vknue/mahjong/images/tiles/"+x.getName()+".png");
                assert is != null;
                x.getImage().setImage(new Image(is));
            });
            game.getPlayer2().forEach( x -> {
                x.setImage(new ImageView());
                x.getImage().setFitWidth(Constants.TILE_IMAGE_WIDTH);
                x.getImage().setFitHeight(Constants.TILE_IMAGE_HEIGHT);
                x.getImage().setUserData(x.getName());
                InputStream is = HelloApplication.class.getClassLoader().getResourceAsStream("vknue/mahjong/images/tiles/"+x.getName()+".png");
                assert is != null;
                x.getImage().setImage(new Image(is));
            });game.getBoardTiles().forEach( x -> {
                x.setImage(new ImageView());
                x.getImage().setFitWidth(Constants.TILE_IMAGE_WIDTH);
                x.getImage().setFitHeight(Constants.TILE_IMAGE_HEIGHT);
                x.getImage().setUserData(x.getName());
                InputStream is = HelloApplication.class.getClassLoader().getResourceAsStream("vknue/mahjong/images/tiles/"+x.getName()+".png");
                assert is != null;
                x.getImage().setImage(new Image(is));
            });
            return game;
        }
    }


    public Tile getDiscardedTile() {
        return discardedTile;
    }

    public void setDiscardedTile(Tile discardedTile) {
        this.discardedTile = discardedTile;
    }
}
