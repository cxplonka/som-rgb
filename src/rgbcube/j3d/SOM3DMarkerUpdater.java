/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rgbcube.j3d;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;
import javax.swing.SwingUtilities;
import som.color.renderer.RGBLattice2DRenderer;
import som.color.renderer.VolumeRGBLattice3DRenderer;
import som.core.LatticeRenderer;
import som.core.SOMLattice;
import som.core.SOMNode;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class SOM3DMarkerUpdater extends MouseAdapter {

    private final RGBLattice2DRenderer renderer2D;
    private final VolumeRGBLattice3DRenderer renderer3D;
    private final Set<Integer> selectedCells = new HashSet<>();

    public SOM3DMarkerUpdater(RGBLattice2DRenderer renderer2D, VolumeRGBLattice3DRenderer renderer3D) {
        this.renderer2D = renderer2D;
        this.renderer3D = renderer3D;
        /* listen at the 2d renderer */
        renderer2D.addMouseMotionListener(this);
        renderer2D.addMouseListener(this);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        /* set visible */
        renderer3D.getMarker().setVisible(true);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        /* set unvisible */
        renderer3D.getMarker().setVisible(false);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        /* update position */
        SOMLattice lattice = renderer2D.getLattice();
        int w = renderer2D.getWidth();
        int h = renderer2D.getHeight();
        double cellWidth = w / (double) lattice.w;
        double cellHeight = h / (double) lattice.h;

        int x = (int) (e.getX() / cellWidth);
        int y = (int) (e.getY() / cellHeight);

        SOMNode node = lattice.getNode(x, y);
        renderer3D.getMarker().setPosition(
                (float) node.getWeight(0),
                (float) node.getWeight(1),
                (float) node.getWeight(2));
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 1) {
            SOMLattice lattice = renderer2D.getLattice();
            if (SwingUtilities.isLeftMouseButton(e)) {
                int w = renderer2D.getWidth();
                int h = renderer2D.getHeight();
                double cellWidth = w / (double) lattice.w;
                double cellHeight = h / (double) lattice.h;

                int x = (int) (e.getX() / cellWidth);
                int y = (int) (e.getY() / cellHeight);

                SOMNode node = lattice.getNode(x, y);
                updateState(node);
            } else if (SwingUtilities.isRightMouseButton(e)) {
                Integer[] indizies = selectedCells.toArray(new Integer[selectedCells.size()]);
                for (int idx : indizies) {
                    int x = idx % lattice.w;
                    int y = idx / lattice.w;
                    SOMNode node = lattice.getNode(x, y);
                    updateState(node);
                }
            }
        } else if(e.getClickCount() == 2){
            /* select all */
            SOMLattice lattice = renderer2D.getLattice();
            for (int x = 0; x < lattice.w; x++) {
                for (int y = 0; y < lattice.h; y++) {
                    updateState(lattice.getNode(x, y));
                }
            }
        }
    }

    public void updateState(SOMNode node) {
        int idx = node.y * renderer2D.getLattice().w + node.x;
        if (selectedCells.contains(idx)) {
            renderer2D.stateChanged(node, LatticeRenderer.STATE_NODE_DESELECTED);
            renderer3D.stateChanged(node, LatticeRenderer.STATE_NODE_DESELECTED);
            selectedCells.remove(idx);
        } else {
            renderer2D.stateChanged(node, LatticeRenderer.STATE_NODE_SELECTED);
            renderer3D.stateChanged(node, LatticeRenderer.STATE_NODE_SELECTED);
            selectedCells.add(idx);
        }
    }
}