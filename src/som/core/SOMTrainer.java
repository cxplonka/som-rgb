/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package som.core;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class SOMTrainer implements Runnable, ITrainer {

    /* These constants can be changed to play with the learning algorithm som = 0.07*/
    private static final double START_LEARNING_RATE = 0.07;
    private static final int NUM_ITERATIONS = 1000;
    /* These two depend on the size of the lattice, so are set later */
    private double LATTICE_RADIUS;
    private double TIME_CONSTANT;
    private LatticeRenderer[] renderer;
    private SOMLattice lattice;
    private double[] inputVector;
    private boolean running;
    private Thread runner;
    private final int dimension;
    private int iteration = 0;

    public SOMTrainer(int dimension) {
        this.dimension = dimension;
        running = false;
    }

    @Override
    public int getDimension() {
        return dimension;
    }

    private double getNeighborhoodRadius(double iteration) {
        return LATTICE_RADIUS * Math.exp(-iteration / TIME_CONSTANT);
    }

    private double getDistanceFalloff(double distSq, double radius) {
        return Math.exp(-(distSq) / (2 * radius * radius));
    }

    @Override
    public void setTraining(SOMLattice lattice, double[] inputVector,
            LatticeRenderer... renderer) {
        if (inputVector.length % dimension != 0) {
            throw new IllegalArgumentException("Wrong InputVector dimension!");
        }
        this.lattice = lattice;
        this.inputVector = inputVector;
        this.renderer = renderer;
    }

    @Override
    public void start() {
        if (lattice != null) {
            runner = new Thread(this);
            runner.setPriority(Thread.MIN_PRIORITY);
            running = true;
            runner.start();
        }
    }

    @Override
    public void run() {
        int lw = lattice.w;
        int lh = lattice.h;
        double dist;
        /* These two values are used in the training algorithm */
        LATTICE_RADIUS = Math.max(lw, lh) / 2;
        /* play around */
        TIME_CONSTANT = (NUM_ITERATIONS / 2) / Math.log(LATTICE_RADIUS);
        /* */
        iteration = 0;
        double nbhRadius, squareRadius;
        SOMNode bmu, temp;

        double[] curInput = new double[dimension];
        double learningRate = START_LEARNING_RATE;

        while (iteration < NUM_ITERATIONS && running) {
            nbhRadius = getNeighborhoodRadius(iteration);
            squareRadius = nbhRadius * nbhRadius;
            /*
             * For each of the input vectors, look for the best matching
             * unit, then adjust the weights for the BMU's neighborhood
             */
            for (int i = 0; i < inputVector.length; i += dimension) {
                /* read one input vector */
                for (int d = 0; d < dimension; d++) {
                    curInput[d] = inputVector[i + d];
                }
                /* find best matching node */
                bmu = lattice.getBMU(curInput);
                /*
                 * We have the BMU for this input now, so adjust everything in
                 * it's neighborhood
                 */
                for (int x = 0; x < lw; x++) {
                    for (int y = 0; y < lh; y++) {
                        temp = lattice.getNode(x, y);
                        dist = bmu.distanceTo(temp);
                        /* in radius */
                        if (dist <= squareRadius) {
                            temp.adjustWeights(
                                    curInput,
                                    learningRate,
                                    getDistanceFalloff(dist, nbhRadius));
                        }
                    }
                }
            }
            iteration++;
            learningRate = START_LEARNING_RATE
                    * Math.exp(-(double) iteration / NUM_ITERATIONS);
            /* */
            for (LatticeRenderer render : renderer) {
                render.update(iteration);
            }
        }
        running = false;
    }

    @Override
    public void stop() {
        if (runner != null) {
            running = false;
            while (runner.isAlive()) {
            }
        }
    }
}
