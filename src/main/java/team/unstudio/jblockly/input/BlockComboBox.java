package team.unstudio.jblockly.input;

import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.scene.control.ComboBox;

public class BlockComboBox<T> extends ComboBox<T> implements IBlockInput<T>{
	
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
					return BlockComboBox.this;
				}
			};
		}
		return name;
	}
	@Override
	public String getName() {return name == null?"":name.get();}
	@Override
	public void setName(String name) {name().set(name);}
	
	private static final String DEFAULT_STYLE_CLASS = "block-combo-box";
	public BlockComboBox() {
		getStyleClass().addAll(DEFAULT_STYLE_CLASS);
	}
}
