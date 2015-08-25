/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rgbcube;

import java.awt.event.ActionEvent;
import rgbcube.j3d.Help;
import rgbcube.swing.ColorSelectionPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.media.j3d.Transform3D;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;
import rgbcube.j3d.ColorSphere;
import rgbcube.j3d.viewer.CubeViewer;
import rgbcube.j3d.viewer.SOMPlaneViewer;
import som.core.SOMLattice;
import som.core.SOMTrainer;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class RGBCubeEntry extends JFrame implements PropertyChangeListener {

    private final ColorSelectionPanel panel = new ColorSelectionPanel();
    private final CubeViewer cviewer = new CubeViewer();
    private final SOMPlaneViewer pviewer = new SOMPlaneViewer();
    /* SOM extension */
    private final SOMLattice lattice = new SOMLattice(100, 100, 3);
    private final SOMTrainer trainer = new SOMTrainer(3);

    public RGBCubeEntry() {
        super("RGBCube");
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        initOwnComponents();
    }

    private void initOwnComponents() {
        panel.addPropertyChangeListener(this);
        pviewer.getRenderer().registerLattice(lattice, new double[]{});
        cviewer.getRenderer().registerLattice(lattice, new double[]{});

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension scrd = toolkit.getScreenSize();

        panel.setSOMAction(new StartAction());

        JSplitPane split3d = new JSplitPane();
        split3d.setLeftComponent(cviewer);
        split3d.setRightComponent(pviewer);
        split3d.setDividerLocation((int) (scrd.getWidth() / 2));

        JSplitPane viewSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        viewSplit.setLeftComponent(split3d);
        viewSplit.setRightComponent(panel);
        viewSplit.setDividerLocation((int) (scrd.getHeight() - (scrd.getHeight() / 4)));

        add(viewSplit, BorderLayout.CENTER);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(ColorSelectionPanel.PROPERTY_ADD_SPHERE)) {
            cviewer.addSphere(evt.getNewValue());
        } else if (evt.getPropertyName().equals(ColorSelectionPanel.PROPERTY_REMOVE_SPHERE)) {
            cviewer.removeSphere(evt.getNewValue());
        } else if (evt.getPropertyName().equals(ColorSelectionPanel.PROPERTY_COLOR_CHANGE)) {
            ColorSphere sphere = cviewer.getSphere(panel.getSelectedValue());
            if (sphere != null && evt.getNewValue() instanceof Color) {
                Color color = (Color) evt.getNewValue();
                Help.updateMaterialColor(sphere.getSphere().getAppearance(), color);
                /* parent must be transform group */
                Transform3D t = new Transform3D();
                /* z = r, y = g, x = b, */
                t.setTranslation(new Vector3d(color.getBlue() / 255f,
                        color.getGreen() / 255f, color.getRed() / 255f));
                sphere.setTransform(t);
            }
        } else if (evt.getPropertyName().equals(ColorSelectionPanel.PROPERTY_SELECTION_SPHERE)) {
            ColorSphere sphere = cviewer.getSphere(evt.getNewValue());
            if (sphere != null) {
                Color3f c = new Color3f();
                sphere.getSphere().getAppearance().getColoringAttributes().getColor(c);
                panel.setSelectedColor(c.get());
            }
        } else if (evt.getPropertyName().equals(ColorSelectionPanel.PROPERTY_SHOW_BOUNDS)) {
            cviewer.getCubeNode().setLinesVisible((Boolean) evt.getNewValue());
        } else if (evt.getPropertyName().equals(ColorSelectionPanel.PROPERTY_SHOW_BOUNDS_COLOR)) {
            cviewer.getCubeNode().setBorderVisible((Boolean) evt.getNewValue());
        } else if (evt.getPropertyName().equals(ColorSelectionPanel.PROPERTY_I_LOVE_TRACKBALL)) {
            cviewer.getTrackball().setTrackBallBehavior((Boolean) evt.getNewValue());
        } else if (evt.getPropertyName().equals(ColorSelectionPanel.PROPERTY_SOM_LINES)) {
            cviewer.getRenderer().setLinesVisible((Boolean) evt.getNewValue());
        } else if (evt.getPropertyName().equals(ColorSelectionPanel.PROPERTY_SOM_POINTS)) {
            cviewer.getRenderer().setPointsVisible((Boolean) evt.getNewValue());
        } else if (evt.getPropertyName().equals(ColorSelectionPanel.PROPERTY_SOM_SURFACE)) {
            cviewer.getRenderer().setSurfaceVisible((Boolean) evt.getNewValue());
        } else if (evt.getPropertyName().equals(ColorSelectionPanel.PROPERTY_SOM_RENDER_WHITE)) {
            cviewer.getRenderer().setRenderSOMPointsWhite((Boolean) evt.getNewValue());
        } else if (evt.getPropertyName().equals(ColorSelectionPanel.PROPERTY_SOM_RANDOM_INIT)) {
            reinitSOM();
        }
    }

    private void reinitSOM() {
        lattice.randomInit();
        cviewer.getRenderer().update(0);
        pviewer.getRenderer().update(0);
    }

    class StartAction extends AbstractAction {

        public StartAction() {
            super("Let's SOM!");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            List<ColorSphere> list = new ArrayList(
                    cviewer.getSpheres());
            if (!list.isEmpty()) {
                trainer.stop();
                /* */
                double[] inputVector = new double[3 * list.size()];
                /* */
                for (int i = 0; i < list.size(); i++) {
                    float[] rgb = list.get(i).getRGB();
                    inputVector[i * 3] = rgb[0];
                    inputVector[i * 3 + 1] = rgb[1];
                    inputVector[i * 3 + 2] = rgb[2];
                }
                lattice.randomInit();
                trainer.setTraining(lattice, inputVector,
                        cviewer.getRenderer(), pviewer.getRenderer(), panel);
                cviewer.getRenderer().registerLattice(lattice, inputVector);
                pviewer.getRenderer().registerLattice(lattice, inputVector);
                trainer.start();
            }
        }
    }

    public static void main(String[] argv) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        final RGBCubeEntry cube = new RGBCubeEntry();

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                cube.setExtendedState(JFrame.MAXIMIZED_BOTH);
                cube.addWindowListener(new WindowAdapter() {

                    @Override
                    public void windowClosing(WindowEvent e) {
                        System.exit(0);
                    }
                    
                });
                cube.setVisible(true);
            }
        });
    }
}