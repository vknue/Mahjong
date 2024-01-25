package vknue.mahjong.mahjong;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import vknue.mahjong.models.GameMove;
import vknue.mahjong.models.PlayerType;
import vknue.mahjong.utilities.BoardUXUtils;
import vknue.mahjong.utilities.GeneralUtils;
import vknue.mahjong.utilities.XMLUtils;

import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class ReplayController implements Initializable {

    @FXML
    private ImageView ivDiscarded; //2
    @FXML
    private HBox pnlMyDeck; //0
    @FXML
    private HBox pnlOpponent;  //1
    @FXML
    private VBox rootNode; //3
    @FXML
    private Button btnNextMove;
    private List<GameMove> gameMoveList;
    private int indexer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initVariables();
        initKeyListeners();
    }


    private void initKeyListeners() {
        rootNode.setOnKeyPressed(
                (e) -> {
                    if (e.getCode() == KeyCode.SPACE) {
                        iterateMove();
                    }
                }
        );
    }


    public void iterateMove() {
        if (indexer == gameMoveList.size()) {
            endGame();
            return;
        }
        GameMove gameMove = gameMoveList.get(indexer);
        indexer++;
        if (gameMove.getGameMoveType() == GameMoveType.DRAW_FROM_TABLE) {
            handleDraw(gameMove);
        } else if (gameMove.getGameMoveType() == GameMoveType.DISCARD) {
            handleDiscard(gameMove);
        } else if (gameMove.getGameMoveType() == GameMoveType.PONG) {
            handlePong(gameMove);
        }
        Arrays.asList(pnlMyDeck, pnlOpponent).forEach(BoardUXUtils::sortDeck);

    }

    private void handleDraw(GameMove gameMove) {
        InputStream inputStream = HelloApplication.class.getClassLoader().getResourceAsStream("vknue/mahjong/images/tiles/" + gameMove.getTileName() + ".png");
        assert inputStream != null;
        Image image = new Image(inputStream);
        ImageView imageView = new ImageView(image);
        imageView.setUserData(gameMove.getTileName());
        imageView.setFitWidth(Constants.TILE_IMAGE_WIDTH);
        imageView.setFitHeight(Constants.TILE_IMAGE_HEIGHT);
        Pane pane = gameMove.getPlayer() == PlayerType.CLIENT ? pnlOpponent : pnlMyDeck;
        pane.getChildren().add(imageView);

    }

    private void handleDiscard(GameMove gameMove) {
        Pane pane = gameMove.getPlayer() == PlayerType.CLIENT ? pnlMyDeck: pnlOpponent;
        ImageView selectedImageView = null;
        for (Node node : pane.getChildren()) {
            if (node instanceof ImageView && gameMove.getTileName().equals(node.getUserData())) {
                selectedImageView = (ImageView) node;
                pane.getChildren().remove(selectedImageView);
                ivDiscarded.setImage(selectedImageView.getImage());
                ivDiscarded.setUserData(gameMove.getTileName());
                break;
            }
        }
    }

    private void handlePong(GameMove gameMove) {
        Pane pane = gameMove.getPlayer() == PlayerType.CLIENT ? pnlMyDeck :pnlOpponent;
        ImageView iv = new ImageView(ivDiscarded.getImage());
        iv.setFitWidth(Constants.TILE_IMAGE_WIDTH);
        iv.setFitHeight(Constants.TILE_IMAGE_HEIGHT);
        iv.setUserData(gameMove.getTileName());
        pane.getChildren().add(iv);
        ivDiscarded.setImage(new Image(HelloApplication.class.getClassLoader().getResourceAsStream(Constants.BACK_IMAGE)));
    }

    private void initVariables() {
        gameMoveList = XMLUtils.LoadGame();
        indexer = 0;
    }

    private void endGame() {
        GeneralUtils.showMessage("Game Over", "Winner is " + gameMoveList.get(indexer - 1).getPlayer().toString(), "GG to both players!");
    }
}
