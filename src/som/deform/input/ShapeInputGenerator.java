/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package som.deform.input;

import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;
import som.core.InputGenerator;
import som.core.SOMLattice;

/**
 *
 * @author cplonka
 */
public class ShapeInputGenerator implements InputGenerator {

    private Shape shape;

    public ShapeInputGenerator() {
    }

    public ShapeInputGenerator(Shape shape) {
        this.shape = shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public Shape getShape() {
        return shape;
    }

    @Override
    public double[] generateInputVector(SOMLattice lattice) {
        if (shape != null) {
            return toPoints(shape);
        }
        return new double[]{};
    }

    public double[] toPoints(Shape shape) {
        List<Double> points = new ArrayList<Double>();
        double coords[] = new double[6];
        PathIterator pi = shape.getPathIterator(null);

        while (!pi.isDone()) {
            switch (pi.currentSegment(coords)) {
                case PathIterator.SEG_MOVETO:
                    points.add(coords[0]);
                    points.add(coords[1]);
                    break;
                case PathIterator.SEG_LINETO:
                    points.add(coords[0]);
                    points.add(coords[1]);
                    break;
                case PathIterator.SEG_CLOSE:
                    break;
            }
            pi.next();
        }
        return toPrimitive(points);
    }

    private double[] toPrimitive(List<Double> list) {
        double[] ret = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            ret[i] = list.get(i);
        }
        return ret;
    }
}