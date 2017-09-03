package team.unstudio.jblockly.util;

import javafx.geometry.Point2D;
import javafx.scene.control.TextArea;
import javafx.stage.Popup;
import team.unstudio.jblockly.Block;
import team.unstudio.jblockly.util.ui.FXHelper;

public enum BlockHelper {

	;
	
	private static Popup NOTE_POPUP;
	private static TextArea NOTE_TEXT_AREA;
	private static Block NOTEING_BLOCK;
	public static void showNotePopup(Block block){
		if(NOTE_POPUP == null)
			initNodePopup();
		
		if(NOTE_POPUP.isShowing())
			hideNotePopup();
		
		NOTEING_BLOCK = block;
		NOTE_TEXT_AREA.setText(block.getNote());
		Point2D pos = FXHelper.getScreenPos(block);
		NOTE_POPUP.show(block, pos.getX(), pos.getY()-NOTE_POPUP.getHeight());
	}
	
	public static void hideNotePopup(){
		if(NOTE_POPUP.isShowing())
			NOTE_POPUP.hide();
		
		if(NOTEING_BLOCK!=null)
			NOTEING_BLOCK.setNote(NOTE_TEXT_AREA.getText());
		
		NOTEING_BLOCK = null;
	}
	
	private static void initNodePopup(){
		NOTE_POPUP = new Popup();
		NOTE_POPUP.setAutoHide(true);
		NOTE_POPUP.setAutoFix(true);
		NOTE_POPUP.showingProperty().addListener(observable->{
			if(!NOTE_POPUP.isShowing())
				hideNotePopup();
		});
		
		NOTE_TEXT_AREA = new TextArea();
		NOTE_TEXT_AREA.setPrefSize(200, 100);
		NOTE_POPUP.getContent().add(NOTE_TEXT_AREA);
	}
}
