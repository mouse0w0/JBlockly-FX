package team.unstudio.jblockly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.sun.javafx.css.converters.BooleanConverter;
import com.sun.javafx.css.converters.EnumConverter;
import com.sun.javafx.css.converters.SizeConverter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.beans.value.ChangeListener;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import team.unstudio.jblockly.input.BlockSlot;
import team.unstudio.jblockly.input.SlotType;
import team.unstudio.jblockly.util.IBlockBuilder;
import team.unstudio.jblockly.util.SVGPathHelper;

//TODO: Support event
public class Block extends Region implements IBlockly,SVGPathHelper{
	
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
			moving = new ReadOnlyBooleanWrapper(this, "moving"){
				@Override
				public void set(boolean newValue) {
					if(newValue)
						getWorkspace().setMovingBlock(Block.this);
					else if(getWorkspace().getMovingBlock().equals(Block.this))
						getWorkspace().setMovingBlock(null);
					
					super.set(newValue);
				}
			};
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
    
	private StringProperty name;
	public final StringProperty name() {
		if(name==null){
			name = new StringPropertyBase() {
				
				@Override
				public String getName() {
					return "name";
				}
				
				@Override
				public Object getBean() {
					return Block.this;
				}
			};
		}
		return name;
	}
	public final String getName() {return name == null?"":name.get();}
	public final void setName(String name) {name().set(name);}
	
	private ObjectProperty<IBlockBuilder> builder;
	public final ObjectProperty<IBlockBuilder> builderProperty(){
		if(builder == null)
			builder = new ObjectPropertyBase<IBlockBuilder>() {

				@Override
				public Object getBean() {
					return Block.this;
				}

				@Override
				public String getName() {
					return "builder";
				}
			
			};
		return builder;
	}
	public final IBlockBuilder getBuilder(){return builder == null ? null : builder.get();}
	public final void setBuilder(IBlockBuilder builder){builderProperty().set(builder);}
	public final boolean hasBuilder(){ return getBuilder() != null;}
	
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
	private final ChangeListener<BlockWorkspace> workspaceListener = (observable, oldValue, newValue)->setWorkspace(newValue);
	
	private double tempOldX, tempOldY;
	private boolean performingLayout;
	private double[][] tempArray;
	private StringBuilder tempStringBuilder;
	
	private List<BlockLineWrapper> cacheLines;
	
	private static final String DEFAULT_STYLE_CLASS = "block";
	
	public Block() {
		getStyleClass().addAll(DEFAULT_STYLE_CLASS);
		
		getChildren().add(svgPath);

		setOnMousePressed(event -> {
			if (!isMovable())
				return;

			if(getWorkspace()==null)
				return;
			
			addToWorkspace();

			Point2D pos = getRelativeWorkspace();
			tempOldX = event.getSceneX() - pos.getX();
			tempOldY = event.getSceneY() - pos.getY();
			
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
		} else {
			Point2D pos = getRelativeWorkspace();
			getWorkspace().getChildren().add(this);
			setLayoutX(pos.getX());
			setLayoutY(pos.getY());
		}
	}
	
	private Point2D getRelativeWorkspace(){
		//TODO: Screen relative location
		BlockWorkspace workspace = getWorkspace();
		Parent parent = getParent();
		double x = getLayoutX(), y = getLayoutY();
		while (parent!=null&&parent!=workspace) {
			x += parent.getLayoutX();
			y += parent.getLayoutY();
			parent = parent.getParent();
		}
		return new Point2D(x, y);
	}
	
	public void removeBlock(){
		Parent parent = getParent();
		if(parent instanceof BlockSlot)
			((BlockSlot) parent).removeBlock();
		else if(parent instanceof Pane)
			((Pane) parent).getChildren().remove(this);
	}
	
	public boolean tryConnectBlock(Block block,double x,double y){
		if(block == this)
			return false;
		
		if(!getLayoutBounds().contains(x, y))
			return false;
		
		for(BlockLineWrapper line:cacheLines){
			BlockSlot slot = line.getSlot();
			if(slot.tryConnectBlock(block, x-slot.getLayoutX(), y-slot.getLayoutY()))
				return true;
		}
		
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
		List<BlockLineWrapper> lines = getLineBounds(managed, vSpace, hSpace, actualAreaBounds);
		
		if(lines.isEmpty())
			return 0;
		
		double width = 0;
		for(BlockLineWrapper line:lines){
			double lineWidth = line.getWidth()+actualAreaBounds[0][line.getFirstNodeIndex()+line.getNodes().size()];
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
		List<BlockLineWrapper> lines = getLineBounds(managed, vSpace, hSpace, actualAreaBounds);
		
		if(lines.isEmpty())
			return 0;
		
		double height = -vSpace;
		for(BlockLineWrapper line:lines)
			height += line.getHeight() + vSpace;
		
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
		List<BlockLineWrapper> lines = getLineBounds(managed, vSpace, hSpace, actualAreaBounds);
		StringBuilder sb = getTempStringBuilder();
		
		if(!lines.isEmpty()){
			Pos pos = getAlignmentInternal();
			VPos vpos = pos.getVpos();
			HPos hpos = pos.getHpos();
			ConnectionType connectionType = getConnectionTypeInternal();
			double x = connectionType==ConnectionType.LEFT?INSERT_WIDTH:0, y = 0;
			boolean hasNext = false;
			
			sb.append(buildTopPath(connectionType, lines.get(0).getWidth()));
	
			label: for (int i = 0, size = lines.size(); i < size; i++) {
				BlockLineWrapper line = lines.get(i);
				layoutLine(line, actualAreaBounds, x, y, vSpace, hSpace, hpos, vpos);
				
				switch (line.getSlot().getSlotType()) {
				case BRANCH:
					sb.append(buildBranchPath(connectionType,y, line.getWidth(), line.getHeight(),
							i + 1 == size ? BLOCK_SLOT_MIN_LINE_WIDTH : lines.get(i + 1).getWidth()));
					break;
				case INSERT:
					sb.append(buildInsertPath(connectionType,y, line.getWidth()));
					break;
				case NEXT:
					hasNext = true;
					break label;
				default:
					break;
				}
				
				y += line.getHeight() + vSpace;
			}
			if(!hasNext&&connectionType!=ConnectionType.LEFT) y-=vSpace;
			sb.append(buildBottomPath(connectionType, y));
		}
		
		svgPath.setContent(sb.toString());
		layoutInArea(svgPath, 0, 0, svgPath.prefWidth(-1), svgPath.prefHeight(-1), 0, HPos.CENTER, VPos.CENTER);
		
		performingLayout = false;
	}

	private void layoutLine(BlockLineWrapper line, double[][] actualAreaBounds, double left, double top,
			double vSpace,double hSpace, HPos hpos, VPos vpos) {
		double x = left + hSpace;
		double y = top + vSpace;
		
		for (int i=0,size = line.getNodes().size();i<size;i++) {
			Node child = line.getNodes().get(i);
			layoutInArea(child, x, y, child.prefWidth(-1), child.prefHeight(-1), 0, getMargin(child), hpos, vpos);
			x += actualAreaBounds[0][i+line.getFirstNodeIndex()] + hSpace;
		}
		
		BlockSlot slot = line.getSlot();
		layoutInArea(slot, slot.getSlotType()==SlotType.INSERT?left+line.getWidth()-INSERT_SLOT_WIDTH:left+line.getWidth(), top, slot.prefWidth(-1), slot.prefHeight(-1), 0, null, hpos, vpos);
	}

	private List<BlockLineWrapper> getLineBounds(List<Node> managed, double vSpace, double hSpace, double[][] actualAreaBounds) {
		List<BlockLineWrapper> cacheLines = cacheLines();
		double tempWidth = hSpace, tempHeight = 0, tempMaxWidth = 0;
		int lastBranchBlockIndex = -1, firstNodeIndex = 0;
		BlockLineWrapper line = new BlockLineWrapper();
		
		label: for (int i = 0, size = managed.size(); i < size; i++) {
			Node child = managed.get(i);
			
			//计算Node宽高
			Insets margin = getMargin(child);
			actualAreaBounds[0][i] = computeChildPrefAreaWidth(child, -1, margin, -1, false);
			actualAreaBounds[1][i] = computeChildPrefAreaHeight(child, -1, margin, -1);
			
			if (tempHeight < actualAreaBounds[1][i]) tempHeight = actualAreaBounds[1][i];
			
			if (child instanceof BlockSlot) {
				BlockSlot slot = (BlockSlot) child;
				cacheLines.add(line);
				line.setSlot(slot);
				line.setWidth(tempWidth);
				line.setHeight(tempHeight);
				line.setFirstNodeIndex(firstNodeIndex);
				
				if (tempMaxWidth < tempWidth) tempMaxWidth = tempWidth; // 求最大行宽

				switch(slot.getSlotType()){
					case BRANCH:
						int lastLine = cacheLines.size() - 1;
						lineAlign(cacheLines, lastBranchBlockIndex + 1, lastLine - 1, tempMaxWidth);
						lastBranchBlockIndex = lastLine;
						tempMaxWidth = 0;
						break;
					case NEXT:
						break label;
					default:
						break;
				}
				
				firstNodeIndex = i + 1;
				tempWidth = hSpace;
				tempHeight = 0;
				line = new BlockLineWrapper();
			} else {
				line.getNodes().add(child);
				tempWidth += actualAreaBounds[0][i] + hSpace;
			}
		}	
		lineAlign(cacheLines, lastBranchBlockIndex + 1, cacheLines.size() - 1, tempMaxWidth); //最后一次行对齐
		return cacheLines;
	}
	
	private void lineAlign(List<BlockLineWrapper> managed, int start, int end, double width) {
		if(start==end)
			return;
		
		for (int i = start; i <= end; i++) 
			managed.get(i).setWidth(width);
	}
	
	private List<BlockLineWrapper> cacheLines(){
		if(cacheLines == null)
			cacheLines = new ArrayList<>();
		cacheLines.clear();
		return cacheLines;
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

//    @Override
//    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
//        return getClassCssMetaData();
//    }
    
    private class BlockLineWrapper{
    	//TODO: Replace cacheSlots\
    	private BlockSlot slot;
    	private final List<Node> nodes = new ArrayList<>();
    	private double width = 0, height = 0;
    	private int firstNodeIndex;

    	public double getHeight() {
    		return height;
    	}

    	public void setHeight(double lineHeight) {
    		this.height = lineHeight;
    	}

    	public double getWidth() {
    		switch (slot.getSlotType()) {
    		case BRANCH:
    			return width<BRANCH_MIN_WIDTH?BRANCH_MIN_WIDTH:width;
    		case NEXT:
    			return 0;
    		default:
    			return width<BLOCK_SLOT_MIN_LINE_WIDTH?BLOCK_SLOT_MIN_LINE_WIDTH:width;
    		}
    	}

    	public void setWidth(double lineWidth) {
    		this.width = lineWidth;
    	}

    	public List<Node> getNodes() {
    		return nodes;
    	}

		public BlockSlot getSlot() {
			return slot;
		}

		public void setSlot(BlockSlot slot) {
			this.slot = slot;
		}

		public int getFirstNodeIndex() {
			return firstNodeIndex;
		}

		public void setFirstNodeIndex(int firstNodeIndex) {
			this.firstNodeIndex = firstNodeIndex;
		}
    }
}