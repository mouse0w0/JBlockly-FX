package team.unstudio.jblockly.util;

import team.unstudio.jblockly.BlockGlobal;
import team.unstudio.jblockly.ConnectionType;

public interface SVGPathHelper extends BlockGlobal{
	default String buildTopPath(ConnectionType connectionType, double width) {
		switch (connectionType) {
		case TOP:
		case TOPANDBOTTOM:
			return new StringBuilder("M 0 0 H ").append(NEXT_OFFSET_X).append(" V ").append(NEXT_HEIGHT).append(" H ")
					.append(NEXT_OFFSET_X + NEXT_WIDTH).append(" V 0 H ").append(width).toString();
		case LEFT:
			return new StringBuilder("M ").append(INSERT_WIDTH).append(" ").append(INSERT_HEIGHT + INSERT_OFFSET_Y)
					.append(" H 0 V ").append(INSERT_HEIGHT).append(" H ").append(INSERT_WIDTH).append(" V 0 H ")
					.append(INSERT_WIDTH+width).toString();
		default:
			return new StringBuilder("M 0 0 H ").append(width).toString();
		}
	}

	default String buildBottomPath(ConnectionType connectionType, double y) {
		switch (connectionType) {
		case BOTTOM:
		case TOPANDBOTTOM:
			return new StringBuilder(" V ").append(y).append(" H 20 V ").append(y + 5).append(" H 10 V ").append(y)
					.append(" H 0 Z").toString();
		case LEFT:
			return new StringBuilder(" V ").append(y).append(" H ").append(INSERT_WIDTH).append(" Z").toString();
		default:
			return new StringBuilder(" V ").append(y).append(" H 0 Z").toString();
		}
	}

	default String buildBranchPath(ConnectionType connectionType, double y, double width, double height, double nextWidth) {
		switch (connectionType) {
		case LEFT:
			return new StringBuilder(" V ").append(y)
					.append(" H ").append(INSERT_WIDTH + width + NEXT_OFFSET_X + NEXT_WIDTH)
					.append(" V ").append(y + NEXT_HEIGHT)
					.append(" H ").append(INSERT_WIDTH + width + NEXT_OFFSET_X)
					.append(" V ").append(y)
					.append(" H ").append(INSERT_WIDTH + width)
					.append(" V ").append(y + height - BRANCH_SLOT_BOTTOM_HEIGHT)
					.append(" H ").append(INSERT_WIDTH + nextWidth)
					.append(" V ").append(y + height)
					.toString();
		default:
			return new StringBuilder(" V ").append(y)
					.append(" H ").append(width + NEXT_OFFSET_X + NEXT_WIDTH)
					.append(" V ").append(y + NEXT_HEIGHT)
					.append(" H ").append(width + NEXT_OFFSET_X)
					.append(" V ").append(y)
					.append(" H ").append(width)
					.append(" V ").append(y + height - BRANCH_SLOT_BOTTOM_HEIGHT)
					.append(" H ").append(nextWidth)
					.append(" V ").append(y + height)
					.toString();
		}
	}

	default String buildInsertPath(ConnectionType connectionType, double y, double width) {
		switch (connectionType) {
		case LEFT:
			return new StringBuilder(" V ").append(y + INSERT_OFFSET_Y).append(" H ").append(INSERT_WIDTH+ width - INSERT_WIDTH)
					.append(" V ").append(y + INSERT_OFFSET_Y + INSERT_HEIGHT).append(" H ").append(INSERT_WIDTH+width).toString();
		default:
			return new StringBuilder(" V ").append(y + INSERT_OFFSET_Y).append(" H ").append(width - INSERT_WIDTH)
					.append(" V ").append(y + INSERT_OFFSET_Y + INSERT_HEIGHT).append(" H ").append(width).toString();
		}
	}
}
