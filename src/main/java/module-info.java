module vknue.mahjong.mahjong {
    requires javafx.controls;
    requires javafx.fxml;


    opens vknue.mahjong.mahjong to javafx.fxml;
    exports vknue.mahjong.mahjong;
    exports vknue.mahjong.utilities;
    opens vknue.mahjong.utilities to javafx.fxml;
}