package team.unstudio.jblockly.component;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class BlockButton extends Button{
	
	public BlockButton() {
        initialize();
    }

    public BlockButton(String text) {
        super(text);
        initialize();
    }

    public BlockButton(String text, Node graphic) {
        super(text, graphic);
        initialize();
    }

    private void initialize() {
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);
        
		setPrefSize(20, 20);
		setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
		setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
		setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(3), BorderWidths.DEFAULT)));
    }

    private static final String DEFAULT_STYLE_CLASS = "block-button";
}
