/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package som.core;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class SOMNode {

    private final double[] weights;
    public final int x, y;

    public SOMNode(int x, int y, int dimension) {
        this.x = x;
        this.y = y;
        weights = new double[dimension];
        /* init random, normalized to 1 */
        for (int i = 0; i < weights.length; i++) {
            weights[i] = Math.random();
        }
    }

    public double[] getWeights() {
        return weights;
    }

    /**
     * distance for the SOM Grid(Node index distance)
     * @param n
     * @return 
     */
    public double distanceTo(SOMNode n) {
        return Math.pow(x - n.x, 2) + Math.pow(y - n.y, 2);
    }

    /**
     * euclidean distance between the multidimensional weights
     * @param w
     * @return 
     */
    public double euclideanDist(double[] w) {
        double sum = 0;
        for (int i = 0; i < weights.length; i++) {
            sum += Math.pow(weights[i] - w[i], 2);
        }
        return sum;
    }

    public void setWeight(int idx, double value) {
        if (idx >= weights.length) {
            throw new IllegalArgumentException("Illegal index value!");
        }
        weights[idx] = value;
    }

    public double getWeight(int idx) {
        if (idx >= weights.length) {
            throw new IllegalArgumentException("Illegal index value!");
        }
        return weights[idx];
    }

    public void adjustWeights(double[] inputVector, double learningRate, double distanceFalloff) {
        for (int i = 0; i < weights.length; i++) {
            /* 
             * decay function - dW - 2008 - villmann take persoan correlation derivate 
             * http://redalyc.uaemex.mx/pdf/925/92503705.pdf
             */
            weights[i] += distanceFalloff * learningRate * (inputVector[i] - weights[i]);
        }
    }

    public void randomInit() {
        /* init random, normalized to 1 */
        for (int i = 0; i < weights.length; i++) {
            weights[i] = Math.random();
        }
    }
}
