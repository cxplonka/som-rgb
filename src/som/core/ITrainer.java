/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package som.core;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public interface ITrainer {

    public int getDimension();
    
    public void setTraining(SOMLattice lattice, double[] inputVector,
            LatticeRenderer... renderer);

    public void start();

    public void stop();
}
