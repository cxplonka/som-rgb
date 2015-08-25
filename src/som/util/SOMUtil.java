/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package som.util;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import som.core.SOMLattice;
import som.core.SOMNode;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class SOMUtil {

    public static double circumference(SOMLattice lattice) {
        int xs = lattice.w;
        int ys = lattice.h;
        double sum = 0;
        Point2D p1 = new Point2D.Double();
        Point2D p2 = new Point2D.Double();
        /* */
        for (int x = 0; x < xs; x++) {
            for (int y = 0; y < ys; y++) {
                int idx = y * xs + x;
                SOMNode n1 = lattice.getNode(x, y);
                int next = idx + 1 < xs * ys ? idx + 1 : 0;
                SOMNode n2 = lattice.getNode(next % xs, next / ys);
                p1.setLocation(n1.getWeight(0), n1.getWeight(1));
                p2.setLocation(n2.getWeight(0), n2.getWeight(1));
                sum += p1.distance(p2);
            }
        }
        return sum;
    }

    public static void initLatticeRandom(Rectangle2D bounds, SOMLattice lattice) {
        double xmin = bounds.getMinX();
        double ymin = bounds.getMinY();
        int xs = lattice.w;
        int ys = lattice.h;
        double dx = bounds.getWidth() / xs;
        double dy = bounds.getHeight() / ys;
        /* */
        for (int x = 0; x < xs; x++) {
            for (int y = 0; y < ys; y++) {
                SOMNode node = lattice.getNode(x, y);
                node.setWeight(0, xmin + x * dx);
                node.setWeight(1, ymin + y * dy);
            }
        }
    }

    public static void initLatticeWith(Shape shape, SOMLattice lattice) {
        double coords[] = new double[6];
        PathIterator pi = shape.getPathIterator(null);
        int count = 0;
        int lw = lattice.w;
        int lh = lattice.h;
        int size = lw * lh;

        while (!pi.isDone()) {
            switch (pi.currentSegment(coords)) {
                case PathIterator.SEG_MOVETO:
                    if (count < size) {
                        int x = count % lw;
                        int y = count / lw;
                        SOMNode node = lattice.getNode(x, y);
                        node.setWeight(0, coords[0]);
                        node.setWeight(1, coords[1]);
                        count++;
                    }
                    break;
                case PathIterator.SEG_LINETO:
                    if (count < size) {
                        int x = count % lw;
                        int y = count / lw;
                        SOMNode node = lattice.getNode(x, y);
                        node.setWeight(0, coords[0]);
                        node.setWeight(1, coords[1]);
                        count++;
                    }
                    break;
                case PathIterator.SEG_CLOSE:
                    break;
            }
            pi.next();
        }
    }

    public static double circumference(Shape shape) {
        double coords[] = new double[6];
        double lastX = 0, lastY = 0, curX, curY, ret = 0;
        double dx, dy, startx = 0, starty = 0;

        PathIterator pi = shape.getPathIterator(null);
        while (!pi.isDone()) {
            switch (pi.currentSegment(coords)) {
                case PathIterator.SEG_MOVETO:
                    startx = lastX = coords[0];
                    starty = lastY = coords[1];
                    break;
                case PathIterator.SEG_LINETO:
                    curX = coords[0];
                    curY = coords[1];
                    dx = curX - lastX;
                    dy = curY - lastY;
                    ret += Math.sqrt(dx * dx + dy * dy);
                    lastX = curX;
                    lastY = curY;
                    break;
                case PathIterator.SEG_CLOSE:
                    dx = startx - coords[0];
                    dy = starty - coords[1];
                    ret += Math.sqrt(dx * dx + dy * dy);
                    lastX = coords[0];
                    lastY = coords[1];
                    break;
            }
            pi.next();
        }
        return ret;
    }

    public static void renderAsPoints(Graphics2D g2d, Shape shape) {
        double coords[] = new double[6];
        PathIterator pi = shape.getPathIterator(null);
        Rectangle2D r = new Rectangle2D.Double();
        double sizeh = 2.5;

        while (!pi.isDone()) {
            switch (pi.currentSegment(coords)) {
                case PathIterator.SEG_MOVETO:
                    r.setRect(coords[0] - sizeh, coords[1] - sizeh, sizeh * 2, sizeh * 2);
                    g2d.fill(r);
                    break;
                case PathIterator.SEG_LINETO:
                    r.setRect(coords[0] - sizeh, coords[1] - sizeh, sizeh * 2, sizeh * 2);
                    g2d.fill(r);
                    break;
                case PathIterator.SEG_CLOSE:
                    break;
            }
            pi.next();
        }
    }
}