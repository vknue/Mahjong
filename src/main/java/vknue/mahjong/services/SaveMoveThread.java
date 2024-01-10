package vknue.mahjong.services;

import vknue.mahjong.models.GameMove;

public class SaveMoveThread extends GameMoveThread implements Runnable{

    private GameMove gameMoveToSave;

    public SaveMoveThread(GameMove gameMove){
        this.gameMoveToSave = gameMove;
    }

    @Override
    public void run() {
        saveMove(gameMoveToSave);
    }
}
