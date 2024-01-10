package vknue.mahjong.services;

import javafx.application.Platform;
import javafx.scene.control.Label;
import vknue.mahjong.models.GameMove;

public class LoadMoveThread extends GameMoveThread implements Runnable {

    private Label theLastMoveLabel;

    public LoadMoveThread(Label label) {
        this.theLastMoveLabel = label;
    }

    @Override
    public void run() {
        while (true) {
            Platform.runLater(() -> {
                GameMove latestGameMove = getTheLastMove();
                theLastMoveLabel.setText(latestGameMove.toString());

            });
            try {
                Thread.sleep(3500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
