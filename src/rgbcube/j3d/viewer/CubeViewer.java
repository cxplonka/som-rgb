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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;
import rgbcube.j3d.ColorSphere;
import rgbcube.j3d.RGBCubeNode;
import rgbcube.j3d.SOM3DMarkerUpdater;
import rgbcube.j3d.navigation.VirtualTrackballBehavior;
import som.color.renderer.VolumeRGBLattice3DRenderer;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class CubeViewer extends Canvas3D {

    private SimpleUniverse u;
    private final BranchGroup sphereGroup = new BranchGroup();
    private RGBCubeNode cubeNode;
    private final Map<Object, ColorSphere> shapes = new HashMap<Object, ColorSphere>();
    private final BoundingSphere ibounds = new BoundingSphere(new Point3d(), Double.MAX_VALUE);
    private final VolumeRGBLattice3DRenderer renderer = new VolumeRGBLattice3DRenderer();
    private Transform3D home = new Transform3D();
    private SOM3DMarkerUpdater markerUpdater;
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

    public CubeViewer() {
        super(SimpleUniverse.getPreferredConfiguration());
        addKeyListener(listener);
        initUniverse(this);
        setMinimumSize(new Dimension(200, 200));
        //        markerUpdater = new SOM3DMarkerUpdater(renderer2D, renderer3D);
    }

    private void initUniverse(Canvas3D c) {
        u = new SimpleUniverse(c);

        sphereGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        sphereGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);

        BranchGroup scene = new BranchGroup();
        scene.addChild(sphereGroup);
        Background back = new Background(new Color3f(Color.GRAY));
        back.setApplicationBounds(ibounds);
        scene.addChild(back);

        /* head light */
        PlatformGeometry pgeom = new PlatformGeometry();
        pgeom.addChild(createLight());
        u.getViewingPlatform().setPlatformGeometry(pgeom);

        scene.addChild(cubeNode = new RGBCubeNode());
        scene.addChild(renderer);

        VirtualTrackballBehavior ob = new VirtualTrackballBehavior(c, OrbitBehavior.REVERSE_ALL);
        ob.setRotationCenter(new Point3d(0.5, 0.5, 0.5));
        ob.setProportionalZoom(true);
        ob.setSchedulingBounds(ibounds);
        ob.setMinRadius(0);

        u.getViewingPlatform().setViewPlatformBehavior(ob);

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

    public VirtualTrackballBehavior getTrackball() {
        return (VirtualTrackballBehavior) u.getViewingPlatform().getViewPlatformBehavior();
    }

    public Collection<ColorSphere> getSpheres() {
        return shapes.values();
    }

    public VolumeRGBLattice3DRenderer getRenderer() {
        return renderer;
    }

    public RGBCubeNode getCubeNode() {
        return cubeNode;
    }

    public void addSphere(Object object) {
        BranchGroup gr = new BranchGroup();
        gr.setCapability(BranchGroup.ALLOW_DETACH);
        gr.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        gr.setUserData(object);

        ColorSphere sphere = new ColorSphere(
                (float) Math.random(),
                (float) Math.random(),
                (float) Math.random());
        shapes.put(object, sphere);
        gr.addChild(sphere);
        sphereGroup.addChild(gr);
    }

    public void removeSphere(Object object) {
        int s = sphereGroup.numChildren();
        for (int i = 0; i < s; i++) {
            Node child = sphereGroup.getChild(i);
            if (child.getUserData().equals(object)) {
                ((BranchGroup) child).detach();
                shapes.remove(object);
                break;
            }
        }
    }

    public ColorSphere getSphere(Object object) {
        return shapes.get(object);
    }
}