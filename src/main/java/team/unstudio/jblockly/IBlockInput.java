package team.unstudio.jblockly;

import javafx.beans.property.StringProperty;

public interface IBlockInput<V> {

	StringProperty name();
	String getName();
	void setName(String name);
	V getValue();
	void setValue(V value);
}
