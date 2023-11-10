package vknue.mahjong.Models;

import javafx.scene.image.ImageView;

import java.io.Serializable;

public class Tile implements Serializable {

    private String name;

    private transient ImageView image;

    public Tile(String name, ImageView image) {
        this.name = name;
        this.image = image;
    }

    public Tile(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }
}
