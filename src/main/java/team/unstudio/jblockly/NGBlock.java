package team.unstudio.jblockly;

import static com.sun.prism.shape.ShapeRep.InvalidationType.LOCATION_AND_GEOMETRY;

import com.sun.javafx.geom.Shape;
import com.sun.javafx.sg.prism.NGRegion;
import com.sun.prism.BasicStroke;
import com.sun.prism.Graphics;
import com.sun.prism.PrinterGraphics;
import com.sun.prism.impl.PrismSettings;
import com.sun.prism.paint.Paint;
import com.sun.prism.shape.ShapeRep;

import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;

public class NGBlock extends NGRegion{
    //NGSVGPath
    private Shape path;

    public void setContent(Object content) {
        path = (Shape)content;
        geometryChanged();
    }

    public Object getGeometry() {
        return path;
    }

    public Shape getShape() {
    	return path;
    }

    //NGShape
    public enum Mode { EMPTY, FILL, STROKE, STROKE_FILL }
    
    protected Paint fillPaint;
    protected Paint drawPaint;
    protected BasicStroke drawStroke;
    protected Mode mode = Mode.FILL;
    protected ShapeRep shapeRep;
    private boolean smooth;

    public void setMode(Mode mode) {
        if (mode != this.mode) {
            this.mode = mode;
            geometryChanged();
        }
    }

    public Mode getMode() {
        return mode;
    }

    public void setSmooth(boolean smooth) {
        smooth = !PrismSettings.forceNonAntialiasedShape && smooth;
        if (smooth != this.smooth) {
            this.smooth = smooth;
            visualsChanged();
        }
    }

    public boolean isSmooth() {
        return smooth;
    }

    public void setFillPaint(Object fillPaint) {
        if (fillPaint != this.fillPaint ||
                (this.fillPaint != null && this.fillPaint.isMutable()))
        {
            this.fillPaint = (Paint) fillPaint;
            visualsChanged();
            invalidateOpaqueRegion();
        }
    }

    public Paint getFillPaint() {
        return fillPaint;
    }

    public void setDrawPaint(Object drawPaint) {
        if (drawPaint != this.drawPaint ||
                (this.drawPaint != null && this.drawPaint.isMutable()))
        {
            this.drawPaint = (Paint) drawPaint;
            visualsChanged();
        }
    }

    public void setDrawStroke(BasicStroke drawStroke) {
        if (this.drawStroke != drawStroke) {
            this.drawStroke = drawStroke;
            geometryChanged();
        }
    }

    public void setDrawStroke(float strokeWidth,
                              StrokeType strokeType,
                              StrokeLineCap lineCap, StrokeLineJoin lineJoin,
                              float strokeMiterLimit,
                              float[] strokeDashArray, float strokeDashOffset)
    {
        int type;
        if (strokeType == StrokeType.CENTERED) {
            type = BasicStroke.TYPE_CENTERED;
        } else if (strokeType == StrokeType.INSIDE) {
            type = BasicStroke.TYPE_INNER;
        } else {
            type = BasicStroke.TYPE_OUTER;
        }

        int cap;
        if (lineCap == StrokeLineCap.BUTT) {
            cap = BasicStroke.CAP_BUTT;
        } else if (lineCap == StrokeLineCap.SQUARE) {
            cap = BasicStroke.CAP_SQUARE;
        } else {
            cap = BasicStroke.CAP_ROUND;
        }

        int join;
        if (lineJoin == StrokeLineJoin.BEVEL) {
            join = BasicStroke.JOIN_BEVEL;
        } else if (lineJoin == StrokeLineJoin.MITER) {
            join = BasicStroke.JOIN_MITER;
        } else {
            join = BasicStroke.JOIN_ROUND;
        }

        if (drawStroke == null) {
            drawStroke = new BasicStroke(type, strokeWidth, cap, join, strokeMiterLimit);
        } else {
            drawStroke.set(type, strokeWidth, cap, join, strokeMiterLimit);
        }
        if (strokeDashArray.length > 0) {
            drawStroke.set(strokeDashArray, strokeDashOffset);
        } else {
            drawStroke.set((float[])null, 0f);
        }

        geometryChanged();
    }

    protected ShapeRep createShapeRep(Graphics g) {
        return g.getResourceFactory().createPathRep();
    }

    @Override
    protected void renderContent(Graphics g) {
        if (mode == Mode.EMPTY) {
            return;
        }
        final boolean printing = g instanceof PrinterGraphics;
        renderBackground(g, printing);
        
        super.renderContent(g);
    }

    protected void renderBackground(Graphics g, boolean printing) {

        // Set smooth property on shape
        boolean saveAA = g.isAntialiasedShape();
        boolean isAA = isSmooth();
        if (isAA != saveAA) {
            g.setAntialiasedShape(isAA);
        }

        ShapeRep localShapeRep = printing ? null : this.shapeRep;
        if (localShapeRep == null) {
            localShapeRep = createShapeRep(g);
        }
        Shape shape = getShape();
        if (mode != Mode.STROKE) {
            g.setPaint(fillPaint);
            localShapeRep.fill(g, shape, contentBounds);
        }
        if (mode != Mode.FILL && drawStroke.getLineWidth() > 0) {
            g.setPaint(drawPaint);
            g.setStroke(drawStroke);
            localShapeRep.draw(g, shape, contentBounds);
        }

        if (isAA != saveAA) {
            g.setAntialiasedShape(saveAA);
        }
        if (!printing) {
            this.shapeRep = localShapeRep;
        }
    }

    @Override
    protected boolean hasOverlappingContents() {
        return mode == Mode.STROKE_FILL;
    }

    protected Shape getStrokeShape() {
        return drawStroke.createStrokedShape(getShape());
    }

    @Override
    protected void geometryChanged() {
        super.geometryChanged();
        if (shapeRep != null) {
            shapeRep.invalidate(LOCATION_AND_GEOMETRY);
        }
    }

    @Override
    protected boolean hasOpaqueRegion() {
        final Mode mode = getMode();
        final Paint fillPaint = getFillPaint();
        return super.hasOpaqueRegion() &&
                    (mode == Mode.FILL || mode == Mode.STROKE_FILL) &&
                    (fillPaint != null && fillPaint.isOpaque());
    }
}
