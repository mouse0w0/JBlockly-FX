package team.unstudio.jblockly.util;

import javafx.scene.Node;

public interface FXHelper {
    static double getScreenX(Node node) {
        return node.localToScene(node.getBoundsInLocal()).getMinX()
                + node.getScene().getX() + node.getScene().getWindow().getX();
    }

    static double getScreenY(Node node) {
        return node.localToScene(node.getBoundsInLocal()).getMinY()
                + node.getScene().getY() + node.getScene().getWindow().getY();
    }
}
