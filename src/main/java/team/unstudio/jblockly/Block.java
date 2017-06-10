package team.unstudio.jblockly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.javafx.css.converters.BooleanConverter;
import com.sun.javafx.css.converters.EnumConverter;
import com.sun.javafx.css.converters.SizeConverter;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;

//TODO: Support event
public class Block extends Region implements BlockGlobal{
	
	private static final String MARGIN_CONSTRAINT = "block-margin";
	private static final String NAME_CONSTRAINT = "block-name";

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
	
	public static void setNodeName(Node child, String value) {
		setConstraint(child, NAME_CONSTRAINT, value);
	}

	public static String getNodeName(Node child) {
		return (String) getConstraint(child, NAME_CONSTRAINT);
	}

	private BooleanProperty movable;
	public final BooleanProperty movableProperty() {
		if (movable == null) {
			movable = new StyleableBooleanProperty(true) {
				
				@Override
				public String getName() {
					return "movable";
				}

				@Override
				public Object getBean() {
					return Block.this;
				}
				
				@Override
				public CssMetaData<? extends Styleable, Boolean> getCssMetaData() {
					return StyleableProperties.MOVABLE;
				}
			};
		}
		return movable;
	}
	public final boolean isMovable() {return movable==null?true:movableProperty().get();}
	public final void setMovable(boolean value) {movableProperty().set(value);}
	
	private BooleanProperty folded; //TODO:
	public final BooleanProperty foldedProperty(){
		if(folded==null){
			folded = new StyleableBooleanProperty() {
				
				@Override
				public String getName() {
					return "folded";
				}
				
				@Override
				public Object getBean() {
					return Block.this;
				}
				
				@Override
				public CssMetaData<? extends Styleable, Boolean> getCssMetaData() {
					return StyleableProperties.FOLDED;
				}
			};
		}
		return folded;
	}
	public final boolean isFolded(){return folded==null?false:foldedProperty().get();}
	public final void setFolded(boolean value){foldedProperty().set(value);}
	
	private StringProperty note;
	public final StringProperty noteProperty(){
		if(note==null){
			note = new StringPropertyBase("") {
				
				@Override
				public String getName() {
					return "note";
				}
				
				@Override
				public Object getBean() {
					return Block.this;
				}
			};
		}
		return note;
	}
	public final String getNote(){return note==null?"":note.get();}
	public final void setNote(String value){noteProperty().set(value);}
	
	private DoubleProperty vSpacing;
	public final DoubleProperty vSpacingProperty(){
		if(vSpacing == null){
			vSpacing = new StyleableDoubleProperty() {
				
				@Override
				public void invalidated() {
					requestLayout();
				}
				
                @Override
                public CssMetaData<Block, Number> getCssMetaData() {
                    return StyleableProperties.V_SPACING;
                }
				
				@Override
				public String getName() {
					return "vSpacing";
				}
				
				@Override
				public Object getBean() {
					return Block.this;
				}
			};
		}
		return vSpacing;
	}
	public final double getVSpacing(){return vSpacing==null?0:vSpacing.get();}
	public final void setVSpacing(double value){vSpacingProperty().set(value);}
	
	private DoubleProperty hSpacing;
	public final DoubleProperty hSpacingProperty(){
		if(hSpacing == null){
			hSpacing = new StyleableDoubleProperty() {
				
				@Override
				public void invalidated() {
					requestLayout();
				}
				
                @Override
                public CssMetaData<Block, Number> getCssMetaData() {
                    return StyleableProperties.H_SPACING;
                }
				
				@Override
				public String getName() {
					return "hSpacing";
				}
				
				@Override
				public Object getBean() {
					return Block.this;
				}
			};
		}
		return hSpacing;
	}
	public final double getHSpacing(){return hSpacing==null?0:hSpacing.get();}
	public final void setHSpacing(double value){hSpacingProperty().set(value);}
	
	private ObjectProperty<Pos> alignment;
    public final ObjectProperty<Pos> alignmentProperty() {
        if (alignment == null) {
            alignment = new StyleableObjectProperty<Pos>(Pos.TOP_LEFT) {
                @Override
                public void invalidated() {
                    requestLayout();
                }

                @Override
                public CssMetaData<Block, Pos> getCssMetaData() {
                    return StyleableProperties.ALIGNMENT;
                }

                @Override
                public Object getBean() {
                    return Block.this;
                }

                @Override
                public String getName() {
                    return "alignment";
                }
            };
        }
        return alignment;
    }
    public final void setAlignment(Pos value) { alignmentProperty().set(value); }
    public final Pos getAlignment() { return alignment == null ? Pos.TOP_LEFT : alignment.get(); }
    private Pos getAlignmentInternal() {
        Pos localPos = getAlignment();
        return localPos == null ? Pos.TOP_LEFT : localPos;
    }

	private ReadOnlyBooleanWrapper moving;
	private final ReadOnlyBooleanWrapper movingPropertyImpl(){
		if(moving==null){
			moving = new ReadOnlyBooleanWrapper(this, "moving");
		}
		return moving;
	}
	private final void setMoving(boolean moving){movingPropertyImpl().set(moving);}
	public final boolean isMoving(){return moving==null?false:moving.get();}
	public final ReadOnlyBooleanProperty movingProperty(){return movingPropertyImpl().getReadOnlyProperty();}
	
	private ObjectProperty<ConnectionType> connectionType;
	public final ObjectProperty<ConnectionType> connectionTypeProperty(){
		if(connectionType==null){
			connectionType = new StyleableObjectProperty<ConnectionType>() {
                @Override
                public void invalidated() {
                    requestLayout();
                }
                
				@Override
				public CssMetaData<? extends Styleable, ConnectionType> getCssMetaData() {
					return StyleableProperties.CONNECTION;
				}

				@Override
				public Object getBean() {
					return Block.this;
				}

				@Override
				public String getName() {
					return "connection";
				}
			};
		}
		return connectionType;
	}
	public ConnectionType getConnectionType() {return connectionType==null?ConnectionType.NONE:connectionTypeProperty().get();}
	public void setConnectionType(ConnectionType value) {connectionTypeProperty().set(value);}
    private ConnectionType getConnectionTypeInternal() {
        ConnectionType local = getConnectionType();
        return local == null ? ConnectionType.NONE : local;
    }
	
	private final SVGPath svgPath;
	public final SVGPath getSVGPath(){return svgPath;}
	
	public final ObjectProperty<Paint> fillProperty() {//TODO:Support CSS
		return svgPath.fillProperty();
	}
	
	public final Paint getFill(){
		return svgPath.getFill();
	}
	
	public final void setFill(Paint value){
		svgPath.setFill(value);
	}
	
	public final ObjectProperty<Paint> strokeProperty() {//TODO:Support CSS
		return svgPath.strokeProperty();
	}
	
	public final Paint getStroke(){
		return svgPath.getStroke();
	}
	
	public final void setStroke(Paint value){
		svgPath.setStroke(value);
	}
	
	private double tempOldX, tempOldY;
	private boolean performingLayout;
	private double[][] tempArray;
	private List<BlockSlot> tempList;
	private Map<String, Node> tempNameToNode;

	public Block() {
		getStyleClass().setAll("block");
		
		svgPath = new SVGPath();
		getChildren().add(svgPath);

		setOnMousePressed(event -> {
			if (!isMovable())
				return;

			addToWorkspace();

			tempOldX = event.getSceneX() - getLayoutX();
			tempOldY = event.getSceneY() - getLayoutY();
			Parent parent = getParent();
			while(parent!=null){
				tempOldX-=parent.getLayoutX();
				tempOldX-=parent.getLayoutY();
				parent = parent.getParent();
			}
			
			setMoving(true);
			
			event.consume();
		});
		setOnMouseDragged(event -> {
			if (!isMoving())
				return;
			setLayoutX(event.getSceneX() - tempOldX);
			setLayoutY(event.getSceneY() - tempOldY);
			
			event.consume();
		});
		setOnMouseReleased(event -> {
			setMoving(false);
			
			double x = event.getSceneX()- tempOldX, y = event.getSceneY()- tempOldY;
			switch(this.getConnectionTypeInternal()){
				case TOP:
				case TOPANDBOTTOM:
					getWorkspace().tryLinkBlock(this,x+NEXT_OFFSET_X+NEXT_WIDTH/2,y-2.5);
					break;
				case LEFT:
					getWorkspace().tryLinkBlock(this,x-2.5,y+INSERT_OFFSET_Y+INSERT_HEIGHT/2);
					break;
				default:
					break;
			}

			event.consume();
		});

		setPickOnBounds(false); // 启用不规则图形判断,具体见contains方法
		
		//临时设置
		setFill(Color.GRAY);
		setStroke(Color.BLACK);
		setVSpacing(5);
		setHSpacing(5);
	}
	
	public final BlockWorkspace getWorkspace() {
		Parent parent = getParent();
		if (parent instanceof BlockSlot)
			return ((BlockSlot) parent).getWorkspace();
		else if (parent instanceof BlockWorkspace)
			return (BlockWorkspace) parent;
		else
			return null;
	}
	
	public void addToWorkspace(){
		Parent oldParent = getParent();
		if (oldParent == null)
			return;
		if (oldParent instanceof BlockWorkspace)
			return;

		Parent parent = getParent();
		double x = getLayoutX(), y = getLayoutY();
		while (!(parent instanceof BlockWorkspace)) {
			x += parent.getLayoutX();
			y += parent.getLayoutY();
			parent = parent.getParent();
		}

		((BlockWorkspace) parent).getChildren().add(this);
		setLayoutX(x);
		setLayoutY(y);
	}
	
	public void removeBlock(){
		getWorkspace().removeBlock(this);
	}
	
	public boolean tryLinkBlock(Block block,double x,double y){
		if(block == this)
			return false;
		
		if(!getLayoutBounds().contains(x, y))
			return false;
		
		for(BlockSlot slot:tempList)
			if(slot.tryLinkBlock(block, x-slot.getLayoutX(), y-slot.getLayoutY()))
				return true;
		
		return false;
	}
	
	public boolean isCanBeLinked(BlockSlot slot){
		return true;
	}

	public Set<String> getNodeNames() {
		return getNameToNode().keySet();
	}
	
	public Map<String,Node> getNameToNode() {
		if(tempNameToNode==null)
			tempNameToNode = new HashMap<>();
		
		tempNameToNode.clear();
		for(Node node:getChildren()){
			String name = getNodeName(node);
			if(name!=null)
				tempNameToNode.put(name, node);
		}
		return Collections.unmodifiableMap(tempNameToNode);
	}

	public Node getNode(String name) {
		return getNameToNode().get(name);
	}

	public void addNode(String name, Node node) {
		if (getNameToNode().containsKey(name)) {
			return;
		}
		setNodeName(node, name);
		getChildren().add(node);
	}

	public void removeNode(String name) {
		if (!getNameToNode().containsKey(name)) {
			return;
		}
		getChildren().remove(getNameToNode().get(name));
	}

	public boolean containNodeName(String name) {
		return getNameToNode().containsKey(name);
	}

	@Override
	public ObservableList<Node> getChildren() {
		return super.getChildren();
	}

	@Override
	protected double computePrefWidth(double height) {
		List<Node> managed = new ArrayList<>(getManagedChildren());
		managed.remove(svgPath);
		
		double vSpace = getVSpacing();
		double hSpace = getHSpacing();
		double[][] actualAreaBounds = getTempArray(managed.size());;
		List<BlockSlot> slots = getLineBounds(managed, vSpace, hSpace, actualAreaBounds);
		
		if(slots.isEmpty())
			return 0;
		
		double width = 0;
		for(BlockSlot slot:slots){
			double lineWidth = slot.getOriginalLineWidth()+actualAreaBounds[0][slot.getLastNode()];
			if(width<lineWidth)
				width = lineWidth;
		}
		
		return width;
	}
	
	@Override
	protected double computePrefHeight(double width) {
		List<Node> managed = new ArrayList<>(getManagedChildren());
		managed.remove(svgPath);
		
		double vSpace = getVSpacing();
		double hSpace = getHSpacing();
		double[][] actualAreaBounds = getTempArray(managed.size());;
		List<BlockSlot> slots = getLineBounds(managed, vSpace, hSpace, actualAreaBounds);
		
		if(slots.isEmpty())
			return 0;
		
		double height = -vSpace;
		for(BlockSlot slot:slots)
			height += slot.getLineHeight() + vSpace;
		
		return height;
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
		if (performingLayout) 
			return;
		performingLayout = true;

		List<Node> managed = new ArrayList<>(getManagedChildren());
		managed.remove(svgPath);

		double vSpace = getVSpacing();
		double hSpace = getHSpacing();
		Pos pos = getAlignmentInternal();
		VPos vpos = pos.getVpos();
		HPos hpos = pos.getHpos();

		double[][] actualAreaBounds = getTempArray(managed.size());;
		List<BlockSlot> slots = getLineBounds(managed, vSpace, hSpace, actualAreaBounds);

		if (!slots.isEmpty()){
			ConnectionType connectionType = getConnectionTypeInternal();
			StringBuilder builder = new StringBuilder(getTopPath(connectionType, slots.get(0).getLineWidth()));
			double x = connectionType==ConnectionType.LEFT?INSERT_WIDTH:0;
			double y = 0;
	
			label1: for (int i = 0, size = slots.size(); i < size; i++) {
				BlockSlot slot = slots.get(i);
				SlotType slotType = slot.getSlotType();
				
				layoutLine(slot, managed, actualAreaBounds, x, y, vSpace, hSpace, hpos, vpos);
				
				switch (slotType) {
				case BRANCH:
					builder.append(getBranchPath(connectionType,y, slot.getLineWidth(), slot.getLineHeight(),
							i + 1 == size ? slot.getLineWidth() : slots.get(i + 1).getLineWidth()));
					break;
				case INSERT:
					builder.append(getInsertPath(connectionType,y, slot.getLineWidth()));
					break;
				case NEXT:
					break label1;
				default:
					break;
				}
				
				y += slot.getLineHeight() + vSpace;
			}
			
			builder.append(getBottomPath(connectionType, y));
			svgPath.setContent(builder.toString());
		}else
			svgPath.setContent("");
		
		layoutInArea(svgPath, 0, 0, svgPath.prefWidth(-1), svgPath.prefHeight(-1), 0, HPos.CENTER, VPos.CENTER);

		performingLayout = false;
	}

	private void layoutLine(BlockSlot slot, List<Node> managed, double[][] actualAreaBounds, double left, double top,
			double vSpace,double hSpace, HPos hpos, VPos vpos) {
		double x = left + hSpace;
		double y = top + vSpace;
		
		for (int i = slot.getFirstNode(), end = slot.getLastNode() - 1; i <= end; i++) {
			Node child = managed.get(i);
			layoutInArea(child, x, y, actualAreaBounds[0][i], slot.getLineHeight(), 0, getMargin(child), hpos, vpos);
			x += actualAreaBounds[0][i] + hSpace;
		}
		
		layoutInArea(slot, slot.getLayoutLineWidth(), top, slot.getWidth(), slot.getHeight(), 0, null, hpos, vpos);
	}

	private List<BlockSlot> getLineBounds(List<Node> managed, double vSpace, double hSpace, double[][] actualAreaBounds) {
		List<BlockSlot> temp = getTempList();
		
		double tempWidth = hSpace, tempHeight = 0, tempMaxWidth = 0;
		int lastBranchOrNextBlock = -1, firstNode = 0;
		
		for (int i = 0, size = managed.size(); i < size; i++) {
			Node child = managed.get(i);
			
			//计算Node宽高
			Insets margin = getMargin(child);
			actualAreaBounds[0][i] = computeChildPrefAreaWidth(child, -1, margin, -1, false);
			actualAreaBounds[1][i] = computeChildPrefAreaHeight(child, -1, margin, -1);
			
			if (tempHeight < actualAreaBounds[1][i])
				tempHeight = actualAreaBounds[1][i];
			
			if (child instanceof BlockSlot) {
				BlockSlot slot = (BlockSlot) child;
				SlotType slotType = slot.getSlotType();
				temp.add(slot);

				if(slotType == SlotType.NEXT)
					tempWidth -= hSpace;
				
				slot.setLineWidth(tempWidth);
				slot.setLineHeight(tempHeight);
				slot.setFirstNode(firstNode);
				slot.setLastNode(i);

				int tsize = temp.size();
				if (slotType == SlotType.BRANCH || slotType == SlotType.NEXT) {
					lineAlignment(temp, lastBranchOrNextBlock + 1, tsize - 2, tempMaxWidth);
					lastBranchOrNextBlock = tsize - 1;
					tempMaxWidth = 0;
				} else {
					if (tempMaxWidth < tempWidth) // 求最大行宽
						tempMaxWidth = tempWidth;
					
					if (size - i == 1) // 最后行对齐
						lineAlignment(temp, lastBranchOrNextBlock + 1, tsize - 1, tempMaxWidth);
				}

				firstNode = i + 1;
				tempWidth = hSpace;
				tempHeight = 0;
			} else {
				tempWidth += actualAreaBounds[0][i] + hSpace;
			}
		}
		return temp;
	}
	
	private void lineAlignment(List<BlockSlot> managed, int start, int end, double width) {
		if(start==end)
			return;
		
		for (int i = start; i <= end; i++)
			managed.get(i).setLineWidth(width);
	}
	
	private List<BlockSlot> getTempList(){
		if(tempList == null)
			tempList = new ArrayList<>();
		tempList.clear();
		return tempList;
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

	private String getTopPath(ConnectionType connectionType, double width) {
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

	private String getBottomPath(ConnectionType connectionType, double y) {
		switch (connectionType) {
		case BUTTOM:
		case TOPANDBOTTOM:
			return new StringBuilder(" V ").append(y).append(" H 20 V ").append(y + 5).append(" H 10 V ").append(y)
					.append(" H 0 Z").toString();
		case LEFT:
			return new StringBuilder(" V ").append(y).append(" H ").append(INSERT_WIDTH).append(" Z").toString();
		default:
			return new StringBuilder(" V ").append(y).append(" H 0 Z").toString();
		}
	}

	private String getBranchPath(ConnectionType connectionType, double y, double width, double height, double nextWidth) {
		switch (connectionType) {
		case LEFT:
			return new StringBuilder(" V ").append(y).append(" H ").append(INSERT_WIDTH+width + NEXT_OFFSET_X + NEXT_WIDTH).append(" V ")
				.append(y + NEXT_HEIGHT).append(" H ").append(INSERT_WIDTH+width + NEXT_OFFSET_X).append(" V ").append(y)
				.append(" H ").append(INSERT_WIDTH+width).append(" V ").append(y + height).append(" H ").append(INSERT_WIDTH+nextWidth)
				.toString();
		default:
			return new StringBuilder(" V ").append(y).append(" H ").append(width + NEXT_OFFSET_X + NEXT_WIDTH).append(" V ")
					.append(y + NEXT_HEIGHT).append(" H ").append(width + NEXT_OFFSET_X).append(" V ").append(y)
					.append(" H ").append(width).append(" V ").append(y + height).append(" H ").append(nextWidth)
					.toString();
		}
	}

	private String getInsertPath(ConnectionType connectionType, double y, double width) {
		switch (connectionType) {
		case LEFT:
			return new StringBuilder(" V ").append(y + INSERT_OFFSET_Y).append(" H ").append(INSERT_WIDTH+width - INSERT_WIDTH)
					.append(" V ").append(y + INSERT_OFFSET_Y + INSERT_HEIGHT).append(" H ").append(INSERT_WIDTH+width).toString();
		default:
			return new StringBuilder(" V ").append(y + INSERT_OFFSET_Y).append(" H ").append(width - INSERT_WIDTH)
					.append(" V ").append(y + INSERT_OFFSET_Y + INSERT_HEIGHT).append(" H ").append(width).toString();
		}
	}
	
    /***************************************************************************
     *                                                                         *
     *                         Stylesheet Handling                             *
     *                                                                         *
     **************************************************************************/

     private static class StyleableProperties {

         private static final CssMetaData<Block,Pos> ALIGNMENT =
             new CssMetaData<Block,Pos>("-fx-alignment",
                 new EnumConverter<Pos>(Pos.class),
                 Pos.TOP_LEFT) {

            @Override
            public boolean isSettable(Block node) {
                return node.alignment == null || !node.alignment.isBound();
            }

            @Override
            public StyleableProperty<Pos> getStyleableProperty(Block node) {
                return (StyleableProperty<Pos>)node.alignmentProperty();
            }
         };
         
         private static final CssMetaData<Block,ConnectionType> CONNECTION =
                 new CssMetaData<Block,ConnectionType>("-fx-block-connection",
                     new EnumConverter<ConnectionType>(ConnectionType.class),
                     ConnectionType.NONE) {

                @Override
                public boolean isSettable(Block node) {
                    return node.connectionType == null || !node.connectionType.isBound();
                }

                @Override
                public StyleableProperty<ConnectionType> getStyleableProperty(Block node) {
                    return (StyleableProperty<ConnectionType>)node.connectionTypeProperty();
                }
             };

         private static final CssMetaData<Block,Number> V_SPACING =
             new CssMetaData<Block,Number>("-fx-vSpacing",
                 SizeConverter.getInstance(), 0.0){

            @Override
            public boolean isSettable(Block node) {
                return node.vSpacing == null || !node.vSpacing.isBound();
            }

            @Override
            public StyleableProperty<Number> getStyleableProperty(Block node) {
                return (StyleableProperty<Number>)node.vSpacingProperty();
            }
         };
         
         private static final CssMetaData<Block,Number> H_SPACING =
                 new CssMetaData<Block,Number>("-fx-hSpacing",
                     SizeConverter.getInstance(), 0.0){

                @Override
                public boolean isSettable(Block node) {
                    return node.hSpacing == null || !node.hSpacing.isBound();
                }

                @Override
                public StyleableProperty<Number> getStyleableProperty(Block node) {
                    return (StyleableProperty<Number>)node.hSpacingProperty();
                }
             };
             
         private static final CssMetaData<Block, Boolean> MOVABLE = 
        		 new CssMetaData<Block, Boolean>("-fx-block-movable",
        				 BooleanConverter.getInstance(), true) {
					
					@Override
					public boolean isSettable(Block styleable) {
						return styleable.movable == null || !styleable.movable.isBound();
					}
					
					@Override
					public StyleableProperty<Boolean> getStyleableProperty(Block styleable) {
						return (StyleableProperty<Boolean>)styleable.movableProperty();
					}
				};
				
		private static final CssMetaData<Block, Boolean> FOLDED = 
				new CssMetaData<Block, Boolean>("-fx-block-folded",
				BooleanConverter.getInstance(), true) {

			@Override
			public boolean isSettable(Block styleable) {
				return styleable.folded == null || !styleable.folded.isBound();
			}

			@Override
			public StyleableProperty<Boolean> getStyleableProperty(Block styleable) {
				return (StyleableProperty<Boolean>) styleable.foldedProperty();
			}
		};
         
         private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
         static {
            final List<CssMetaData<? extends Styleable, ?>> styleables =
                new ArrayList<CssMetaData<? extends Styleable, ?>>(Region.getClassCssMetaData());
            styleables.add(ALIGNMENT);
            styleables.add(CONNECTION);
            styleables.add(V_SPACING);
            styleables.add(H_SPACING);
            styleables.add(MOVABLE);
            styleables.add(FOLDED);
            STYLEABLES = Collections.unmodifiableList(styleables);
         }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }
}