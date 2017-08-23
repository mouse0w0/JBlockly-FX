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
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.PathIterator;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.shape.FillRule;
import javafx.scene.shape.SVGPath;
import team.unstudio.jblockly.util.SVGPathHelper;

//TODO: Support event
public class Block extends Region implements IBlockly,SVGPathHelper{
	
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

	private StyleableBooleanProperty movable;
	public final StyleableBooleanProperty movableProperty() {
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
	
	private StyleableDoubleProperty vSpacing;
	public final StyleableDoubleProperty vSpacingProperty(){
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
	
	private StyleableDoubleProperty hSpacing;
	public final StyleableDoubleProperty hSpacingProperty(){
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
	
	private StyleableObjectProperty<Pos> alignment;
    public final StyleableObjectProperty<Pos> alignmentProperty() {
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
	
	private StyleableObjectProperty<ConnectionType> connectionType;
	public final StyleableObjectProperty<ConnectionType> connectionTypeProperty(){
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
	
	private final SVGPath svgPath = new SVGPath();
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
	
	private ReadOnlyObjectWrapper<BlockWorkspace> workspace;
	private final ReadOnlyObjectWrapper<BlockWorkspace> workspacePropertyImpl(){
		if(workspace==null){
			workspace = new ReadOnlyObjectWrapper<BlockWorkspace>(this, "workspace");
		}
		return workspace;
	}
	private final void setWorkspace(BlockWorkspace workspace){workspacePropertyImpl().set(workspace);}
	public final BlockWorkspace getWorkspace(){return workspace==null?null:workspace.get();}
	public final ReadOnlyObjectProperty<BlockWorkspace> workspaceProperty(){return workspacePropertyImpl().getReadOnlyProperty();}
	private final ChangeListener<BlockWorkspace> workspaceListener = new ChangeListener<BlockWorkspace>() {
		
		@Override
		public void changed(ObservableValue<? extends BlockWorkspace> observable, BlockWorkspace oldValue,
				BlockWorkspace newValue) {
			setWorkspace(newValue);
		}
	};
	
	private double tempOldX, tempOldY;
	private boolean performingLayout;
	private double[][] tempArray;
	private StringBuilder tempStringBuilder;
	
	private List<BlockSlot> cacheSlots;
	private Map<String, Node> cacheNameToNode;
	
	public Block() {
		getStyleClass().add("block");
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
			
			if(isConnectable())
				getWorkspace().tryConnectBlock(this,getConnectLocation());
			
			event.consume();
		});
		
		parentProperty().addListener((observable, oldValue, newValue)->{
			if(oldValue instanceof IBlockly){
				((IBlockly) oldValue).workspaceProperty().removeListener(workspaceListener);
			}
			if(newValue instanceof IBlockly){
				IBlockly blockly = (IBlockly) newValue;
				setWorkspace(blockly.getWorkspace());
				blockly.workspaceProperty().addListener(workspaceListener);
			}else{
				setWorkspace(null);
			}
		});

		setPickOnBounds(false); // 启用不规则图形判断,具体见contains方法
		
		//默认设置
		setFill(Color.GRAY);
		setStroke(Color.BLACK);
		setVSpacing(5);
		setHSpacing(5);
	}
	
	public void addToWorkspace() {
		Parent oldParent = getParent();
		if (oldParent == null) {
			return;
		} else if (oldParent instanceof BlockWorkspace) {
			toFront();
		} else if (oldParent instanceof BlockSlot) {
			Parent parent = getParent();
			double x = getLayoutX(), y = getLayoutY();
			while (parent!=null&&parent!=getWorkspace()) {
				x += parent.getLayoutX();
				y += parent.getLayoutY();
				parent = parent.getParent();
			}

			((BlockWorkspace) parent).getChildren().add(this);
			setLayoutX(x);
			setLayoutY(y);
		}
	}
	
	public void removeBlock(){
		getWorkspace().removeBlock(this);
	}
	
	public boolean tryConnectBlock(Block block,double x,double y){
		if(block == this)
			return false;
		
		if(!getLayoutBounds().contains(x, y))
			return false;
		
		for(BlockSlot slot:cacheSlots)
			if(slot.tryConnectBlock(block, x-slot.getLayoutX(), y-slot.getLayoutY()))
				return true;
		
		return false;
	}
	
	public boolean isConnectable(){
		return getConnectionTypeInternal().isConnectable();
	}
	
	public boolean isConnectable(BlockSlot slot){
		return isConnectable();
	}
	
	protected Point2D getConnectLocation(){
		final double x=getLayoutX(), y=getLayoutY();
		switch(this.getConnectionTypeInternal()){
		case TOP:
		case TOPANDBOTTOM:
			return new Point2D(x+NEXT_OFFSET_X+NEXT_WIDTH/2,y-2.5);
		case LEFT:
			return new Point2D(x-2.5,y+INSERT_OFFSET_Y+INSERT_HEIGHT/2);
		default:
			return null;
		}
	}

	public Set<String> getNodeNames() {
		return getNameToNode().keySet();
	}
	
	public Map<String,Node> getNameToNode() {
		if(cacheNameToNode==null)
			cacheNameToNode = new HashMap<>();
		
		cacheNameToNode.clear();
		for(Node node:getChildren()){
			String name = getNodeName(node);
			if(name!=null)
				cacheNameToNode.put(name, node);
		}
		return Collections.unmodifiableMap(cacheNameToNode);
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
		
		double left = getConnectionTypeInternal()==ConnectionType.LEFT?INSERT_WIDTH:0;
		double vSpace = getVSpacing();
		double hSpace = getHSpacing();
		double[][] actualAreaBounds = getTempArray(managed.size());;
		List<BlockSlot> slots = getLineBounds(managed, vSpace, hSpace, actualAreaBounds);
		
		if(slots.isEmpty())
			return 0;
		
		double width = 0;
		for(BlockSlot slot:slots){
			double lineWidth = slot.getLineWidth()+actualAreaBounds[0][slot.getLastNode()+1];
			if(width<lineWidth) width = lineWidth;
		}
		
		return left+width;
	}
	
	@Override
	protected double computePrefHeight(double width) { //TODO:fix height
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
		//return getConnectionTypeInternal()==ConnectionType.BOTTOM||getConnectionTypeInternal()==ConnectionType.TOPANDBOTTOM?height-vSpace:height;
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
		if (performingLayout) 
			return;
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
		double[][] actualAreaBounds = getTempArray(managed.size());
		List<BlockSlot> slots = getLineBounds(managed, vSpace, hSpace, actualAreaBounds);
		StringBuilder sb = getTempStringBuilder();
		
		if(!slots.isEmpty()){
			Pos pos = getAlignmentInternal();
			VPos vpos = pos.getVpos();
			HPos hpos = pos.getHpos();
			ConnectionType connectionType = getConnectionTypeInternal();
			double x = connectionType==ConnectionType.LEFT?INSERT_WIDTH:0, y = 0;
			boolean hasNext = false;
			
			sb.append(buildTopPath(connectionType, slots.get(0).getLineWidth()));
	
			label: for (int i = 0, size = slots.size(); i < size; i++) {
				BlockSlot slot = slots.get(i);
				layoutLine(slot, managed, actualAreaBounds, x, y, vSpace, hSpace, hpos, vpos);
				
				switch (slot.getSlotType()) {
				case BRANCH:
					sb.append(buildBranchPath(connectionType,y, slot.getLineWidth(), slot.getLineHeight(),
							i + 1 == size ? BLOCK_SLOT_MIN_LINE_WIDTH : slots.get(i + 1).getLineWidth()));
					break;
				case INSERT:
					sb.append(buildInsertPath(connectionType,y, slot.getLineWidth()));
					break;
				case NEXT:
					hasNext = true;
					break label;
				default:
					break;
				}
				
				y += slot.getLineHeight() + vSpace;
			}
			if(!hasNext&&connectionType!=ConnectionType.LEFT) y-=vSpace;
			sb.append(buildBottomPath(connectionType, y));
		}
		
		svgPath.setContent(sb.toString());
		layoutInArea(svgPath, 0, 0, svgPath.prefWidth(-1), svgPath.prefHeight(-1), 0, HPos.CENTER, VPos.CENTER);
		
		performingLayout = false;
	}

	private void layoutLine(BlockSlot slot, List<Node> managed, double[][] actualAreaBounds, double left, double top,
			double vSpace,double hSpace, HPos hpos, VPos vpos) {
		double x = left + hSpace;
		double y = top + vSpace;
		
		for (int i = slot.getFirstNode(), end = slot.getLastNode(); i <= end; i++) {
			Node child = managed.get(i);
			layoutInArea(child, x, y, child.prefWidth(-1), child.prefHeight(-1), 0, getMargin(child), hpos, vpos);
			x += actualAreaBounds[0][i] + hSpace;
		}
		
		layoutInArea(slot, slot.getSlotType()==SlotType.INSERT?left+slot.getLineWidth()-INSERT_SLOT_WIDTH:left+slot.getLineWidth(), top, slot.prefWidth(-1), slot.prefHeight(-1), 0, null, hpos, vpos);
	}

	private List<BlockSlot> getLineBounds(List<Node> managed, double vSpace, double hSpace, double[][] actualAreaBounds) {
		List<BlockSlot> cacheSlots = cacheSlots();
		double tempWidth = hSpace, tempHeight = 0, tempMaxWidth = 0;
		int lastBranchBlock = -1, firstNode = 0;
		
		label: for (int i = 0, size = managed.size(); i < size; i++) {
			Node child = managed.get(i);
			
			//计算Node宽高
			Insets margin = getMargin(child);
			actualAreaBounds[0][i] = computeChildPrefAreaWidth(child, -1, margin, -1, false);
			actualAreaBounds[1][i] = computeChildPrefAreaHeight(child, -1, margin, -1);
			
			if (tempHeight < actualAreaBounds[1][i]) tempHeight = actualAreaBounds[1][i];
			
			if (child instanceof BlockSlot) {
				BlockSlot slot = (BlockSlot) child;
				cacheSlots.add(slot);
				slot.setLineWidth(tempWidth);
				slot.setLineHeight(tempHeight);
				slot.setNodeRange(firstNode,i-1);
				
				if (tempMaxWidth < tempWidth) tempMaxWidth = tempWidth; // 求最大行宽

				switch(slot.getSlotType()){
					case BRANCH:
						int lastSlot = cacheSlots.size() - 1;
						lineAlign(cacheSlots, lastBranchBlock + 1, lastSlot - 1, tempMaxWidth);
						lastBranchBlock = lastSlot;
						tempMaxWidth = 0;
						break;
					case NEXT:
						break label;
					default:
						break;
				}
				
				firstNode = i + 1;
				tempWidth = hSpace;
				tempHeight = 0;
			} else 
				tempWidth += actualAreaBounds[0][i] + hSpace;
		}	
		lineAlign(cacheSlots, lastBranchBlock + 1, cacheSlots.size() - 1, tempMaxWidth); //最后一次行对齐
		return cacheSlots;
	}
	
	private void lineAlign(List<BlockSlot> managed, int start, int end, double width) {
		if(start==end)
			return;
		
		for (int i = start; i <= end; i++) 
			managed.get(i).setLineWidth(width);
	}
	
	private List<BlockSlot> cacheSlots(){
		if(cacheSlots == null)
			cacheSlots = new ArrayList<>();
		cacheSlots.clear();
		return cacheSlots;
	}

	private double[][] getTempArray(int size) {
		if (tempArray == null) {
			tempArray = new double[2][size];
		} else if (tempArray[0].length < size) {
			tempArray = new double[2][Math.max(tempArray.length * 3, size)];
		}
		return tempArray;
	}
	
	private StringBuilder getTempStringBuilder(){
		if(tempStringBuilder==null){
			tempStringBuilder = new StringBuilder();
		}
		tempStringBuilder.delete(0,tempStringBuilder.length());
		return tempStringBuilder;
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
                return node.alignmentProperty();
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
						return node.connectionTypeProperty();
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
                return node.vSpacingProperty();
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
                    return node.hSpacingProperty();
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
						return styleable.movableProperty();
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
    
    public Path2D createSVGPath2D(String content,FillRule fillRule) {
        int windingRule = fillRule == FillRule.NON_ZERO ? PathIterator.WIND_NON_ZERO : PathIterator.WIND_EVEN_ODD;
        Path2D path = new Path2D(windingRule);
        path.appendSVGPath(content);
        return path;
    }
}