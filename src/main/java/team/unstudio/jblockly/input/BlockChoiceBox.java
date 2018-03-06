package team.unstudio.jblockly.input;

import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.scene.control.ChoiceBox;

public class BlockChoiceBox<T> extends ChoiceBox<T> implements IBlockInput<T>{

	private StringProperty name;
	@Override
	public StringProperty nameProperty() {
		if(name==null){
			name = new StringPropertyBase() {
				
				@Override
				public String getName() {
					return "name";
				}
				
				@Override
				public Object getBean() {
					return BlockChoiceBox.this;
				}
			};
		}
		return name;
	}
	@Override
	public String getName() {return name == null?"":name.get();}
	@Override
	public void setName(String name) {nameProperty().set(name);}
	
	private static final String DEFAULT_STYLE_CLASS = "block-choice-box";
	public BlockChoiceBox() {
		getStyleClass().addAll(DEFAULT_STYLE_CLASS);
	}
}
