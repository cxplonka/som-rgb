/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package som.core;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public interface LatticeRenderer {

    public static final int STATE_NODE_SELECTED = 0;
    public static final int STATE_NODE_DESELECTED = 1;
    
    public void registerLattice(SOMLattice lat, double[] inputVector);

    public void stateChanged(SOMNode node, int state);
    
    public void update(int iteration);
}
