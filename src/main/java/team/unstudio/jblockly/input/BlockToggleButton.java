package team.unstudio.jblockly.input;

import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.scene.control.ToggleButton;

public class BlockToggleButton extends ToggleButton implements IBlockInput<Boolean>{
	
	private StringProperty name;
	@Override
	public StringProperty name() {
		if(name==null){
			name = new StringPropertyBase() {
				
				@Override
				public String getName() {
					return "name";
				}
				
				@Override
				public Object getBean() {
					return BlockToggleButton.this;
				}
			};
		}
		return name;
	}
	@Override
	public String getName() {return name == null?"":name.get();}
	@Override
	public void setName(String name) {name().set(name);}

	@Override
	public Boolean getValue() {
		return isSelected();
	}

	@Override
	public void setValue(Boolean value) {
		setSelected(value);
	}

	private static final String DEFAULT_STYLE_CLASS = "block-toggle-button";
	public BlockToggleButton() {
		getStyleClass().addAll(DEFAULT_STYLE_CLASS);
	}
}
