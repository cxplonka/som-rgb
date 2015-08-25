/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rgbcube.j3d;

import com.sun.j3d.utils.geometry.Box;
import java.awt.Color;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class ColorBox extends TransformGroup {

    private Box box;

    public ColorBox(double x, double y, double z) {
        this((float) z, (float) y, (float) x);
    }

    public ColorBox(float r, float g, float b) {
        this(r, g, b, .05f);
    }

    public ColorBox(float r, float g, float b, float size) {
        super();
        setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        /* z = r, y = g, x = b, */
        addChild(box = Help.createBox(r, g, b, size));
        setPosition(r, g, b);
    }

    public Box getBox() {
        return box;
    }

    public void setPosition(float r, float g, float b) {
        Transform3D t = new Transform3D();
        t.setTranslation(new Vector3f(b, g, r));
        setTransform(t);
        Help.updateMaterialColor(box.getAppearance(), new Color(r, g, b));
    }

    public float[] getRGB() {
        Transform3D tmp = new Transform3D();
        getTransform(tmp);
        Vector3d t = new Vector3d();
        tmp.get(t);
        return new float[]{(float) t.z, (float) t.y, (float) t.x};
    }

    public void setVisible(boolean value) {
        box.getAppearance().getRenderingAttributes().setVisible(value);
    }
}