/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rgbcube.j3d.viewer;

import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.Color;
import java.awt.Dimension;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import som.color.renderer.PlaneRGBLattice3DRenderer;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class SOMPlaneViewer extends Canvas3D {

    private SimpleUniverse u;
    private final BoundingSphere ibounds = new BoundingSphere(new Point3d(), Double.MAX_VALUE);
    private final PlaneRGBLattice3DRenderer renderer = new PlaneRGBLattice3DRenderer();

    public SOMPlaneViewer() {
        super(SimpleUniverse.getPreferredConfiguration());
        initUniverse(this);
        setMinimumSize(new Dimension(200, 200));
    }

    private void initUniverse(Canvas3D c) {
        u = new SimpleUniverse(c);

        BranchGroup scene = new BranchGroup();
        Background back = new Background(new Color3f(Color.GRAY));
        back.setApplicationBounds(ibounds);
        scene.addChild(back);
        scene.addChild(renderer);

        /* scence.getBounds() not calculate correct */
        u.getViewingPlatform().getViewPlatformTransform().setTransform(
                Utils.centerView(c, new BoundingSphere(
                new Point3d(0.5, 0, 0.5), 0.6)));

        u.addBranchGraph(scene);
    }

    public PlaneRGBLattice3DRenderer getRenderer() {
        return renderer;
    }
}
