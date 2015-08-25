/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package som.core;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class SOMLattice {

    public final int w, h;
    private final SOMNode[][] matrix;
    public final int dim;

    public SOMLattice(int w, int h, int dimension) {
        this.w = w;
        this.h = h;
        this.dim = dimension;
        matrix = new SOMNode[w][h];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                matrix[x][y] = new SOMNode(x, y, dimension);
            }
        }
    }

    public SOMNode getNode(int x, int y) {
        return matrix[x][y];
    }

    /** Finds the best matching unit for the given
     *  inputVector
     * @param inputVector
     * @return 
     */
    public SOMNode getBMU(double[] inputVector) {
        /* Start out assuming that 0,0 is our best matching unit */
        SOMNode bmu = matrix[0][0];
        double bestDist = bmu.euclideanDist(inputVector);
        double curDist;
        /*
         * Now step through the entire matrix and check the euclidean distance
         * between the input vector and the given node
         */
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                curDist = matrix[x][y].euclideanDist(inputVector);
                if (curDist < bestDist) {
                    /*
                     * If the distance from the current node to the input vector
                     * is less than the distance to our current BMU, we have a 
                     * new BMU
                     */
                    bmu = matrix[x][y];
                    bestDist = curDist;
                }
            }
        }
        return bmu;
    }

    public void randomInit() {
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                matrix[x][y].randomInit();
            }
        }
    }
}