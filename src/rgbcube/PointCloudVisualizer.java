/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rgbcube;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import rgbcube.j3d.viewer.PointCloudViewer;
import rgbcube.j3d.viewer.Utils;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class PointCloudVisualizer extends JFrame {

    private PointCloudViewer viewer;

    public PointCloudVisualizer() {
        super("PointCloud");
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        initOwnComponents();
    }

    private void initOwnComponents() {
        add(viewer = new PointCloudViewer(), BorderLayout.CENTER);
        add(new JButton(new LoadMiAction()), BorderLayout.SOUTH);
    }

    class LoadMiAction extends AbstractAction {

        private JFileChooser _chooser;

        public LoadMiAction() {
            super("Load...");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (_chooser == null) {
                _chooser = new JFileChooser();
                _chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            }
            if (_chooser.showOpenDialog((Component) e.getSource()) == JFileChooser.APPROVE_OPTION) {
                try {
                    viewer.setPointCloud(Utils.loadMRA(_chooser.getSelectedFile()));
                } catch (IOException ex) {
                    /* :( */
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] argv) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        final PointCloudVisualizer view = new PointCloudVisualizer();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                view.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        System.exit(0);
                    }
                });
                view.setVisible(true);
            }
        });
    }
}