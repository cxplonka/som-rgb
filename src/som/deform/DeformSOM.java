/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package som.deform;

import som.deform.input.DeformInputGenerator;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import som.core.ITrainer;
import som.core.InputGenerator;
import som.core.SOMLattice;
import som.core.SOMTrainer;
import som.deform.renderer.LayeredLatticeRenderer;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class DeformSOM extends JPanel {

    private static final int DIMENSION = 2;
    private final int lw = 15;
    private final int lh = 15;
    private ITrainer trainer;
    private SOMLattice lattice;
    private final LayeredLatticeRenderer renderer;
    private final InputGenerator generator = new DeformInputGenerator();
    private double[] inputVector;

    public DeformSOM() {
        super(new BorderLayout());
        add(renderer = new LayeredLatticeRenderer(), BorderLayout.CENTER);
        JPanel control = new JPanel(new FlowLayout());
        control.add(new JButton(new StartStopAction()));
        control.add(new JCheckBox(new ShowLinesAction()));
        control.add(new JCheckBox(new ShowSOMAction()));
        add(control, BorderLayout.SOUTH);
        initSOM();
    }

    private void initSOM() {
        lattice = new SOMLattice(lw, lh, DIMENSION);
        trainer = new SOMTrainer(DIMENSION);
        renderer.registerLattice(lattice,
                inputVector = generator.generateInputVector(lattice));
    }

    class StartStopAction extends AbstractAction {

        private boolean started = false;

        public StartStopAction() {
            super("Start!");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            trainer.stop();
            if (!started) {
                putValue(NAME, "Stop!");
                trainer.setTraining(lattice, inputVector, renderer);
                renderer.registerLattice(lattice, inputVector);
                trainer.start();
            } else {
                putValue(NAME, "Start!");
            }
            started = !started;
        }
    }

    class ShowLinesAction extends AbstractAction {

        public ShowLinesAction() {
            super("Lines");
            putValue(SELECTED_KEY, renderer.isRenderLines());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof JCheckBox) {
                boolean selected = ((JCheckBox) e.getSource()).isSelected();
                renderer.setRenderLines(selected);
            }
        }
    }

    class ShowSOMAction extends AbstractAction {

        public ShowSOMAction() {
            super("SOM");
            putValue(SELECTED_KEY, renderer.isRenderSOM());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof JCheckBox) {
                boolean selected = ((JCheckBox) e.getSource()).isSelected();
                renderer.setRenderSOM(selected);
            }
        }
    }
}