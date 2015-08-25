/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rgbcube.j3d.util;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public final class LinearColorMap {

    private final List<LinearColorMap.Knot> knots = new LinkedList<LinearColorMap.Knot>();

    public LinearColorMap() {
        super();
    }

    public LinearColorMap(float[] fractions, Color[] colors) {
        this();
        setColorMap(fractions, colors);
    }

    public void setColorMap(float[] fractions, Color[] colors) {
        knots.clear();
        for (int i = 0; i < fractions.length; i++) {
            addKnot(fractions[i], colors[i]);
        }
    }

    public float[] getFractions() {
        float[] ret = new float[knots.size()];
        for (int i = 0; i < knots.size(); i++) {
            ret[i] = knots.get(i).fraction;
        }
        return ret;
    }

    public Color[] getColors() {
        Color[] ret = new Color[knots.size()];
        for (int i = 0; i < knots.size(); i++) {
            ret[i] = knots.get(i).color;
        }
        return ret;
    }

    /**
     * 0..1 fraction
     *
     * @param fraction
     * @param color
     */
    public void addKnot(float fraction, Color color) {
        ListIterator<Knot> itr = knots.listIterator();
        Knot newKnot = new Knot(fraction, color);

        while (itr.hasNext()) {
            Knot knot = (Knot) itr.next();

            if (knot.fraction == fraction) {
                itr.remove();
                itr.add(newKnot);

                return;
            }

            if (knot.fraction >= fraction) {
                itr.add(newKnot);

                return;
            }
        }

        itr.add(newKnot);
    }

    public int getRGB(double value) {
        Knot prevKnot = null;
        Knot knot = null;
        Iterator itr = knots.iterator();

        while (itr.hasNext()) {
            prevKnot = knot;
            knot = (Knot) itr.next();

            if (knot.fraction > value) {
                break;
            }
        }

        if (prevKnot == null) {
            return knot.color.getRGB();
        }

        if (knot == null) {
            return prevKnot.color.getRGB();
        }

        if (prevKnot.fraction == knot.fraction) {
            return prevKnot.color.getRGB();
        }

        double v = (value - prevKnot.fraction) / (knot.fraction
                - prevKnot.fraction);

        return interpolateRGB(prevKnot.color, knot.color, v);
    }

    public Color getColor(double value) {
        return new Color(getRGB(value));
    }

    private double clamp(double value) {
        if (value > 1f) {
            return 1f;
        }

        if (value < 0f) {
            return 0;
        }

        return value;
    }

    private int interpolateRGB(Color c1, Color c2, double fraction) {
        double frac = clamp(fraction);
        double c = 1f - frac;

        int a = (int) ((c1.getAlpha() * c) + (c2.getAlpha() * frac));
        int r = (int) ((c1.getRed() * c) + (c2.getRed() * frac));
        int g = (int) ((c1.getGreen() * c) + (c2.getGreen() * frac));
        int b = (int) ((c1.getBlue() * c) + (c2.getBlue() * frac));

        return ((a & 0xFF) << 24)
                | ((r & 0xFF) << 16)
                | ((g & 0xFF) << 8)
                | ((b & 0xFF));
    }

    private class Knot {

        public float fraction;
        public Color color;

        public Knot(float fraction, Color color) {
            this.fraction = fraction;
            this.color = color;
        }
    }
}