package team.unstudio.jblockly.input;

import javafx.beans.property.StringProperty;

public interface IBlockInput<V> {

	StringProperty nameProperty();
	String getName();
	void setName(String name);
	V getValue();
	void setValue(V value);
}
