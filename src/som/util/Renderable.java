/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package som.util;

import java.awt.Graphics2D;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public interface Renderable {
    
    public void paint(Graphics2D g2d, int w, int h);
}
