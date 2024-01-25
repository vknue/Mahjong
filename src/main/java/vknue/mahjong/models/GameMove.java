package vknue.mahjong.models;

import vknue.mahjong.mahjong.GameMoveType;

import java.io.Serializable;
import java.time.LocalDateTime;

public class GameMove implements Serializable {

    private PlayerType player;
    private GameMoveType gameMoveType;
    private String tileName;
    private LocalDateTime time;
    @Override
    public String toString() {
        return this.getPlayer().name()
                + " has made move "
                + this.getGameMoveType().name()
                + " with card "
                + this.getTileName()
                + " at "
                + this.getTime().toString();
    }

    public GameMove(PlayerType player, GameMoveType gameMoveType, String tileName, LocalDateTime time) {
        this.player = player;
        this.gameMoveType = gameMoveType;
        this.tileName = tileName;
        this.time = time;
    }

    public PlayerType getPlayer() {
        return player;
    }

    public void setPlayer(PlayerType player) {
        this.player = player;
    }

    public GameMoveType getGameMoveType() {
        return gameMoveType;
    }

    public void setGameMoveType(GameMoveType gameMoveType) {
        this.gameMoveType = gameMoveType;
    }

    public String getTileName() {
        return tileName;
    }

    public void setTileName(String tileName) {
        this.tileName = tileName;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
