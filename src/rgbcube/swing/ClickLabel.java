/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rgbcube.swing;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class ClickLabel extends JLabel implements MouseListener {

    public static final String PROPERTY_CLICKED = "mouseClicked";

    public ClickLabel() {
        super();
        addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        firePropertyChange(PROPERTY_CLICKED, null, e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}
