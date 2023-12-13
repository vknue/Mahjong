module vknue.mahjong.mahjong {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;


    opens vknue.mahjong.mahjong to javafx.fxml;
    exports vknue.mahjong.mahjong;
    exports vknue.mahjong.networking.RMI to java.rmi;
    exports  vknue.mahjong.models;
    exports vknue.mahjong.utilities;
    opens vknue.mahjong.utilities to javafx.fxml;
}