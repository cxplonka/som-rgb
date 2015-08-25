/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rgbcube.j3d.viewer;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.universe.PlatformGeometry;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;
import rgbcube.j3d.util.PointGeometry;
import rgbcube.j3d.StaticPointSetShape;
import rgbcube.j3d.navigation.VirtualTrackballBehavior;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class PointCloudViewer extends Canvas3D {

    private SimpleUniverse u;
    private final BoundingSphere ibounds = new BoundingSphere(new Point3d(), Double.MAX_VALUE);
    private Transform3D home = new Transform3D();
    private final BranchGroup geometryBranch = new BranchGroup();
    private final KeyListener listener = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyChar()) {
                case 'r':
                    VirtualTrackballBehavior b = (VirtualTrackballBehavior) u.getViewingPlatform().getViewPlatformBehavior();
                    b.setHomeTransform(home);
                    b.goHome();
                    break;
            }
        }
    };

    public PointCloudViewer() {
        super(SimpleUniverse.getPreferredConfiguration());
        addKeyListener(listener);
        initUniverse(this);
        setMinimumSize(new Dimension(200, 200));
    }

    private void initUniverse(Canvas3D c) {
        u = new SimpleUniverse(c);

        geometryBranch.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        geometryBranch.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        geometryBranch.setCapability(BranchGroup.ALLOW_DETACH);
        geometryBranch.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);

        BranchGroup scene = new BranchGroup();
        Background back = new Background(new Color3f(Color.GRAY));
        back.setApplicationBounds(ibounds);
        scene.addChild(back);
        scene.addChild(geometryBranch);

        /* head light */
        PlatformGeometry pgeom = new PlatformGeometry();
        pgeom.addChild(createLight());
        u.getViewingPlatform().setPlatformGeometry(pgeom);

        VirtualTrackballBehavior ob = new VirtualTrackballBehavior(c, OrbitBehavior.REVERSE_ALL);
        ob.setRotationCenter(new Point3d(0.5, 0.5, 0.5));
        ob.setProportionalZoom(true);
        ob.setSchedulingBounds(ibounds);
        ob.setTrackBallBehavior(true); //MUSTTTTTTT!!!!!!!!!!!!!! :)
        ob.setMinRadius(0);

        u.getViewingPlatform().setViewPlatformBehavior(ob);

        c.getView().setScreenScalePolicy(View.SCALE_EXPLICIT);
        c.getView().setTransparencySortingPolicy(View.TRANSPARENCY_SORT_GEOMETRY);
        c.getView().setFrontClipPolicy(View.VIRTUAL_EYE);
        c.getView().setBackClipPolicy(View.VIRTUAL_EYE);
        c.getView().setBackClipDistance(10000);

        home.set(Utils.centerView(c, new BoundingSphere(scene.getBounds())));
        u.getViewingPlatform().getViewPlatformTransform().setTransform(home);

        u.addBranchGraph(scene);
    }

    private TransformGroup createLight() {
        TransformGroup light = new TransformGroup();
        light.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        BranchGroup bgLight = new BranchGroup();
        bgLight.setPickable(false);
        light.addChild(bgLight);

        Color3f lightColor1 = new Color3f(1.0f, 1.0f, 1.0f);
        AmbientLight light1 = new AmbientLight(lightColor1);
        light1.setInfluencingBounds(ibounds);

        Vector3f lightDir2 = new Vector3f(0.0f, 0f, -1f);
        DirectionalLight light2 = new DirectionalLight(lightColor1, lightDir2);
        light2.setInfluencingBounds(ibounds);

        bgLight.addChild(light1);
        bgLight.addChild(light2);

        return light;
    }

    public void setPointCloud(PointGeometry coords) {
        geometryBranch.removeAllChildren();
        BranchGroup group = new BranchGroup();
        group.setCapability(BranchGroup.ALLOW_DETACH);
        StaticPointSetShape shape = new StaticPointSetShape(coords);
        group.addChild(shape);

        BoundingSphere sphere = new BoundingSphere(shape.getBounds());        

        VirtualTrackballBehavior b = (VirtualTrackballBehavior) u.getViewingPlatform().getViewPlatformBehavior();
        Point3d center = new Point3d();
        sphere.getCenter(center);
        b.setRotationCenter(center);        
        
        home.set(Utils.centerView(this, sphere));
        
        u.getViewingPlatform().getViewPlatformTransform().setTransform(home);

        geometryBranch.addChild(group);
    }
}