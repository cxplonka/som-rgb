/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package som.deform.renderer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import som.core.LatticeRenderer;
import som.core.SOMLattice;
import som.core.SOMNode;
import som.util.Renderable;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class LayeredLatticeRenderer extends JPanel implements LatticeRenderer {

    private final Font arial = new Font(Font.DIALOG_INPUT, Font.BOLD, 12);
    private SOMLattice lattice;
    private final Rectangle2D rec = new Rectangle2D.Double();
    private final Path2D gridPath = new Path2D.Double();
    private final double markerSizeRed = 3;
    private final double markerSizeBlack = 6;
    private boolean renderLines = true;
    private boolean renderSOM = true;
    /* current values */
    private int iteration = 0;
    private double[] inputVector;
    private List<Renderable> layers;

    public void addRenderable(Renderable renderable){
        if(layers == null){
            layers = new ArrayList<Renderable>();
        }
        layers.add(renderable);
    }
    
    public void removeRenderable(Renderable renderable){
        if(layers != null){
            layers.remove(renderable);
        }
    }
    
    @Override
    public void paint(Graphics gr) {
        super.paint(gr);

        int dim = lattice.dim;
        int lw = lattice.w;
        int lh = lattice.h;
        double halfMarkerRed = markerSizeRed / 2;
        double halfMarkerBlack = markerSizeBlack / 2;

        Graphics2D g2d = (Graphics2D) gr;
        g2d.setBackground(Color.WHITE);
        g2d.clearRect(0, 0, getWidth(), getHeight());
        /* reset old path */
        gridPath.reset();
        /* draw inputvector */
        g2d.setColor(Color.RED);
        for (int i = 0; i < inputVector.length; i += dim) {
            rec.setRect(inputVector[i] - halfMarkerRed,
                    inputVector[i + 1] - halfMarkerRed,
                    markerSizeRed, markerSizeRed);
            g2d.fill(rec);
        }
        /* draw grid lines and nodes */
        g2d.setColor(Color.BLACK);
        if (renderSOM) {
            for (int x = 0; x < lw; x++) {
                for (int y = 0; y < lh; y++) {
                    /* coordinates as weights */
                    SOMNode node = lattice.getNode(x, y);
                    /* draw gridnode marker */
                    rec.setRect(node.getWeight(0) - halfMarkerBlack,
                            node.getWeight(1) - halfMarkerBlack,
                            markerSizeBlack, markerSizeBlack);
                    g2d.fill(rec);
                    /* draw grid lines */
                    if (x + 1 < lw && renderLines) {
                        SOMNode nextNode = lattice.getNode(x + 1, y);
                        gridPath.moveTo(node.getWeight(0), node.getWeight(1));
                        gridPath.lineTo(nextNode.getWeight(0), nextNode.getWeight(1));
                    }
                    if (y + 1 < lh && renderLines) {
                        SOMNode nextNode = lattice.getNode(x, y + 1);
                        gridPath.moveTo(node.getWeight(0), node.getWeight(1));
                        gridPath.lineTo(nextNode.getWeight(0), nextNode.getWeight(1));
                    }
                }
            }
            /* draw grid */
            g2d.draw(gridPath);
        }
        /* draw layers */
        if(layers != null){
            for(Renderable renderable : layers){
                renderable.paint(g2d, getWidth(), getHeight());
            }
        }
        /* status */
        g2d.setColor(Color.BLACK);
        g2d.setFont(arial);
        g2d.drawString("Iteration: " + String.valueOf(iteration), 5, 15);
        g2d.dispose();
    }

    public void setRenderLines(boolean renderLines) {
        this.renderLines = renderLines;
        repaint();
    }

    public boolean isRenderLines() {
        return renderLines;
    }

    public void setRenderSOM(boolean renderSOM) {
        this.renderSOM = renderSOM;
        repaint();
    }

    public boolean isRenderSOM() {
        return renderSOM;
    }

    @Override
    public void registerLattice(SOMLattice lat, double[] inputVector) {
        this.lattice = lat;
        this.inputVector = inputVector;
        repaint();
    }

    @Override
    public void update(int iteration) {
        this.iteration = iteration;
        repaint();
    }

    @Override
    public void stateChanged(SOMNode node, int state) {
    }
}