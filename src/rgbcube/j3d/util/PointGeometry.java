/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rgbcube.j3d.util;

import java.nio.FloatBuffer;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class PointGeometry {
    
    public final FloatBuffer coordBuffer;

    public PointGeometry(FloatBuffer coordBuffer) {
        this.coordBuffer = coordBuffer;
    }    
}