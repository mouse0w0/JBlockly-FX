package team.unstudio.jblockly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import team.unstudio.jblockly.BlockSlot.SlotType;

public final class Block extends Region {

	private final SVGPath svgPath;
	private final Map<String, Node> nodeNames = new HashMap<>();

	private BooleanProperty movable;

	public final BooleanProperty movableProperty() {
		if (movable == null) {
			movable = new BooleanPropertyBase(true) {

				@Override
				public String getName() {
					return "movable";
				}

				@Override
				public Object getBean() {
					return Block.this;
				}
			};
		}
		return movable;
	}

	public boolean isMovable() {
		return movableProperty().get();
	}

	public void setMovable(boolean movable) {
		movableProperty().set(movable);
	}

	private double tempOldX, tempOldY;
	private boolean performingLayout, moving;
	private double[][] tempArray;

	private static final String MARGIN_CONSTRAINT = "block-margin";

	private static void setConstraint(Node node, Object key, Object value) {
		if (value == null) {
			node.getProperties().remove(key);
		} else {
			node.getProperties().put(key, value);
		}
		if (node.getParent() != null) {
			node.getParent().requestLayout();
		}
	}

	private static Object getConstraint(Node node, Object key) {
		if (node.hasProperties()) {
			Object value = node.getProperties().get(key);
			if (value != null) {
				return value;
			}
		}
		return null;
	}

	public static void setMargin(Node child, Insets value) {
		setConstraint(child, MARGIN_CONSTRAINT, value);
	}

	public static Insets getMargin(Node child) {
		return (Insets) getConstraint(child, MARGIN_CONSTRAINT);
	}

	public enum ConnectionType {
		LEFT, TOP, BUTTOM, TOPANDBUTTOM, NONE
	}

	private ConnectionType connectionType = ConnectionType.NONE;

	public ConnectionType getConnectionType() {
		return connectionType;
	}

	public void setConnectionType(ConnectionType connectionType) {
		this.connectionType = connectionType;
	}

	public Block() {
		svgPath = new SVGPath();
		svgPath.setFill(Color.GRAY);
		getChildren().add(svgPath);

		setOnMousePressed(event -> {
			if (!isMovable())
				return;

			Parent parent = getParent();
			if (parent instanceof BlockSlot) {
				BlockWorkspace.addBlockToWorkspace(this);
				((BlockSlot) parent).validateBlock();
			}

			tempOldX = event.getSceneX() - getLayoutX();
			tempOldY = event.getSceneY() - getLayoutY();

			moving = true;
		});
		setOnMouseDragged(event -> {
			if (!moving)
				return;
			setLayoutX(event.getSceneX() - tempOldX);
			setLayoutY(event.getSceneY() - tempOldY);
		});
		setOnMouseReleased(event -> {
			moving = false;
		});

		setPickOnBounds(false); // 启用不规则图形判断,具体见contain方法
	}

	public BlockWorkspace getWorkspace() {
		Parent parent = getParent();
		if (parent instanceof BlockSlot)
			return ((BlockSlot) parent).getWorkspace();
		else if (parent instanceof BlockWorkspace)
			return (BlockWorkspace) parent;
		else
			return null;
	}

	public Set<String> getNodeNames() {
		return nodeNames.keySet();
	}

	public Node getNode(String name) {
		return nodeNames.get(name);
	}

	public void addNode(String name, Node node) {
		if (nodeNames.containsKey(name)) {
			return;
		}
		nodeNames.put(name, node);
		getChildren().add(node);
	}

	public void addNode(String name, Node node, int index) {
		if (nodeNames.containsKey(name)) {
			return;
		}
		nodeNames.put(name, node);
		getChildren().add(index, node);
	}

	public void addNode(Node node) {
		getChildren().add(node);
	}

	public void addNode(Node node, int index) {
		getChildren().add(index, node);
	}

	public void removeNode(Node node) {
		getChildren().remove(node);
	}

	public String getNodeName(Node node) {
		for (Entry<String, Node> entry : nodeNames.entrySet())
			if (entry.getValue().equals(node))
				return entry.getKey();
		return null;
	}

	public void removeNode(String name) {
		if (!nodeNames.containsKey(name)) {
			return;
		}
		getChildren().remove(nodeNames.get(name));
		nodeNames.remove(name);
	}

	public boolean containNodeName(String name) {
		return nodeNames.containsKey(name);
	}

	public boolean containNode(Node node) {
		return getChildren().contains(node);
	}

	@Override
	public ObservableList<Node> getChildren() {
		return super.getChildren();
	}

	@Override
	protected double computeMinWidth(double height) {
		return svgPath.minWidth(height);
	}

	@Override
	protected double computeMinHeight(double width) {
		return svgPath.minHeight(width);
	}

	@Override
	protected double computePrefWidth(double height) {
		return svgPath.prefWidth(height);
	}

	@Override
	protected double computePrefHeight(double width) {
		return svgPath.prefHeight(width);
	}

	@Override
	public boolean contains(double localX, double localY) {
		return svgPath.contains(localX, localY);
	}

	@Override
	public boolean contains(Point2D localPoint) {
		return svgPath.contains(localPoint);
	}

	@Override
	public void requestLayout() {
		if (performingLayout) {
			return;
		}
		super.requestLayout();
	}

	@Override
	protected void layoutChildren() {
		if (performingLayout) {
			return;
		}
		performingLayout = true;

		layoutInArea(svgPath, 0, 0, svgPath.getLayoutBounds().getWidth(), svgPath.getLayoutBounds().getHeight(), 0,
				HPos.CENTER, VPos.CENTER);

		List<Node> managed = new ArrayList<>(getManagedChildren());
		managed.remove(svgPath);

		Insets insets = getInsets();
		double width = getWidth();
		double height = getHeight();
		double top = snapSpace(insets.getTop());
		double left = snapSpace(insets.getLeft());
		double bottom = snapSpace(insets.getBottom());
		double right = snapSpace(insets.getRight());
		double space = 0;
		HPos hpos = HPos.LEFT;
		VPos vpos = VPos.TOP;

		double[][] actualAreaBounds = getAreaBounds(managed, width, height, false);
		List<BlockSlot> slots = getLineBounds(managed, actualAreaBounds, space);

		svgPath.setContent(getSVGPath(slots));

		double x = left;
		double y = top;

		for (int i = 0, size = slots.size(); i < size; i++) {
			BlockSlot child = slots.get(i);
			layoutLine(child, managed, actualAreaBounds, x, y, space, hpos, vpos);
			y += child.getLineHeight();
		}

		performingLayout = false;
	}

	private void layoutLine(BlockSlot slot, List<Node> managed, double[][] actualAreaBounds, double left, double top,
			double space, HPos hpos, VPos vpos) {
		double x = left;
		double y = top;
		for (int i = slot.getFirstNode(), end = slot.getLastNode() - 1; i <= end; i++) {
			Node child = managed.get(i);
			layoutInArea(child, x, y, actualAreaBounds[0][i], slot.getLineHeight(), 0, getMargin(child), hpos, vpos);
			x += actualAreaBounds[0][i] + space;
		}
		layoutInArea(slot, x, y, slot.getWidth(), slot.getHeight(), 0, null, hpos, vpos);
	}

	private List<BlockSlot> getLineBounds(List<Node> managed, double[][] actualAreaBounds, double space) {
		List<BlockSlot> temp = new ArrayList<>();
		double tempWidth = 0, tempHeight = 0, tempMaxWidth = 0;
		int lastBranchOrNextBlock = -1, firstNode = 0;
		for (int i = 0, size = managed.size(); i < size; i++) {
			Node child = managed.get(i);
			if (child instanceof BlockSlot) {
				BlockSlot slot = (BlockSlot) child;

				slot.setFirstNode(firstNode);
				slot.setLastNode(i);
				firstNode = i + 1;

				if (tempHeight < actualAreaBounds[1][i])
					tempHeight = actualAreaBounds[1][i];

				slot.setLineWidth(tempWidth);
				slot.setLineHeight(tempHeight);

				temp.add(slot);

				int tsize = temp.size();
				if (slot.getSlotType() == SlotType.BRANCH || slot.getSlotType() == SlotType.NEXT) {
					if (tsize - lastBranchOrNextBlock != 1) // 行对齐
						replaceAllLineWidth(temp, lastBranchOrNextBlock + 1, tsize - 2, tempMaxWidth);
					lastBranchOrNextBlock = tsize - 1;
					tempMaxWidth = 0;
				} else {
					if (tempMaxWidth < tempWidth) // 求最大行宽
						tempMaxWidth = tempWidth;
					if (size - i == 1 && tsize - lastBranchOrNextBlock != 1)// 最后行对齐
						replaceAllLineWidth(temp, lastBranchOrNextBlock + 1, tsize - 1, tempMaxWidth);
				}

				tempWidth = 0;
				tempHeight = 0;
			} else {
				tempWidth += actualAreaBounds[0][i] + space;
				if (tempHeight < actualAreaBounds[1][i])
					tempHeight = actualAreaBounds[1][i];
			}
		}
		return temp;
	}

	private void replaceAllLineWidth(List<BlockSlot> managed, int start, int end, double width) {
		for (int i = start; i <= end; i++)
			managed.get(i).setLineWidth(width);
	}

	private double[][] getAreaBounds(List<Node> managed, double width, double height, boolean minimum) {
		double[][] temp = getTempArray(managed.size());
		final double insideWidth = width == -1 ? -1
				: width - snapSpace(getInsets().getLeft()) - snapSpace(getInsets().getRight());
		final double insideHeight = height == -1 ? -1
				: height - snapSpace(getInsets().getTop()) - snapSpace(getInsets().getBottom());
		for (int i = 0, size = managed.size(); i < size; i++) {
			Node child = managed.get(i);
			Insets margin = getMargin(child);
			if (minimum) {
				temp[0][i] = computeChildMinAreaWidth(child, -1, margin, insideHeight, false);
				temp[1][i] = computeChildMinAreaHeight(child, -1, margin, insideWidth);
			} else {
				temp[0][i] = computeChildPrefAreaWidth(child, -1, margin, insideHeight, false);
				temp[1][i] = computeChildPrefAreaHeight(child, -1, margin, insideWidth);
			}
		}
		return temp;
	}

	private double[][] getTempArray(int size) {
		if (tempArray == null) {
			tempArray = new double[2][size];
		} else if (tempArray[0].length < size) {
			tempArray = new double[2][Math.max(tempArray.length * 3, size)];
		}
		return tempArray;
	}

	private double computeChildMinAreaHeight(Node child, double minBaselineComplement, Insets margin, double width) {
		final boolean snap = isSnapToPixel();
		double top = margin != null ? snapSpace(margin.getTop(), snap) : 0;
		double bottom = margin != null ? snapSpace(margin.getBottom(), snap) : 0;

		double alt = -1;
		if (child.isResizable() && child.getContentBias() == Orientation.HORIZONTAL) { // height
																						// depends
																						// on
																						// width
			double left = margin != null ? snapSpace(margin.getLeft(), snap) : 0;
			double right = margin != null ? snapSpace(margin.getRight(), snap) : 0;
			alt = snapSize(width != -1 ? boundedSize(child.minWidth(-1), width - left - right, child.maxWidth(-1))
					: child.maxWidth(-1));
		}

		// For explanation, see computeChildPrefAreaHeight
		if (minBaselineComplement != -1) {
			double baseline = child.getBaselineOffset();
			if (child.isResizable() && baseline == BASELINE_OFFSET_SAME_AS_HEIGHT) {
				return top + snapSize(child.minHeight(alt)) + bottom + minBaselineComplement;
			} else {
				return baseline + minBaselineComplement;
			}
		} else {
			return top + snapSize(child.minHeight(alt)) + bottom;
		}
	}

	private double computeChildPrefAreaHeight(Node child, double prefBaselineComplement, Insets margin, double width) {
		final boolean snap = isSnapToPixel();
		double top = margin != null ? snapSpace(margin.getTop(), snap) : 0;
		double bottom = margin != null ? snapSpace(margin.getBottom(), snap) : 0;

		double alt = -1;
		if (child.isResizable() && child.getContentBias() == Orientation.HORIZONTAL) { // height
																						// depends
																						// on
																						// width
			double left = margin != null ? snapSpace(margin.getLeft(), snap) : 0;
			double right = margin != null ? snapSpace(margin.getRight(), snap) : 0;
			alt = snapSize(boundedSize(child.minWidth(-1), width != -1 ? width - left - right : child.prefWidth(-1),
					child.maxWidth(-1)));
		}

		if (prefBaselineComplement != -1) {
			double baseline = child.getBaselineOffset();
			if (child.isResizable() && baseline == BASELINE_OFFSET_SAME_AS_HEIGHT) {
				// When baseline is same as height, the preferred height of the
				// node will be above the baseline, so we need to add
				// the preferred complement to it
				return top + snapSize(boundedSize(child.minHeight(alt), child.prefHeight(alt), child.maxHeight(alt)))
						+ bottom + prefBaselineComplement;
			} else {
				// For all other Nodes, it's just their baseline and the
				// complement.
				// Note that the complement already contain the Node's preferred
				// (or fixed) height
				return top + baseline + prefBaselineComplement + bottom;
			}
		} else {
			return top + snapSize(boundedSize(child.minHeight(alt), child.prefHeight(alt), child.maxHeight(alt)))
					+ bottom;
		}
	}

	private double computeChildMinAreaWidth(Node child, double baselineComplement, Insets margin, double height,
			boolean fillHeight) {
		final boolean snap = isSnapToPixel();
		double left = margin != null ? snapSpace(margin.getLeft(), snap) : 0;
		double right = margin != null ? snapSpace(margin.getRight(), snap) : 0;
		double alt = -1;
		if (height != -1 && child.isResizable() && child.getContentBias() == Orientation.VERTICAL) { // width
																										// depends
																										// on
																										// height
			double top = margin != null ? snapSpace(margin.getTop(), snap) : 0;
			double bottom = (margin != null ? snapSpace(margin.getBottom(), snap) : 0);
			double bo = child.getBaselineOffset();
			final double contentHeight = bo == BASELINE_OFFSET_SAME_AS_HEIGHT && baselineComplement != -1
					? height - top - bottom - baselineComplement : height - top - bottom;
			if (fillHeight) {
				alt = snapSize(boundedSize(child.minHeight(-1), contentHeight, child.maxHeight(-1)));
			} else {
				alt = snapSize(boundedSize(child.minHeight(-1), child.prefHeight(-1),
						Math.min(child.maxHeight(-1), contentHeight)));
			}
		}
		return left + snapSize(child.minWidth(alt)) + right;
	}

	private double computeChildPrefAreaWidth(Node child, double baselineComplement, Insets margin, double height,
			boolean fillHeight) {
		final boolean snap = isSnapToPixel();
		double left = margin != null ? snapSpace(margin.getLeft(), snap) : 0;
		double right = margin != null ? snapSpace(margin.getRight(), snap) : 0;
		double alt = -1;
		if (height != -1 && child.isResizable() && child.getContentBias() == Orientation.VERTICAL) { // width
																										// depends
																										// on
																										// height
			double top = margin != null ? snapSpace(margin.getTop(), snap) : 0;
			double bottom = margin != null ? snapSpace(margin.getBottom(), snap) : 0;
			double bo = child.getBaselineOffset();
			final double contentHeight = bo == BASELINE_OFFSET_SAME_AS_HEIGHT && baselineComplement != -1
					? height - top - bottom - baselineComplement : height - top - bottom;
			if (fillHeight) {
				alt = snapSize(boundedSize(child.minHeight(-1), contentHeight, child.maxHeight(-1)));
			} else {
				alt = snapSize(boundedSize(child.minHeight(-1), child.prefHeight(-1),
						Math.min(child.maxHeight(-1), contentHeight)));
			}
		}
		return left + snapSize(boundedSize(child.minWidth(alt), child.prefWidth(alt), child.maxWidth(alt))) + right;
	}

	private static double boundedSize(double min, double pref, double max) {
		double a = pref >= min ? pref : min;
		double b = min >= max ? min : max;
		return a <= b ? a : b;
	}

	private static double snapSpace(double value, boolean snapToPixel) {
		return snapToPixel ? Math.round(value) : value;
	}

	/************************************************************
	 * * SVG Path * *
	 ************************************************************/

	private static final double INSERT_WIDTH = 5;
	private static final double INSERT_OFFSET_Y = 10;
	private static final double INSERT_HEIGHT = 10;
	private static final double NEXT_WIDTH = 10;
	private static final double NEXT_HEIGHT = 5;
	private static final double NEXT_OFFSET_X = 10;
	private static final double BRANCH_MIN_WIDTH = 10;

	private String getSVGPath(List<BlockSlot> slots) {
		if (slots.isEmpty())
			return "";

		ConnectionType connectionType = getConnectionType();
		StringBuilder builder = new StringBuilder();
		builder.append(getTopPath(connectionType, slots.get(0).getLineWidth()));
		double y = 0;
		for (int i = 0, size = slots.size(); i < size; i++) {
			BlockSlot slot = slots.get(i);
			switch (slot.getSlotType()) {
			case BRANCH:
				builder.append(getBranchPath(y, slot.getLineWidth(), slot.getLineHeight(),
						i + 1 == size ? slot.getLineWidth() : slots.get(i + 1).getLineWidth()));
				break;
			case INSERT:
				builder.append(getInsertPath(y, slot.getLineWidth()));
				break;
			default:
				break;
			}
			if (slot.getSlotType() != SlotType.NEXT)
				y += slot.getLineHeight();
		}
		builder.append(getBottomPath(connectionType, y));
		return builder.toString();
	}

	private String getTopPath(ConnectionType connectionType, double width) {
		switch (connectionType) {
		case TOP:
		case TOPANDBUTTOM:
			return new StringBuilder("M 0 0 H ").append(NEXT_OFFSET_X).append(" V ").append(NEXT_HEIGHT).append(" H ")
					.append(NEXT_OFFSET_X + NEXT_WIDTH).append(" V 0 H ").append(width).toString();
		case LEFT:
			return new StringBuilder("M ").append(INSERT_WIDTH).append(" ").append(INSERT_HEIGHT + INSERT_OFFSET_Y)
					.append(" H 0 V ").append(INSERT_HEIGHT).append(" H ").append(INSERT_WIDTH).append(" V 0 H ")
					.append(width).toString();
		default:
			return new StringBuilder("M 0 0 H ").append(width).toString();
		}
	}

	private String getBottomPath(ConnectionType connectionType, double y) {
		switch (connectionType) {
		case BUTTOM:
		case TOPANDBUTTOM:
			return new StringBuilder(" V ").append(y).append(" H 20 V ").append(y + 5).append(" H 10 V ").append(y)
					.append(" H 0 Z").toString();
		case LEFT:
			return new StringBuilder(" V ").append(y).append(" H ").append(INSERT_WIDTH).append(" Z").toString();
		default:
			return new StringBuilder(" V ").append(y).append(" H 0 Z").toString();
		}
	}

	private String getBranchPath(double y, double width, double height, double nextWidth) {
		return new StringBuilder(" V ").append(y).append(" H ").append(width + NEXT_OFFSET_X + NEXT_WIDTH).append(" V ")
				.append(y + NEXT_HEIGHT).append(" H ").append(width + NEXT_OFFSET_X).append(" V ").append(y)
				.append(" H ").append(width).append(" V ").append(y + height).append(" H ").append(nextWidth)
				.toString();
	}

	private String getInsertPath(double y, double width) {
		return new StringBuilder(" V ").append(y + INSERT_OFFSET_Y).append(" H ").append(width - INSERT_WIDTH)
				.append(" V ").append(y + INSERT_OFFSET_Y + INSERT_HEIGHT).append(" H ").append(width).toString();
	}
}
