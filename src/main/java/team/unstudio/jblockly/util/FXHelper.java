package team.unstudio.jblockly.util;

import javafx.scene.Node;
import javafx.scene.Parent;

public interface FXHelper {
    static double getScreenX(Node node) {
        return node.localToScene(node.getBoundsInLocal()).getMinX() + node.getScene().getX() + node.getScene().getWindow().getX();
    }

    static double getScreenY(Node node) {
        return node.localToScene(node.getBoundsInLocal()).getMinY() + node.getScene().getY() + node.getScene().getWindow().getY();
    }
    
    static double getSceneX(Node node){
    	return node.localToScene(node.getBoundsInLocal()).getMinX() + node.getScene().getX();
    }
    
    static double getSceneY(Node node){
    	return node.localToScene(node.getBoundsInLocal()).getMinY() + node.getScene().getY();
    }
    
    static double getRelativeX(Parent parent,Node node) {
    	return getSceneX(node)-getSceneX(parent);
    }
    
    static double getRelativeY(Parent parent,Node node) {
    	return getSceneY(node)-getSceneY(parent);
    }
}
