/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rgbcube.j3d;

import com.sun.j3d.utils.geometry.Sphere;
import java.awt.Color;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class ColorSphere extends TransformGroup {

    private Sphere sphere;

    public ColorSphere(double x, double y, double z) {
        this((float) z, (float) y, (float) x);
    }

    public ColorSphere(float r, float g, float b) {
        super();
        setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);        
        /* z = r, y = g, x = b, */
        addChild(sphere = Help.createSphere(r, g, b));
        setPosition(r, g, b);
    }

    public Sphere getSphere() {
        return sphere;
    }

    public void setPosition(float r, float g, float b) {
        Transform3D t = new Transform3D();
        t.setTranslation(new Vector3f(b, g, r));
        setTransform(t);
        Help.updateMaterialColor(
                sphere.getAppearance(), new Color(r, g, b));
    }

    public float[] getRGB() {
        Transform3D tmp = new Transform3D();
        getTransform(tmp);
        Vector3d t = new Vector3d();
        tmp.get(t);
        return new float[]{(float) t.z, (float) t.y, (float) t.x};
    }

    public void setVisible(boolean value) {
        sphere.getAppearance().getRenderingAttributes().setVisible(value);
    }
}