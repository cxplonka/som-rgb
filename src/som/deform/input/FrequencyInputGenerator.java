/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package som.deform.input;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import som.core.InputGenerator;
import som.core.SOMLattice;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class FrequencyInputGenerator implements InputGenerator {

    private final Rectangle2D bounds;
    private final int xSteps, ySteps;
    private final double dx, dy;
    private final int[] freq = new int[]{
        1, 1, 0, 1, 1,
        1, 0, 1, 0, 1,
        0, 1, 10, 1, 0,
        1, 0, 1, 0, 1,
        1, 1, 0, 1, 1
    };

    public FrequencyInputGenerator(Rectangle2D bounds, int xSteps, int ySteps) {
        this.bounds = bounds;
        this.xSteps = xSteps;
        this.ySteps = ySteps;
        this.dx = bounds.getWidth() / xSteps;
        this.dy = bounds.getHeight() / ySteps;
//        freq = new int[xSteps * ySteps];
        /* */
//        Random r = new Random();
//        for (int i = 0; i < freq.length; i++) {
//            freq[i] = r.nextInt(3);
//        }
    }

    @Override
    public double[] generateInputVector(SOMLattice lattice) {        
        double xs = bounds.getX();
        double ys = bounds.getY();
        int fidx;
        /* take faster structure - trove */
        List<Double> signals = new ArrayList<Double>(frequency(freq) * 2);
        for (int i = 0; i < xSteps; i++) {
            for (int j = 0; j < ySteps; j++) {
                fidx = j * xSteps + i;
                /* f times this signal into the training set */
                for (int f = 0; f < freq[fidx]; f++) {
                    signals.add(xs + i * dx);
                    signals.add(ys + j * dy);
                }
            }
        }
        return buildDoubleArray(signals);
    }

    private int frequency(int[] f) {
        int ret = 0;
        for (int i = 0; i < f.length; i++) {
            ret += f[i];
        }
        return ret;
    }

    private double[] buildDoubleArray(List<Double> doubles) {
        double[] ret = new double[doubles.size()];
        int i = 0;
        for (Double n : doubles) {
            ret[i++] = n;
        }
        return ret;
    }
}