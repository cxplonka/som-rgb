/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rgbcube.j3d;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.Shape3D;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class RGBCubeNode extends BranchGroup {

    private final double[] borderCoord = {
        0, 0, 0, 1, 0, 0,
        1, 0, 0, 1, 0, 1,
        1, 0, 1, 0, 0, 1,
        0, 0, 1, 0, 0, 0, /* end back plane */
        0, 0, 0, 0, 1, 0,
        0, 1, 0, 0, 1, 1,
        0, 1, 1, 0, 0, 1,
        0, 0, 1, 0, 0, 0, /* end left plane */
        0, 0, 0, 0, 0, 1,
        0, 0, 1, 1, 0, 1,
        1, 0, 1, 1, 1, 1,
        1, 1, 1, 0, 1, 1, /* end up plane */
        0, 0, 0, 1, 0, 0,
        1, 0, 0, 1, 1, 0,
        1, 1, 0, 0, 1, 0, /* end down plane */
        1, 0, 0, 1, 0, 1,
        1, 0, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 0, /* end right plane */};
    private Appearance aborder;

    public RGBCubeNode() {
        super();
        initNode();
    }

    private float[] mapToColors(double[] coords) {
        float[] ret = new float[coords.length];
        /* z = r, y = g, x = b, */
        for (int i = 0; i < ret.length; i += 3) {
            ret[i] = (float) coords[i + 2];
            ret[i + 1] = (float) coords[i + 1];
            ret[i + 2] = (float) coords[i];
        }
        return ret;
    }

    public Appearance getBorderAppearance() {
        return aborder;
    }

    public void setLinesVisible(boolean value) {
        aborder.getRenderingAttributes().setVisible(value);
    }

    public void setBorderVisible(boolean value) {
        for (int i = 0; i < numChildren(); i++) {
            if (getChild(i) instanceof ColorSphere) {
                ColorSphere c = (ColorSphere) getChild(i);
                c.getSphere().getAppearance().getRenderingAttributes().setVisible(value);
            }
        }
    }

    private void initNode() {
        aborder = new Appearance();
        LineAttributes latt = new LineAttributes(1.5f, LineAttributes.PATTERN_SOLID, true);
        aborder.setLineAttributes(latt);
        RenderingAttributes ratt = new RenderingAttributes();
        ratt.setCapability(RenderingAttributes.ALLOW_VISIBLE_WRITE);
        aborder.setRenderingAttributes(ratt);

        LineArray gborder = new LineArray(borderCoord.length / 3,
                LineArray.COORDINATES | LineArray.BY_REFERENCE | LineArray.COLOR_3);
        gborder.setCoordRefDouble(borderCoord);
        gborder.setColorRefFloat(mapToColors(borderCoord));

        Shape3D border = new Shape3D(gborder, aborder);
        addChild(border);

        addChild(new ColorSphere(0, 0, 0));
        addChild(new ColorSphere(1, 0, 0));
        addChild(new ColorSphere(1, 0, 1));
        addChild(new ColorSphere(0, 1, 0));
        addChild(new ColorSphere(0, 1, 1));
        addChild(new ColorSphere(0, 0, 1));
        addChild(new ColorSphere(1, 1, 0));
        addChild(new ColorSphere(1, 1, 1));
    }
}
