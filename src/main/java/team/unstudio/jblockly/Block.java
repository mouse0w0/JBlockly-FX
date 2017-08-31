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
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Skin;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import team.unstudio.jblockly.input.BlockSlot;
import team.unstudio.jblockly.util.provider.IBlockProvider;
import team.unstudio.jblockly.util.ui.FXHelper;

//TODO: Support event
public class Block extends Control implements IBlockly,BlockGlobal{
	
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
    public final Pos getAlignmentInternal() {
        Pos localPos = getAlignment();
        return localPos == null ? Pos.TOP_LEFT : localPos;
    }

	private ReadOnlyBooleanWrapper moving;
	private final ReadOnlyBooleanWrapper movingPropertyImpl(){
		if(moving==null){
			moving = new ReadOnlyBooleanWrapper(this, "moving"){
				@Override
				public void set(boolean newValue) {
					if(hasWorkspace()){
						if(newValue)
							getWorkspace().setMovingBlock(Block.this);
						else if(getWorkspace().getMovingBlock().equals(Block.this))
							getWorkspace().setMovingBlock(null);
					}
					
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
    public final ConnectionType getConnectionTypeInternal() {
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
			
			name.addListener((observable, oldValue, newValue)->{
				if(oldValue!=null&&!oldValue.isEmpty())
					getStyleClass().remove(oldValue);
				if(newValue!=null&&!newValue.isEmpty())
					getStyleClass().add(newValue);
			});
		}
		return name;
	}
	public final String getName() {return name == null?"":name.get();}
	public final void setName(String name) {name().set(name);}
	
	private ObjectProperty<IBlockProvider> provider;
	public final ObjectProperty<IBlockProvider> providerProperty(){
		if(provider == null)
			provider = new ObjectPropertyBase<IBlockProvider>() {

				@Override
				public Object getBean() {
					return Block.this;
				}

				@Override
				public String getName() {
					return "provider";
				}
			
			};
		return provider;
	}
	public final IBlockProvider getProvider(){return provider == null ? null : provider.get();}
	public final void setProvider(IBlockProvider value){providerProperty().set(value);}
	public final boolean hasProvider(){ return getProvider() != null;}
	
	private ObjectProperty<Paint> fill;
	public final ObjectProperty<Paint> fillProperty() {//TODO:Support CSS
		if(fill==null)
			fill = new ObjectPropertyBase<Paint>() {

				@Override
				public Object getBean() {
					return Block.this;
				}

				@Override
				public String getName() {
					return "fill";
				}
			};
		return fill;
	}
	public final Paint getFill(){return fill==null?Color.WHITE:fill.get();}
	public final void setFill(Paint value){fillProperty().set(value);}
	
	private ObjectProperty<Paint> stroke;
	public final ObjectProperty<Paint> strokeProperty() {//TODO:Support CSS
		if(stroke==null)
			stroke = new ObjectPropertyBase<Paint>() {
			
				@Override
				public Object getBean() {
					return Block.this;
				}

				@Override
				public String getName() {
					return "stroke";
				}
			};
		return stroke;
	}
	public final Paint getStroke(){return stroke==null?Color.BLACK:stroke.get();}
	public final void setStroke(Paint value){strokeProperty().set(value);}
	
	private ReadOnlyBooleanWrapper selected;
	final ReadOnlyBooleanWrapper selectedPropertyImpl(){
		if(selected == null){
			selected = new ReadOnlyBooleanWrapper(this, "selected");
			selected.addListener((observable, oldValue, newValue)->{
				BlockWorkspace workspace = getWorkspace();
				if(workspace!=null)
					workspace.setSelectedBlock(Block.this);
			});
		}
		return selected;
	}
	final void setSelected(boolean selected){selectedPropertyImpl().set(selected);}
	public final boolean isSelected(){return selected == null ? false : selected.get();}
	public ReadOnlyBooleanProperty selectedProperty(){return selectedPropertyImpl().getReadOnlyProperty();}
	
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
	public final boolean hasWorkspace(){return getWorkspace() != null;}
	private final ChangeListener<BlockWorkspace> workspaceListener = (observable, oldValue, newValue)->setWorkspace(newValue);
	
	private final ObservableList<Node> components = FXCollections.observableArrayList();
	private final ObservableList<Node> unmodifiableComponents = FXCollections.unmodifiableObservableList(components);
	final ObservableList<Node> getComponents(){return components;}
	public final ObservableList<Node> getUnmodifiableComponents(){return unmodifiableComponents;}
	
	private final ObservableList<BlockSlot> slots = FXCollections.observableArrayList();
	private final ObservableList<BlockSlot> unmodifiableSlots = FXCollections.unmodifiableObservableList(slots);
	public final ObservableList<BlockSlot> getUnmodifiableSlots(){
		return unmodifiableSlots;
	}
	
	private double tempOldX, tempOldY;
	
	private static final String DEFAULT_STYLE_CLASS = "block";
	
	public Block() {
		getStyleClass().addAll(DEFAULT_STYLE_CLASS);

		setOnMousePressed(event -> {
			if(!hasWorkspace())
				return;
			
			setSelected(true);
			
			if (!isMovable())
				return;
			
			addToWorkspace();

			Point2D pos = FXHelper.getRelativePos(getWorkspace(), this);
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
			
			if(isConnectable()&&hasWorkspace())
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
		
		getChildren().addListener(new ListChangeListener<Node>(){

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Node> c) {
				c.next();
				c.getAddedSubList().stream().filter(node->node instanceof BlockSlot).forEach(node->slots.add((BlockSlot) node));
				c.getRemoved().stream().filter(node->node instanceof BlockSlot).forEach(node->slots.remove(node));
			}
			
		});

		setPickOnBounds(false); // 启用不规则图形判断,具体见contains方法
		setSnapToPixel(true);
		
		//Menu
		ContextMenu menu = new ContextMenu();
		MenuItem delete = new MenuItem("Delete");
		delete.setOnAction(event->removeBlock());
		menu.getItems().add(delete);
		setContextMenu(menu);
		
		//default setting
		setVSpacing(5);
		setHSpacing(5);
	}

	public void addToWorkspace() {
		Parent oldParent = getParent();
		if (oldParent instanceof BlockWorkspace) {
			toFront();
		} else {
			Point2D pos = FXHelper.getRelativePos(getWorkspace(), this);
			getWorkspace().getChildren().add(this);
			setLayoutX(pos.getX());
			setLayoutY(pos.getY());
		}
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
		
		for(BlockSlot slot:getUnmodifiableSlots())
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

	@Override
	public ObservableList<Node> getChildren() {
		return super.getChildren();
	}

	@Override
	public boolean contains(double localX, double localY) {
		return ((BlockSkin)getSkin()).getSVGPath().contains(localX, localY);
	}

	@Override
	public boolean contains(Point2D localPoint) {
		return ((BlockSkin)getSkin()).getSVGPath().contains(localPoint);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new BlockSkin(this);
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
}