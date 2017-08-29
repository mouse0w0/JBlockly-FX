package team.unstudio.jblockly;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.shape.SVGPath;
import team.unstudio.jblockly.input.BlockSlot;
import team.unstudio.jblockly.util.SVGPathHelper;

public class BlockSkin extends SkinBase<Block> implements BlockGlobal,SVGPathHelper{
	
	private ObservableList<Node> components;
	private SVGPath svgPath = new SVGPath();

	public BlockSkin(Block control) {
		super(control);
		components = control.getComponents();
		
		init();
	}
	
	private void init(){
		components.addAll(getChildren());
		
		//Init SVGPath
		svgPath.fillProperty().bind(getSkinnable().fillProperty());
		svgPath.strokeProperty().bind(getSkinnable().strokeProperty());
		svgPath.setLayoutX(0);
		svgPath.setLayoutY(0);
		getChildren().add(svgPath);
		svgPath.toBack();
		
		getChildren().addListener(new ListChangeListener<Node>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Node> event) {
				event.next();
				components.removeAll(event.getRemoved());
				components.addAll(event.getAddedSubList());
			}

		});
		
	}
	
	public SVGPath getSVGPath(){
		return svgPath;
	}
	
	private boolean performingLayout;
	private double[][] tempArray;
	private StringBuilder tempStringBuilder;
	private List<BlockLineWrapper> cacheLines;
	
	@Override
	protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
		if (performingLayout) 
			return;
		performingLayout = true;
		
		Block block = getSkinnable();
		double vSpace = block.getVSpacing(),hSpace = block.getHSpacing();
		double[][] actualAreaBounds = getTempArray(components.size());
		List<BlockLineWrapper> lines = getLineBounds(components, vSpace, hSpace, actualAreaBounds);
		StringBuilder sb = getTempStringBuilder();
		
		if(!lines.isEmpty()){
			Pos pos = block.getAlignmentInternal();
			VPos vpos = pos.getVpos();
			HPos hpos = pos.getHpos();
			ConnectionType connectionType = block.getConnectionTypeInternal();
			double x = connectionType==ConnectionType.LEFT?INSERT_WIDTH:0, y = 0;
			
			sb.append(buildTopPath(connectionType, lines.get(0).getLineWidth())); 
	
			label: for (int i = 0, size = lines.size(); i < size; i++) {
				BlockLineWrapper line = lines.get(i);
				layoutLine(line, actualAreaBounds, x, y, vSpace, hSpace, hpos, vpos);
				
				switch (line.getSlot().getSlotType()) {
				case BRANCH:
					sb.append(buildBranchPath(connectionType,y, line.getLineWidth(), line.getFullHeight(),
							i + 1 == size ? 
									i - 1 == -1 ? BLOCK_SLOT_MIN_LINE_WIDTH : lines.get(i - 1).getLineWidth() 
										: lines.get(i + 1).getLineWidth()));
					break;
				case INSERT:
					sb.append(buildInsertPath(connectionType,y, line.getLineWidth()));
					break;
				case NEXT:
					break label;
				default:
					break;
				}
				
				y += line.getFullHeight();
			}
			sb.append(buildBottomPath(connectionType, y));
		}
		
		svgPath.setContent(sb.toString());
		
		performingLayout = false;
	}	
	private List<BlockLineWrapper> getLineBounds(List<Node> managed, double vSpace, double hSpace, double[][] actualAreaBounds) {
		List<BlockLineWrapper> cacheLines = cacheLines();
		double tempWidth = 0, tempHeight = 0, tempMaxWidth = 0;
		int lastBranchBlockIndex = -1, firstNodeIndex = 0;
		BlockLineWrapper line = new BlockLineWrapper();
		
		label: for (int i = 0, size = managed.size(); i < size; i++) {
			Node child = managed.get(i);
			Insets margin = Block.getMargin(child);
			actualAreaBounds[0][i] = computeChildPrefAreaWidth(child, margin);
			actualAreaBounds[1][i] = computeChildPrefAreaHeight(child, margin);
			
			if (child instanceof BlockSlot) {
				BlockSlot slot = (BlockSlot) child;
				line.setSlot(slot);
				line.setSlotWidth(actualAreaBounds[0][i]);
				line.setSlotHeight(actualAreaBounds[1][i]);
				line.setWidth(tempWidth);
				line.setHeight(Math.max(tempHeight + vSpace,actualAreaBounds[1][i]));
				line.setFirstNodeIndex(firstNodeIndex);
				cacheLines.add(line);
				
				tempMaxWidth = Math.max(tempMaxWidth, tempWidth); // 求最大行宽

				switch(slot.getSlotType()){
					case BRANCH: //行对齐
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
				tempHeight = Math.max(tempHeight, actualAreaBounds[1][i]);
			}
		}	
		
		lineAlign(cacheLines, lastBranchBlockIndex + 1, cacheLines.size() - 1, tempMaxWidth); //最后一次行对齐
		return cacheLines;
	}

	private void layoutLine(BlockLineWrapper line, double[][] actualAreaBounds, double left, double top,
			double vSpace, double hSpace, HPos hpos, VPos vpos) {
		double x = left + hSpace,y = top + vSpace;
		
		for (int i=0,size = line.getNodes().size();i<size;i++) {
			Node child = line.getNodes().get(i);
			double width = actualAreaBounds[0][i+line.getFirstNodeIndex()],height = actualAreaBounds[1][line.getFirstNodeIndex()+i];
			layoutInArea(child, x, y, width, height, 0, Block.getMargin(child), hpos, vpos);
			x += width + hSpace;
		}
		
		//layout slot
		BlockSlot slot = line.getSlot();
		slot.setLayoutX(left+line.getWidth());
		slot.setLayoutY(top);
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

	private double computeChildPrefAreaHeight(Node child, Insets margin) {
		final boolean snap = getSkinnable().isSnapToPixel();
		double top = margin != null ? snapSpace(margin.getTop(), snap) : 0;
		double bottom = margin != null ? snapSpace(margin.getBottom(), snap) : 0;
		return top + snapSize(child.prefHeight(-1)) + bottom;
	}

	private double computeChildPrefAreaWidth(Node child, Insets margin) {
		final boolean snap = getSkinnable().isSnapToPixel();
		double left = margin != null ? snapSpace(margin.getLeft(), snap) : 0;
		double right = margin != null ? snapSpace(margin.getRight(), snap) : 0;
		return left + snapSize(child.prefWidth(-1)) + right;
	}

	private static double snapSpace(double value, boolean snapToPixel) {
		return snapToPixel ? Math.round(value) : value;
	}
	
	@Override
	protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset,
			double leftInset) {
		Block block = getSkinnable();
		
		double left = block.getConnectionTypeInternal()==ConnectionType.LEFT?INSERT_WIDTH:0;
		double vSpace = block.getVSpacing(),hSpace = block.getHSpacing();
		List<BlockLineWrapper> lines = getLineBounds(components, vSpace, hSpace, getTempArray(components.size()));
		
		if(lines.isEmpty())
			return 0;
		
		double width = 0;
		for(BlockLineWrapper line:lines)
			width = Math.max(width,line.getFullWidth());
		
		return left+width;
	}
	
	@Override
	protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset,
			double leftInset) {
		Block block = getSkinnable();
		
		double vSpace = block.getVSpacing(),hSpace = block.getHSpacing();
		List<BlockLineWrapper> lines = getLineBounds(components, vSpace, hSpace, getTempArray(components.size()));
		
		if(lines.isEmpty())
			return 0;
		
		double height = 0;
		for(BlockLineWrapper line:lines)
			height += line.getFullHeight();
		
		return height;
	}
	
	@Override
	public void dispose() {
		cacheLines = null;
		components = null;
		svgPath = null;
		tempStringBuilder = null;
		super.dispose();
	}
	
    private static class BlockLineWrapper{
    	private BlockSlot slot;
    	private final List<Node> nodes = new ArrayList<>();
    	private double width = 0, height = 0, slotWidth = 0, slotHeight = 0;
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
    			return Math.max(BRANCH_MIN_WIDTH,width);
    		case NEXT:
    			return 0;
    		default:
    			return Math.max(BLOCK_SLOT_MIN_LINE_WIDTH,width);
    		}
    	}
    	
    	public void setWidth(double width) {
    		this.width = width;
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

		public double getSlotWidth() {
			return slotWidth;
		}

		public void setSlotWidth(double slotWidth) {
			this.slotWidth = slotWidth;
		}

		public double getSlotHeight() {
			return slotHeight;
		}

		public void setSlotHeight(double slotHeight) {
			this.slotHeight = slotHeight;
		}

		public double getFullWidth() {
			return getWidth()+getSlotWidth();
		}

		public double getFullHeight() {
    		switch (slot.getSlotType()) {
    		case NEXT:
    			return slot.hasBlock()?getSlotHeight():getSlotHeight()+NEXT_SLOT_BOTTOM_HEIGHT;
    		case BRANCH:
    			return (slot.hasBlock()?Math.max(getHeight(),getSlotHeight()):Math.max(getHeight(),getSlotHeight()+BRANCH_SLOT_CONTAINER_MIN_HEIGHT))+BRANCH_SLOT_BOTTOM_HEIGHT;
    		default:
    			return Math.max(getHeight(),getSlotHeight());
    		}
		}
		
		public double getLineWidth(){
    		switch (slot.getSlotType()) {
    		case BRANCH:
    		case NEXT:
    			return getWidth();
    		default:
    			return getWidth()+slot.getDefaultWidth();
    		}
			
		}
    }

}
