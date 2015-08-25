/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package som.color.renderer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import som.core.LatticeRenderer;
import som.core.SOMLattice;
import som.core.SOMNode;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class RGBLattice2DRenderer extends JPanel implements LatticeRenderer {

    private final Rectangle2D rec = new Rectangle2D.Double();
    private final Line2D line = new Line2D.Double();
    private final Font arial = new Font("Arial", Font.BOLD, 12);
    private SOMLattice lattice = null;
    private int iteration = 0;
    private Set<Integer> selectedCells;

    public RGBLattice2DRenderer() {
        super();
    }

    @Override
    public void paint(Graphics gr) {
        super.paint(gr);
        int w = getWidth();
        int h = getHeight();
        double cellWidth = w / (double) lattice.w;
        double cellHeight = h / (double) lattice.h;

        float r, g, b;
        Graphics2D g2 = (Graphics2D) gr;

        for (int x = 0; x < lattice.w; x++) {
            for (int y = 0; y < lattice.h; y++) {
                SOMNode node = lattice.getNode(x, y);
                r = (float) node.getWeight(0);
                g = (float) node.getWeight(1);
                b = (float) node.getWeight(2);
                g2.setColor(new Color(r, g, b));
                rec.setRect(x * cellWidth, y * cellHeight, cellWidth, cellHeight);
                g2.fill(rec);
                /* outline SOM cell */
                g2.setColor(Color.BLACK);
                g2.draw(rec);
            }
        }
        /* render all selected cells */
        if (selectedCells != null && !selectedCells.isEmpty()) {
            g2.setColor(Color.RED);
            for (int idx : selectedCells) {
                int x = idx % lattice.w;
                int y = idx / lattice.w;
                double xs = x * cellWidth;
                double ys = y * cellHeight;
                rec.setRect(xs, ys, cellWidth, cellHeight);
                g2.draw(rec);
                line.setLine(xs, ys, xs + cellWidth, ys + cellHeight);
                g2.draw(line);
                line.setLine(xs + cellWidth, ys, xs, ys + cellHeight);
                g2.draw(line);
            }
        }
        g2.setColor(Color.BLACK);
        g2.setFont(arial);
        g2.drawString("Iteration: " + String.valueOf(iteration), 5, 15);
        g2.dispose();
    }

    @Override
    public void registerLattice(SOMLattice lat, double[] inputVector) {
        lattice = lat;
    }

    public SOMLattice getLattice() {
        return lattice;
    }

    @Override
    public void update(int iteration) {
        this.iteration = iteration;
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                repaint();
            }
        });
    }

    @Override
    public void stateChanged(SOMNode node, int state) {
        /* lazy */
        if (selectedCells == null) {
            selectedCells = new HashSet<Integer>();
        }
        /* index of som node */
        int idx = node.y * lattice.w + node.x;
        if (state == LatticeRenderer.STATE_NODE_SELECTED) {
            selectedCells.add(idx);
        } else if (state == LatticeRenderer.STATE_NODE_DESELECTED) {
            selectedCells.remove(idx);
        }
        repaint();
    }
}