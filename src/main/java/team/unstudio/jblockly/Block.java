package team.unstudio.jblockly;

import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.layout.Region;
import javafx.scene.shape.SVGPath;

public class Block extends Region {
	private SVGPath svgPath;
	private double oldX,oldY;

	public Block() {
		svgPath = new SVGPath();
		svgPath.setContent("M 30 0 H 50 L 30 50 H 0 Z");
		
		setOnMousePressed(event->{
			oldX = event.getSceneX()-getLayoutX();
			oldY = event.getSceneY()-getLayoutY();
		});
		setOnMouseDragged(event->{
			setLayoutX(event.getSceneX()-oldX);
			setLayoutY(event.getSceneY()-oldY);
		});
		setPickOnBounds(false);
		
		getChildren().add(svgPath);
	}

	@Override
	protected void layoutChildren() {
		layoutInArea(svgPath, 0, 0, svgPath.getLayoutBounds().getWidth(), svgPath.getLayoutBounds().getHeight(), 0,
				HPos.CENTER, VPos.CENTER);
	}

	@Override
	protected double computeMinWidth(double height) {
		return svgPath.minWidth(height);
	}
	
	@Override
	protected double computeMinHeight(double width) {
		return svgPath.minHeight(width);
	}
	
	@Override
	protected double computePrefWidth(double height) {
		return svgPath.prefWidth(height);
	}

	@Override
	protected double computePrefHeight(double width) {
		return svgPath.prefHeight(width);
	}
	
	@Override
	public boolean contains(double localX, double localY) {
		return svgPath.contains(localX, localY);
	}
	
	@Override
	public boolean contains(Point2D localPoint) {
		return svgPath.contains(localPoint);
	}
}
