/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package som.color.input;

import som.core.InputGenerator;
import som.core.SOMLattice;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class RGBInputGenerator implements InputGenerator {

    @Override
    public double[] generateInputVector(SOMLattice lattice) {
        /* generate random rgb inputvector */
        int sColors = 9;
        double[] v = new double[3 * sColors];
        for (int i = 0; i < v.length; i += 3) {
            v[i] = Math.random();
            v[i + 1] = Math.random();
            v[i + 2] = Math.random();
        }
        return v;
    }
}
