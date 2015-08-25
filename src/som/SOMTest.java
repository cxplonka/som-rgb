/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package som;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import som.color.ColorSOM;
import som.deform.DeformSOM;

/**
 * http://www.ai-junkie.com/ann/som/som1.html
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class SOMTest extends JFrame {

    private final JTabbedPane pane = new JTabbedPane();

    public SOMTest() {
        super("SOM - RGB");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        /* create views */
        pane.addTab("DeformSOM", new DeformSOM());
        pane.addTab("ColorSOM", new ColorSOM());
        add(pane, BorderLayout.CENTER);
        setSize(550, 550);
    }

    public static void main(String[] arg) {
        final SOMTest test = new SOMTest();

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                test.setVisible(true);
            }
        });
    }
}
