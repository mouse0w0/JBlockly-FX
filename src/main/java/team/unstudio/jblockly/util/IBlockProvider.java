package team.unstudio.jblockly.util;

import team.unstudio.jblockly.Block;

public interface IBlockProvider {
	
	Block build();
	String getRegistyName();
}
