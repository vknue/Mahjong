package vknue.mahjong.services;

import vknue.mahjong.mahjong.AppParameters;
import vknue.mahjong.mahjong.Constants;
import vknue.mahjong.models.GameMove;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class GameMoveThread {

    private  List<GameMove> getAllMoves(){
        List<GameMove> moves = new ArrayList<>();
        if(!Files.exists(Path.of(Constants.MOVES_FILE))){
            return moves;
        }
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(Constants.MOVES_FILE))){
            moves.addAll((List<GameMove>) ois.readObject());
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return moves;
    }


    public void save(GameMove gameMove){
        List<GameMove> gameMoves = getAllMoves();
        gameMoves.add(gameMove);
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(Constants.MOVES_FILE))){
            oos.writeObject(gameMoves);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public synchronized GameMove getTheLastMove(){
        while(AppParameters.fileBeingAccessed) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        AppParameters.fileBeingAccessed =true;
        GameMove gameMove =  getAllMoves().getLast();
        AppParameters.fileBeingAccessed=false;

        notifyAll();

        return  gameMove;
    }
    public synchronized void saveMove(GameMove gameMove){

        while(AppParameters.fileBeingAccessed) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        AppParameters.fileBeingAccessed=true;
        save(gameMove);
        AppParameters.fileBeingAccessed=false;

        notifyAll();
    }
}
