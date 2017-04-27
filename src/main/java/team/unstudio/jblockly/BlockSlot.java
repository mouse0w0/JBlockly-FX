package team.unstudio.jblockly;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Region;

public class BlockSlot extends Region{
	
	public enum SlotType{
		NONE,INSERT,BRANCH,NEXT
	}
	
	private SlotType slotType = SlotType.NONE;
	private Block block;
	private double lineWidth,lineHeight;
	
	public BlockSlot() {
		
	}
	
	public BlockWorkspace getWorkspace(){
		Parent parent = getParent();
		
		if(parent instanceof Block)
			return ((Block) parent).getWorkspace();
		else
			return null;
	}

	public SlotType getSlotType() {
		return slotType;
	}

	public void setSlotType(SlotType slotType) {
		this.slotType = slotType;
	}

	public Block getBlock() {
		return block;
	}

	public void setBlock(Block block) {
		if(this.block!=null) getChildren().remove(this.block);
		if(block!=null) getChildren().add(block);
		this.block = block;
	}
	
	@Override
	protected void layoutChildren() {
		if(block==null) return;
		layoutInArea(block, 0, 0, computeChildMinAreaWidth(block, -1, null, -1, false), computeChildMinAreaHeight(block, -1, null, -1), 0, null, HPos.CENTER, VPos.CENTER);
	}
	
	@Override
	protected double computeMinWidth(double height) {
		return block==null?0:block.minWidth(height);
	}
	
	@Override
	protected double computeMinHeight(double width) {
		return block==null?0:block.minHeight(width);
	}
	
	@Override
	protected double computePrefWidth(double height) {
		return block==null?0:block.prefWidth(height);
	}

	@Override
	protected double computePrefHeight(double width) {
		return block==null?0:block.prefHeight(width);
	}
	
	@Override
	protected double computeMaxWidth(double height) {
		return block==null?0:block.maxWidth(height);
	}
	
	@Override
	protected double computeMaxHeight(double width) {
		return block==null?0:block.maxHeight(width);
	}

	public double getLineHeight() {
		return lineHeight;
	}

	public void setLineHeight(double lineHeight) {
		this.lineHeight = lineHeight;
	}

	public double getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(double lineWidth) {
		this.lineWidth = lineWidth;
	}
	
    private double computeChildMinAreaHeight(Node child, double minBaselineComplement, Insets margin, double width) {
        final boolean snap = isSnapToPixel();
        double top =margin != null? snapSpace(margin.getTop(), snap) : 0;
        double bottom = margin != null? snapSpace(margin.getBottom(), snap) : 0;

        double alt = -1;
        if (child.isResizable() && child.getContentBias() == Orientation.HORIZONTAL) { // height depends on width
            double left = margin != null? snapSpace(margin.getLeft(), snap) : 0;
            double right = margin != null? snapSpace(margin.getRight(), snap) : 0;
            alt = snapSize(width != -1? boundedSize(child.minWidth(-1), width - left - right, child.maxWidth(-1)) :
                    child.maxWidth(-1));
        }

        // For explanation, see computeChildPrefAreaHeight
        if (minBaselineComplement != -1) {
            double baseline = child.getBaselineOffset();
            if (child.isResizable() && baseline == BASELINE_OFFSET_SAME_AS_HEIGHT) {
                return top + snapSize(child.minHeight(alt)) + bottom
                        + minBaselineComplement;
            } else {
                return baseline + minBaselineComplement;
            }
        } else {
            return top + snapSize(child.minHeight(alt)) + bottom;
        }
    }
    
    private double computeChildPrefAreaHeight(Node child, double prefBaselineComplement, Insets margin, double width) {
        final boolean snap = isSnapToPixel();
        double top = margin != null? snapSpace(margin.getTop(), snap) : 0;
        double bottom = margin != null? snapSpace(margin.getBottom(), snap) : 0;

        double alt = -1;
        if (child.isResizable() && child.getContentBias() == Orientation.HORIZONTAL) { // height depends on width
            double left = margin != null ? snapSpace(margin.getLeft(), snap) : 0;
            double right = margin != null ? snapSpace(margin.getRight(), snap) : 0;
            alt = snapSize(boundedSize(
                    child.minWidth(-1), width != -1 ? width - left - right
                    : child.prefWidth(-1), child.maxWidth(-1)));
        }

        if (prefBaselineComplement != -1) {
            double baseline = child.getBaselineOffset();
            if (child.isResizable() && baseline == BASELINE_OFFSET_SAME_AS_HEIGHT) {
                // When baseline is same as height, the preferred height of the node will be above the baseline, so we need to add
                // the preferred complement to it
                return top + snapSize(boundedSize(child.minHeight(alt), child.prefHeight(alt), child.maxHeight(alt))) + bottom
                        + prefBaselineComplement;
            } else {
                // For all other Nodes, it's just their baseline and the complement.
                // Note that the complement already contain the Node's preferred (or fixed) height
                return top + baseline + prefBaselineComplement + bottom;
            }
        } else {
            return top + snapSize(boundedSize(child.minHeight(alt), child.prefHeight(alt), child.maxHeight(alt))) + bottom;
        }
    }
    
    private double computeChildMinAreaWidth(Node child, double baselineComplement, Insets margin, double height, boolean fillHeight) {
        final boolean snap = isSnapToPixel();
        double left = margin != null? snapSpace(margin.getLeft(), snap) : 0;
        double right = margin != null? snapSpace(margin.getRight(), snap) : 0;
        double alt = -1;
        if (height != -1 && child.isResizable() && child.getContentBias() == Orientation.VERTICAL) { // width depends on height
            double top = margin != null? snapSpace(margin.getTop(), snap) : 0;
            double bottom = (margin != null? snapSpace(margin.getBottom(), snap) : 0);
            double bo = child.getBaselineOffset();
            final double contentHeight = bo == BASELINE_OFFSET_SAME_AS_HEIGHT && baselineComplement != -1 ?
                    height - top - bottom - baselineComplement :
                     height - top - bottom;
            if (fillHeight) {
                alt = snapSize(boundedSize(
                        child.minHeight(-1), contentHeight,
                        child.maxHeight(-1)));
            } else {
                alt = snapSize(boundedSize(
                        child.minHeight(-1),
                        child.prefHeight(-1),
                        Math.min(child.maxHeight(-1), contentHeight)));
            }
        }
        return left + snapSize(child.minWidth(alt)) + right;
    }
    
    private double computeChildPrefAreaWidth(Node child, double baselineComplement, Insets margin, double height, boolean fillHeight) {
        final boolean snap = isSnapToPixel();
        double left = margin != null? snapSpace(margin.getLeft(), snap) : 0;
        double right = margin != null? snapSpace(margin.getRight(), snap) : 0;
        double alt = -1;
        if (height != -1 && child.isResizable() && child.getContentBias() == Orientation.VERTICAL) { // width depends on height
            double top = margin != null? snapSpace(margin.getTop(), snap) : 0;
            double bottom = margin != null? snapSpace(margin.getBottom(), snap) : 0;
            double bo = child.getBaselineOffset();
            final double contentHeight = bo == BASELINE_OFFSET_SAME_AS_HEIGHT && baselineComplement != -1 ?
                    height - top - bottom - baselineComplement :
                     height - top - bottom;
            if (fillHeight) {
                alt = snapSize(boundedSize(
                        child.minHeight(-1), contentHeight,
                        child.maxHeight(-1)));
            } else {
                alt = snapSize(boundedSize(
                        child.minHeight(-1),
                        child.prefHeight(-1),
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
}