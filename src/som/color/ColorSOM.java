/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package som.color;

import som.color.renderer.RGBLattice2DRenderer;
import som.color.input.RGBInputGenerator;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import som.core.ITrainer;
import som.core.InputGenerator;
import som.core.SOMLattice;
import som.core.SOMTrainer;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class ColorSOM extends JPanel {

    private static final int DIMENSION = 3;
    private final int lw = 40;
    private final int lh = 40;
    private ITrainer trainer;
    private SOMLattice lattice;
    private final RGBLattice2DRenderer renderer;
    private final InputGenerator inputGenerator = new RGBInputGenerator();
    private double[] inputVector;

    public ColorSOM() {
        super(new BorderLayout());        
        JPanel control = new JPanel(new FlowLayout());
        control.add(new JButton(new StartStopAction()));        
        add(renderer = new RGBLattice2DRenderer(), BorderLayout.CENTER);
        add(control, BorderLayout.SOUTH);
        initSOM();
    }

    private void initSOM() {
        lattice = new SOMLattice(lw, lh, DIMENSION);
        trainer = new SOMTrainer(DIMENSION);
        renderer.registerLattice(lattice, inputVector =
                inputGenerator.generateInputVector(lattice));
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
}