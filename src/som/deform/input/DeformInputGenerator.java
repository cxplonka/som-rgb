/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package som.deform.input;

import java.awt.Font;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.util.Arrays;
import som.core.InputGenerator;
import som.core.SOMLattice;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class DeformInputGenerator implements InputGenerator {

    @Override
    public double[] generateInputVector(SOMLattice lattice) {
        /* normalize inputvector */
        int w = 10;
        int h = 10;
        double[] v = new double[2 * w * h];
        double dx = 400d / w;
        double dy = 400d / h;
        /* grid points */
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int idx = (y * w + x) * 2;
                v[idx] = x * dx + 50;
                v[idx + 1] = y * dy + 50;
            }
        }
        /* some signal */
        double[] signal = new double[8];
        for (int i = 0; i < signal.length; i += 2) {
            signal[i] = w / 2d * dx + 50;
            signal[i + 1] = h / 2d * dy + 50;
        }
        /* */
        signal = create();
        double[] ret = Arrays.copyOf(v, v.length + signal.length);
        System.arraycopy(signal, 0, ret, v.length, signal.length);

        return signal;
    }

    private double[] create() {
        double[] v = new double[2 * 200];
        double w = 350;
        double h = 350;

        Font font = new Font(Font.DIALOG, Font.BOLD, 300);
        GlyphVector glyphVector = font.createGlyphVector(
                new FontRenderContext(null, false, false),
                "A");
        Shape p = glyphVector.getOutline(150, 350);

        int i = 0;
        while (i < v.length) {
            double x = Math.random() * w;
            double y = Math.random() * h;
            if (p.contains(x, y)) {
                v[i++] = x;
                v[i++] = y;
            }
        }

        return v;
    }
}