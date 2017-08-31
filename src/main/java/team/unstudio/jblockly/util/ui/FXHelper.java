package team.unstudio.jblockly.util.ui;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;

public interface FXHelper {
    
    static Point2D getRelativePos(Parent parent,Node node) {
    	Point2D parentScreenPos = getScreenPos(parent);
    	Point2D nodeScreenPos = getScreenPos(node);
    	return new Point2D(nodeScreenPos.getX() - parentScreenPos.getX(), nodeScreenPos.getY() - parentScreenPos.getY());
    }
    
    static Point2D getScreenPos(Node node){
    	Parent parent = node.getParent();
    	double x = node.getLayoutX() + node.getScene().getX() + node.getScene().getWindow().getX();
    	double y = node.getLayoutY() + node.getScene().getY() + node.getScene().getWindow().getY();
    	while(parent != null){
    		x += parent.getLayoutX();
    		y += parent.getLayoutY();
    		parent = parent.getParent();
    	}	
    	return new Point2D(x, y);
    }
}
