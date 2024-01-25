package vknue.mahjong.services;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Screen;
import vknue.mahjong.mahjong.Constants;
import vknue.mahjong.mahjong.HelloApplication;
import vknue.mahjong.mahjong.ReplayScene;
import vknue.mahjong.models.GameMove;
import vknue.mahjong.utilities.XMLUtils;

import java.util.List;

public class ReplayMode{

    public static void updateLastGame(List<GameMove> gameMoves){
        XMLUtils.SaveGame(gameMoves);
    }

    public void start() {
        try {
            ReplayScene.show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
