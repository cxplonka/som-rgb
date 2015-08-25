/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package som.deform.input;

import java.util.Random;
import som.core.InputGenerator;
import som.core.SOMLattice;

/**
 *
 * @author cplonka
 */
public class GaussianInputGenerator implements InputGenerator {

    private Random r = new Random();

    @Override
    public double[] generateInputVector(SOMLattice lattice) {
        int size = 600, i = 0;
        double[] ret = new double[2 * size];
        for (i = 0; i < size / 2; i++) {
            ret[i * 2] = r.nextGaussian() * 100 + 100;
            ret[i * 2 + 1] = r.nextGaussian() * 100 + 100;
        }
        for (; i < size; i++) {
            ret[i * 2] = r.nextGaussian() * 20 + 300;
            ret[i * 2 + 1] = r.nextGaussian() * 20 + 300;
        }
        return ret;
    }
}