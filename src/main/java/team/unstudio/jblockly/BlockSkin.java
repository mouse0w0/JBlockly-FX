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
import team.unstudio.jblockly.input.SlotType;
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
			layoutInArea(child, x, y, child.prefWidth(-1), child.prefHeight(-1), 0, Block.getMargin(child), hpos, vpos);
			x += actualAreaBounds[0][i+line.getFirstNodeIndex()] + hSpace;
		}
		
		BlockSlot slot = line.getSlot();
		layoutInArea(slot, slot.getSlotType()==SlotType.INSERT?left+line.getWidth()-INSERT_SLOT_WIDTH:left+line.getWidth(),top, slot.prefWidth(-1), slot.prefHeight(-1), 0, null, hpos, vpos);
	}

	private List<BlockLineWrapper> getLineBounds(List<Node> managed, double vSpace, double hSpace, double[][] actualAreaBounds) {
		List<BlockLineWrapper> cacheLines = cacheLines();
		double tempWidth = hSpace, tempHeight = 0, tempMaxWidth = 0;
		int lastBranchBlockIndex = -1, firstNodeIndex = 0;
		BlockLineWrapper line = new BlockLineWrapper();
		
		label: for (int i = 0, size = managed.size(); i < size; i++) {
			Node child = managed.get(i);
			
			//计算Node宽高
			Insets margin = Block.getMargin(child);
			actualAreaBounds[0][i] = computeChildPrefAreaWidth(child, margin);
			actualAreaBounds[1][i] = computeChildPrefAreaHeight(child, margin);
			
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
		double[][] actualAreaBounds = getTempArray(components.size());;
		List<BlockLineWrapper> lines = getLineBounds(components, vSpace, hSpace, actualAreaBounds);
		
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
	protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset,
			double leftInset) {
		Block block = getSkinnable();
		
		double vSpace = block.getVSpacing(),hSpace = block.getHSpacing();
		double[][] actualAreaBounds = getTempArray(components.size());;
		List<BlockLineWrapper> lines = getLineBounds(components, vSpace, hSpace, actualAreaBounds);
		
		if(lines.isEmpty())
			return 0;
		
		double height = -vSpace;
		for(BlockLineWrapper line:lines)
			height += line.getHeight() + vSpace;
		
		return height;
		//return getConnectionTypeInternal()==ConnectionType.BOTTOM||getConnectionTypeInternal()==ConnectionType.TOPANDBOTTOM?height-vSpace:height;
	}
	
	@Override
	public void dispose() {
		cacheLines = null;
		components = null;
		svgPath = null;
		tempStringBuilder = null;
		super.dispose();
	}
	
    static class BlockLineWrapper{
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
