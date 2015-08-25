/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rgbcube.j3d;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Sphere;
import java.awt.Color;
import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.RenderingAttributes;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.vecmath.Color3f;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class Help {

    public static void updateMaterialColor(Appearance app, Color c) {
        Material m = app.getMaterial();

        m.setAmbientColor(new Color3f(c.darker().darker().darker().darker()));
        m.setEmissiveColor(new Color3f(0f, 0f, 0f));
        m.setDiffuseColor(new Color3f(c));
        m.setSpecularColor(new Color3f(c.darker().darker()));
        m.setShininess(60);

        app.getColoringAttributes().setColor(new Color3f(c));
    }

    public static Sphere createSphere(float r, float g, float b) {
        Appearance sapp = new Appearance();
        RenderingAttributes ratt = new RenderingAttributes();
        ratt.setCapability(RenderingAttributes.ALLOW_VISIBLE_WRITE);
        sapp.setRenderingAttributes(ratt);

        Material m = new Material();
        m.setCapability(Material.ALLOW_COMPONENT_WRITE);
        sapp.setMaterial(m);

        ColoringAttributes catt = new ColoringAttributes();
        catt.setCapability(ColoringAttributes.ALLOW_COLOR_READ);
        catt.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);
        sapp.setColoringAttributes(catt);

        Help.updateMaterialColor(sapp, new Color(r, g, b));

        return new Sphere(.05f, Sphere.GENERATE_NORMALS, 50, sapp);
    }

    public static Box createBox(float r, float g, float b, float size) {
        Appearance sapp = new Appearance();
        RenderingAttributes ratt = new RenderingAttributes();
        ratt.setCapability(RenderingAttributes.ALLOW_VISIBLE_WRITE);
        sapp.setRenderingAttributes(ratt);

        Material m = new Material();
        m.setCapability(Material.ALLOW_COMPONENT_WRITE);
        sapp.setMaterial(m);

        ColoringAttributes catt = new ColoringAttributes();
        catt.setCapability(ColoringAttributes.ALLOW_COLOR_READ);
        catt.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);
        sapp.setColoringAttributes(catt);

        Help.updateMaterialColor(sapp, new Color(r, g, b));

        return new Box(size, size, size, sapp);
    }
    
    public static void registerKeyBoardAction(JComponent comp, Action action) {
        registerKeyBoardAction(comp, action, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    /**
     * 
     * @param comp
     * @param action
     * @param condition - see {@link JComponent}
     * (WHEN_FOCUSED, WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,WHEN_IN_FOCUSED_WINDOW)
     */
    public static void registerKeyBoardAction(JComponent comp, Action action, int condition) {
        comp.getInputMap(condition).put((KeyStroke) action.getValue(
                action.ACCELERATOR_KEY), action.getValue(action.NAME));
        comp.getActionMap().put(action.getValue(action.NAME), action);
    }
}
