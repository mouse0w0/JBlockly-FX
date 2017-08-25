package team.unstudio.jblockly.input;

import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.scene.control.TextField;

public class BlockTextField extends TextField implements IBlockInput<String>{

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
					return BlockTextField.this;
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
	public String getValue() {
		return getText();
	}

	@Override
	public void setValue(String value) {
		setText(value);
	}
	
	private static final String DEFAULT_STYLE_CLASS = "block-text-field";
	public BlockTextField() {
		getStyleClass().addAll(DEFAULT_STYLE_CLASS);
	}
}
