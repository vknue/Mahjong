package vknue.mahjong.mahjong;

import vknue.mahjong.models.Game;
import vknue.mahjong.models.PlayerType;

public class AppParameters {

    private AppParameters(){}
    private static PlayerType playerType;
    public static boolean fileBeingAccessed = false;


    public static void setPlayerType(PlayerType type){
        playerType = type;
    }

    public static PlayerType getPlayerType(){
        return playerType;
    }
}
