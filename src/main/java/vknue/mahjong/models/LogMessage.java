package vknue.mahjong.models;


import java.io.Serializable;

public class LogMessage  implements Serializable {

    private  String message;

    private  String color;

    public LogMessage(String message, String color) {
        setMessage(message);
        setColor(color);
    }


    public   String getMessage() {
        return message;
    }

    public  void setMessage(String message) {
        this.message = message;
    }

    public  String getColor() {
        return color;
    }

    public  void setColor(String color) {
        this.color = color;
    }
}
