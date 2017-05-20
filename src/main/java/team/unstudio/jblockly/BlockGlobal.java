package team.unstudio.jblockly;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;

public interface BlockGlobal {

	double INSERT_WIDTH = 5;
	double INSERT_OFFSET_Y = 10;
	double INSERT_HEIGHT = 10;
	
	double NEXT_WIDTH = 10;
	double NEXT_HEIGHT = 5;
	double NEXT_OFFSET_X = 10;
	
	Bounds INSERT_SLOT_BOUNDS = new BoundingBox(0, INSERT_OFFSET_Y, INSERT_WIDTH, INSERT_HEIGHT);
	Bounds NEXT_SLOT_BOUNDS = new BoundingBox(NEXT_OFFSET_X,0,NEXT_WIDTH,NEXT_HEIGHT+5);
	
	double BLOCK_SLOT_MIN_WIDTH=0;
	double BLOCK_SLOT_MIN_HEIGHT=30;
	
	double BRANCH_MIN_WIDTH = 20;
}
