package vknue.mahjong.mahjong;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ReplayScene {

    public static void show() throws Exception {
        FXMLLoader replayLoader = new FXMLLoader(ReplayScene.class.getResource("replay-view.fxml"));
        Parent replayRoot = replayLoader.load();
        ReplayController replayController = replayLoader.getController();

        Scene secondScene = new Scene(replayRoot);
        Stage secondStage = new Stage();
        secondStage.setScene(secondScene);
        secondStage.setTitle("Replay Mode");
        secondStage.show();
    }

}
