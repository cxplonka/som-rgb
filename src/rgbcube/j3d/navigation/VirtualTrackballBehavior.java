/*
 * VirtualTrackballBehavior.java
 *
 * Created on 26. Oktober 2006, 13:47
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package rgbcube.j3d.navigation;

import com.sun.j3d.internal.J3dUtilsI18N;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.behaviors.vp.ViewPlatformAWTBehavior;
import com.sun.j3d.utils.universe.ViewingPlatform;
import java.awt.AWTEvent;
import java.awt.event.MouseEvent;
import javax.media.j3d.Alpha;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Transform3D;
import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 * @see OrbitBehavior
 */
public class VirtualTrackballBehavior extends ViewPlatformAWTBehavior {

    /**
     * Constructor flag to reverse the rotate behavior
     */
    public static final int REVERSE_ROTATE = 0x010;
    /**
     * Constructor flag to reverse the translate behavior
     */
    public static final int REVERSE_TRANSLATE = 0x020;
    /**
     * Constructor flag to reverse the zoom behavior
     */
    public static final int REVERSE_ZOOM = 0x040;
    /**
     * Constructor flag to reverse all the behaviors
     */
    public static final int REVERSE_ALL = REVERSE_ROTATE | REVERSE_TRANSLATE
            | REVERSE_ZOOM;
    /**
     * Constructor flag that indicates zoom should stop when it reaches
     * the minimum orbit radius set by setMinRadius().  The minimus
     * radius default is 0.0.
     */
    public static final int STOP_ZOOM = 0x100;
    /**
     * Constructor flag to disable rotate
     */
    public static final int DISABLE_ROTATE = 0x200;
    /**
     * Constructor flag to disable translate
     */
    public static final int DISABLE_TRANSLATE = 0x400;
    /**
     * Constructor flag to disable zoom
     */
    public static final int DISABLE_ZOOM = 0x800;
    /**
     * Constructor flag to use proportional zoom, which determines
     * how much you zoom based on view's distance from the center of
     * rotation.  The percentage of distance that the viewer zooms
     * is determined by the zoom factor.
     */
    public static final int PROPORTIONAL_ZOOM = 0x1000;
    /**
     * Used to set the fuction for a mouse button to Rotate
     */
    private static final int ROTATE = 0;
    /**
     * Used to set the function for a mouse button to Translate
     */
    private static final int TRANSLATE = 1;
    /**
     * Used to set the function for a mouse button to Zoom
     */
    private static final int ZOOM = 2;
    private static final double NOMINAL_ZOOM_FACTOR = .01;
    private static final double NOMINAL_PZOOM_FACTOR = 1.0;
    private static final double NOMINAL_TRANS_FACTOR = .01;
    private static final double NOMINAL_ROT_FACTOR = .01;
    private Transform3D longditudeTransform = new Transform3D();
    private Transform3D latitudeTransform = new Transform3D();
    private Transform3D quatRotTransform = new Transform3D();
    private Transform3D rotateTransform = new Transform3D();
    private Transform3D temp1 = new Transform3D();
    private Transform3D temp2 = new Transform3D();
    private Transform3D translation = new Transform3D();
    private Vector3d transVector = new Vector3d();
    private Vector3d distanceVector = new Vector3d();
    private Vector3d centerVector = new Vector3d();
    private Vector3d invertCenterVector = new Vector3d();
    private double startDistanceFromCenter = 20.0;
    private double distanceFromCenter = 20.0;
    private Point3d lookat = new Point3d();
    private Point3d rotationCenter = new Point3d();
    private Matrix3d rotMatrix = new Matrix3d();
    private Transform3D currentXfm = new Transform3D();
    private int mouseX = 0;
    private int mouseY = 0;
    private double rotXFactor = 1.0;
    private double rotYFactor = 1.0;
    private double transXFactor = 1.0;
    private double transYFactor = 1.0;
    private double zoomFactor = 1.0;
    private double xtrans = 0.0;
    private double ytrans = 0.0;
    private double ztrans = 0.0;
    private boolean zoomEnabled = true;
    private boolean rotateEnabled = true;
    private boolean translateEnabled = true;
    private boolean reverseRotate = false;
    private boolean reverseTrans = false;
    private boolean reverseZoom = false;
    private boolean stopZoom = false;
    private boolean proportionalZoom = false;
    private double minRadius = 0.0;
    private int leftButton = ROTATE;
    private int rightButton = TRANSLATE;
    private int middleButton = ZOOM;
    protected boolean trackBallBehavior = false;
    private double longditude = 0.0;
    private double latitude = 0.0;
    private float wheelZoomFactor = 50.0f;
    private double rotXMul = NOMINAL_ROT_FACTOR * rotXFactor;
    private double rotYMul = NOMINAL_ROT_FACTOR * rotYFactor;
    private double transXMul = NOMINAL_TRANS_FACTOR * transXFactor;
    private double transYMul = NOMINAL_TRANS_FACTOR * transYFactor;
    private double zoomMul = NOMINAL_ZOOM_FACTOR * zoomFactor;
    private double[] curQuad = new double[4];
    private Quat4d quat = new Quat4d(0f, 0f, 0f, 1f);
    private double[] rot = new double[16];
    private RotInterpolator rotInterpolator;
    private long stime, alphaTime = 10000;
    private int msx, msy, mex, mey;

    /**
     * Parameterless constructor for this behavior.  This is intended for use
     * by ConfiguredUniverse, which requires such a constructor for
     * configurable behaviors.  The Canvas3D used to listen for mouse and
     * mouse motion events is obtained from the superclass
     * setViewingPlatform() method.
     * @since Java 3D 1.3
     */
    public VirtualTrackballBehavior() {
        super(MOUSE_LISTENER | MOUSE_MOTION_LISTENER | MOUSE_WHEEL_LISTENER);
        setPickable(false);
    }

    /**
     * Creates a new OrbitBehavior
     *
     * @param c The Canvas3D to add the behavior to
     */
    public VirtualTrackballBehavior(Canvas3D c) {
        this(c, 0);
    }

    /**
     * Creates a new OrbitBehavior
     *
     * @param c The Canvas3D to add the behavior to
     * @param flags The option flags
     */
    public VirtualTrackballBehavior(Canvas3D c, int flags) {
        super(c,
                MOUSE_LISTENER | MOUSE_MOTION_LISTENER | MOUSE_WHEEL_LISTENER
                | flags);
        setPickable(false);
        
        if ((flags & DISABLE_ROTATE) != 0) {
            rotateEnabled = false;
        }
        
        if ((flags & DISABLE_ZOOM) != 0) {
            zoomEnabled = false;
        }
        
        if ((flags & DISABLE_TRANSLATE) != 0) {
            translateEnabled = false;
        }
        
        if ((flags & REVERSE_TRANSLATE) != 0) {
            reverseTrans = true;
        }
        
        if ((flags & REVERSE_ROTATE) != 0) {
            reverseRotate = true;
        }
        
        if ((flags & REVERSE_ZOOM) != 0) {
            reverseZoom = true;
        }
        
        if ((flags & STOP_ZOOM) != 0) {
            stopZoom = true;
        }
        
        if ((flags & PROPORTIONAL_ZOOM) != 0) {
            proportionalZoom = true;
            zoomMul = NOMINAL_PZOOM_FACTOR * zoomFactor;
        }
    }
    
    @Override
    protected synchronized void processAWTEvents(final AWTEvent[] events) {
        motion = false;
        for (int i = 0; i < events.length; i++) {
            if (events[i] instanceof MouseEvent) {
                processMouseEvent((MouseEvent) events[i]);
            }
        }
    }
    int xchange, ychange;
    
    protected void processMouseEvent(final MouseEvent evt) {
        if (evt.getID() == MouseEvent.MOUSE_PRESSED) {
            rotInterpolator.setEnable(false);
            stime = evt.getWhen();
            /* */
            msx = mouseX = evt.getX();
            msy = mouseY = evt.getY();
            motion = true;
        } else if (evt.getID() == MouseEvent.MOUSE_DRAGGED) {
            xchange = evt.getX() - mouseX;
            ychange = evt.getY() - mouseY;
            // rotate
            if (rotate(evt)) {
                if (!trackBallBehavior) {
                    if (reverseRotate) {
                        longditude -= xchange * rotXMul;
                        latitude -= ychange * rotYMul;
                    } else {
                        longditude += xchange * rotXMul;
                        latitude += ychange * rotYMul;
                    }
                } else {
                    int w = canvases[0].getWidth();
                    int h = canvases[0].getHeight();
                    float sx = h / (float) w;
                    //trackball
                    float p1x = (mouseX / (w / 2f)) - 1;
                    float p1y = 1 - (mouseY / (h / 2f));
                    float p2x = (evt.getX() / (w / 2f)) - 1;
                    float p2y = 1 - (evt.getY() / (h / 2f));
                    /* */
                    Trackball.trackball(curQuad, p1x / sx, p1y, p2x / sx, p2y, 1);
                    quat.set(curQuad);
                    if (!reverseRotate) {
                        quat.conjugate();
                    }
                }
            } else if (translate(evt)) {
                if (reverseTrans) {
                    xtrans -= (xchange * transXMul);
                    ytrans += (ychange * transYMul);
                } else {
                    xtrans += (xchange * transXMul);
                    ytrans -= (ychange * transYMul);
                }
            } else if (zoom(evt)) {
                doZoomOperations(ychange);
            }
            mouseX = evt.getX();
            mouseY = evt.getY();
            motion = true;
        } else if (evt.getID() == MouseEvent.MOUSE_RELEASED) {
            /* let's spin */            
            if (Math.abs(xchange) > 1 || Math.abs(ychange) > 1) {
                double time = evt.getWhen() - stime;
                
                mex = (int) (evt.getX() + ((evt.getX() - msx) * alphaTime) / time);
                mey = (int) (evt.getY() + ((evt.getY() - msy) * alphaTime) / time);
                
                rotInterpolator.setEnable(true);
            }
        } else if (evt.getID() == MouseEvent.MOUSE_WHEEL) {
            if (zoom(evt)) {
                // if zooming is done through mouse wheel,
                // the amount of increments the wheel changed,
                // multiplied with wheelZoomFactor is used,
                // so that zooming speed looks natural compared to mouse movement zoom.
                if (evt instanceof java.awt.event.MouseWheelEvent) {
                    // I/O differenciation is made between
                    // java.awt.event.MouseWheelEvent.WHEEL_UNIT_SCROLL or
                    // java.awt.event.MouseWheelEvent.WHEEL_BLOCK_SCROLL so
                    // that behavior remains stable and not dependent on OS settings.
                    // If getWheelRotation() was used for calculating the zoom,
                    // the zooming speed could act differently on different platforms,
                    // if, for example, the user sets his mouse wheel to jump 10 lines
                    // or a block.
                    int zoom = (int) (((java.awt.event.MouseWheelEvent) evt).getWheelRotation() * wheelZoomFactor);
                    doZoomOperations(zoom);
                    motion = true;
                }
            }
        }
    }
    
    public void setTrackBallBehavior(boolean trackBallBehavior) {
        this.trackBallBehavior = trackBallBehavior;
    }
    
    public boolean isTrackBallBehavior() {
        return trackBallBehavior;
    }

    /*extraction of the zoom algorithms so that there is no code duplication or source 'uglyfication'.
     */
    private void doZoomOperations(int ychange) {
        if (proportionalZoom) {
            if (reverseZoom) {
                if ((distanceFromCenter
                        - ((zoomMul * ychange * distanceFromCenter) / 100.0)) > minRadius) {
                    distanceFromCenter -= ((zoomMul * ychange * distanceFromCenter) / 100.0);
                } else {
                    distanceFromCenter = minRadius;
                }
            } else {
                if ((distanceFromCenter
                        + ((zoomMul * ychange * distanceFromCenter) / 100.0)) > minRadius) {
                    distanceFromCenter += ((zoomMul * ychange * distanceFromCenter) / 100.0);
                } else {
                    distanceFromCenter = minRadius;
                }
            }
        } else {
            if (stopZoom) {
                if (reverseZoom) {
                    if ((distanceFromCenter - (ychange * zoomMul)) > minRadius) {
                        distanceFromCenter -= (ychange * zoomMul);
                    } else {
                        distanceFromCenter = minRadius;
                    }
                } else {
                    if ((distanceFromCenter + (ychange * zoomMul)) > minRadius) {
                        distanceFromCenter += (ychange * zoomMul);
                    } else {
                        distanceFromCenter = minRadius;
                    }
                }
            } else {
                if (reverseZoom) {
                    distanceFromCenter -= (ychange * zoomMul);
                } else {
                    distanceFromCenter += (ychange * zoomMul);
                }
            }
        }
    }

    /**
     * Sets the ViewingPlatform for this behavior.  This method is
     * called by the ViewingPlatform.
     * If a sub-calls overrides this method, it must call
     * super.setViewingPlatform(vp).
     * NOTE: Applications should <i>not</i> call this method.
     */
    @Override
    public void setViewingPlatform(ViewingPlatform vp) {
        super.setViewingPlatform(vp);
        
        if (vp != null) {
            rotInterpolator = new RotInterpolator(new Alpha(-1, alphaTime), null) {
                
                float last = 0;
                
                @Override
                public void setEnable(boolean bln) {
                    /* reset current interpolation state */
                    if (bln) {
                        getAlpha().setStartTime(System.currentTimeMillis());
                    }
                    super.setEnable(bln);
                    last = 0;
                }
                
                @Override
                public void computeTransform(float f) {
                    /* http://www.mail-archive.com/java3d-interest@java.sun.com/msg22350.html */
                    /* check for first run and next cycle */
                    if (last != 0 && Math.abs(last - f) < 0.9) {
                        double xchange = f * (mex - msx) - last * (mex - msx);
                        double ychange = f * (mey - msy) - last * (mey - msy);
                        /* slow down */
                        xchange /= 2;
                        ychange /= 2;
                        
                        if (reverseRotate) {
                            longditude -= xchange * rotXMul;
                            latitude -= ychange * rotYMul;
                        } else {
                            longditude += xchange * rotXMul;
                            latitude += ychange * rotYMul;
                        }
                        integrateTransforms();
                    }
                    last = f;
                }
            };
            rotInterpolator.setEnable(false);
            rotInterpolator.setSchedulingBounds(
                    new BoundingSphere(new Point3d(), Float.MAX_VALUE));
            
            BranchGroup group = new BranchGroup();
            group.addChild(rotInterpolator);
            vp.addChild(group);
            /* */
            resetView();
            integrateTransforms();
        }
    }
    
    public void resetRotation() {
        rotInterpolator.setEnable(false);
        rotateTransform.setIdentity();
        
        integrateTransforms();
    }

    /**
     * Reset the orientation and distance of this behavior to the current
     * values in the ViewPlatform Transform Group
     */
    private void resetView() {
        Vector3d centerToView = new Vector3d();
        
        targetTG.getTransform(targetTransform);
        
        targetTransform.get(rotMatrix, transVector);
        centerToView.sub(transVector, rotationCenter);
        distanceFromCenter = centerToView.length();
        startDistanceFromCenter = distanceFromCenter;
        
        targetTransform.get(rotMatrix);
        rotateTransform.set(rotMatrix);

        // compute the initial x/y/z offset
        temp1.set(centerToView);
        rotateTransform.invert();
        rotateTransform.mul(temp1);
        rotateTransform.get(centerToView);
        xtrans = centerToView.x;
        ytrans = centerToView.y;
        ztrans = centerToView.z;

        // resetRotation rotMatrix
        rotateTransform.set(rotMatrix);
    }
    
    @Override
    protected synchronized void integrateTransforms() {
        // Check if the transform has been changed by another
        // behavior
        targetTG.getTransform(currentXfm);
        
        if (!targetTransform.equals(currentXfm)) {
            resetView();
        }

        //trackball
        if (!rotInterpolator.getEnable() && trackBallBehavior) {
            quat.get(curQuad);
            Trackball.build_rotmatrix(rot, curQuad);
            quatRotTransform.set(rot);
            rotateTransform.mul(quatRotTransform);
        } else {
            longditudeTransform.rotY(longditude);
            latitudeTransform.rotX(latitude);
            rotateTransform.mul(rotateTransform, latitudeTransform);
            rotateTransform.mul(rotateTransform, longditudeTransform);
        }
        
        distanceVector.z = distanceFromCenter - startDistanceFromCenter;
        
        setTransXFactor(distanceFromCenter / 10);
        setTransYFactor(distanceFromCenter / 10);
        
        temp1.set(distanceVector);
        temp1.mul(rotateTransform, temp1);

        // want to look at rotationCenter
        transVector.x = rotationCenter.x + xtrans;
        transVector.y = rotationCenter.y + ytrans;
        transVector.z = rotationCenter.z + ztrans;
        
        translation.set(transVector);
        targetTransform.mul(temp1, translation);

        // handle rotationCenter
        temp1.set(centerVector);
        temp1.mul(targetTransform);
        
        invertCenterVector.x = -centerVector.x;
        invertCenterVector.y = -centerVector.y;
        invertCenterVector.z = -centerVector.z;
        
        temp2.set(invertCenterVector);
        targetTransform.mul(temp1, temp2);
        
        targetTG.setTransform(targetTransform);
        targetTG.getTransform(currentXfm);
        
        resetView();

        //reset
        longditude = 0.0;
        latitude = 0.0;
    }

    /**
     * Sets the center around which the View rotates.
     * The default is (0,0,0).
     * @param center The Point3d to set the center of rotation to
     */
    public synchronized void setRotationCenter(Point3d center) {
        rotationCenter.x = center.x;
        rotationCenter.y = center.y;
        rotationCenter.z = center.z;
        centerVector.set(rotationCenter);
        lookat.set(rotationCenter);
    }

    /**
     * Property which sets the center around which the View rotates.
     * Used by ConfiguredUniverse.
     * @param center array of length 1 containing an instance of Point3d
     * @since Java 3D 1.3
     */
    public void RotationCenter(Object[] center) {
        if (!((center.length == 1) && center[0] instanceof Point3d)) {
            throw new IllegalArgumentException(
                    "RotationCenter must be a single Point3d");
        }
        
        setRotationCenter((Point3d) center[0]);
    }

    /**
     * Places the value of the center around which the View rotates
     * into the Point3d.
     * @param center The Point3d
     */
    public void getRotationCenter(Point3d center) {
        center.x = rotationCenter.x;
        center.y = rotationCenter.y;
        center.z = rotationCenter.z;
    }

    // TODO
    // Need to add key factors for Rotate, Translate and Zoom
    // Method calls should just update MAX_KEY_ANGLE, KEY_TRANSLATE and
    // KEY_ZOOM
    //
    // Methods also need to correctly set sign of variables depending on
    // the Reverse settings.
    /**
     * Sets the rotation x and y factors.  The factors are used to determine
     * how many radians to rotate the view for each pixel of mouse movement.
     * The view is rotated factor * 0.01 radians for each pixel of mouse
     * movement.  The default factor is 1.0.
     * @param xfactor The x movement multiplier
     * @param yfactor The y movement multiplier
     **/
    public synchronized void setRotFactors(float xfactor, float yfactor) {
        rotXFactor = xfactor;
        rotYFactor = yfactor;
        rotXMul = NOMINAL_ROT_FACTOR * xfactor;
        rotYMul = NOMINAL_ROT_FACTOR * yfactor;
        
        rotXMul = xfactor;
        rotYMul = yfactor;
    }

    /**
     * Sets the rotation x factor.  The factors are used to determine
     * how many radians to rotate the view for each pixel of mouse movement.
     * The view is rotated factor * 0.01 radians for each pixel of mouse
     * movement.  The default factor is 1.0.
     * @param xfactor The x movement multiplier
     **/
    public synchronized void setRotXFactor(float xfactor) {
        rotXFactor = xfactor;
        rotXMul = NOMINAL_ROT_FACTOR * xfactor;
        rotXMul = xfactor;
    }

    /**
     * Set reverse rotate behavior.  The default is false.
     * @param state if true, reverse rotate behavior
     * @since Java 3D 1.3
     */
    public void setReverseRotate(boolean state) {
        reverseRotate = state;
    }
    
    public boolean isReverseRotate() {
        return reverseRotate;
    }

    /**
     * Sets the rotation y factor.  The factors are used to determine
     * how many radians to rotate the view for each pixel of mouse movement.
     * The view is rotated factor * 0.01 radians for each pixel of mouse
     * movement.  The default factor is 1.0.
     * @param yfactor The y movement multiplier
     **/
    public synchronized void setRotYFactor(float yfactor) {
        rotYFactor = yfactor;
        rotYMul = NOMINAL_ROT_FACTOR * yfactor;
    }

    /**
     * Sets the translation x and y factors.  The factors are used to determine
     * how many units to translate the view for each pixel of mouse movement.
     * The view is translated factor * 0.01 units for each pixel of mouse
     * movement.  The default factor is 1.0.
     * @param xfactor The x movement multiplier
     * @param yfactor The y movement multiplier
     **/
    public synchronized void setTransFactors(double xfactor, double yfactor) {
        transXFactor = xfactor;
        transYFactor = yfactor;
        transXMul = NOMINAL_TRANS_FACTOR * xfactor;
        transYMul = NOMINAL_TRANS_FACTOR * yfactor;
    }

    /**
     * Property which sets the translation x and y factors.
     * Used by ConfiguredUniverse.
     * @param factors array of length 2 containing instances of Double
     * @since Java 3D 1.3
     */
    public void TransFactors(Object[] factors) {
        if (!((factors.length == 2) && factors[0] instanceof Double
                && factors[1] instanceof Double)) {
            throw new IllegalArgumentException(
                    "TransFactors must be two Doubles");
        }
        
        setTransFactors(((Double) factors[0]).doubleValue(),
                ((Double) factors[1]).doubleValue());
    }

    /**
     * Sets the translation x factor.  The factors are used to determine
     * how many units to translate the view for each pixel of mouse movement.
     * The view is translated factor * 0.01 units for each pixel of mouse
     * movement.  The default factor is 1.0.
     * @param xfactor The x movement multiplier
     **/
    public synchronized void setTransXFactor(double xfactor) {
        transXFactor = xfactor;
        transXMul = NOMINAL_TRANS_FACTOR * xfactor;
    }

    /**
     * Sets the translation y factor.  The factors are used to determine
     * how many units to translate the view for each pixel of mouse movement.
     * The view is translated factor * 0.01 units for each pixel of mouse
     * movement.  The default factor is 1.0.
     * @param yfactor The y movement multiplier
     **/
    public synchronized void setTransYFactor(double yfactor) {
        transYFactor = yfactor;
        transYMul = NOMINAL_TRANS_FACTOR * yfactor;
    }

    /**
     * Sets the zoom factor.  The factor is used to determine how many
     * units to zoom the view for each pixel of mouse movement.
     * The view is zoomed factor * 0.01 units for each pixel of mouse
     * movement.  For proportional zoom, the view is zoomed factor * 1%
     * of the distance from the center of rotation for each pixel of
     * mouse movement.  The default factor is 1.0.
     * @param zfactor The movement multiplier
     */
    public synchronized void setZoomFactor(double zfactor) {
        zoomFactor = zfactor;
        
        if (proportionalZoom) {
            zoomMul = NOMINAL_PZOOM_FACTOR * zfactor;
        } else {
            zoomMul = NOMINAL_ZOOM_FACTOR * zfactor;
        }
    }

    /**
     * Returns the x rotation movement multiplier
     * @return The movement multiplier for x rotation
     */
    public double getRotXFactor() {
        return rotXFactor;
    }

    /**
     * Returns the y rotation movement multiplier
     * @return The movement multiplier for y rotation
     */
    public double getRotYFactor() {
        return rotYFactor;
    }

    /**
     * Returns the x translation movement multiplier
     * @return The movement multiplier for x translation
     */
    public double getTransXFactor() {
        return transXFactor;
    }

    /**
     * Returns the y translation movement multiplier
     * @return The movement multiplier for y translation
     */
    public double getTransYFactor() {
        return transYFactor;
    }

    /**
     * Returns the zoom movement multiplier
     * @return The movement multiplier for zoom
     */
    public double getZoomFactor() {
        return zoomFactor;
    }

    /**
     * Enables or disables rotation.  The default is true.
     * @param enabled true or false to enable or disable rotate
     */
    public synchronized void setRotateEnable(boolean enabled) {
        rotateEnabled = enabled;
    }

    /**
     * Enables or disables zoom. The default is true.
     * @param enabled true or false to enable or disable zoom
     */
    public synchronized void setZoomEnable(boolean enabled) {
        zoomEnabled = enabled;
    }

    /**
     * Enables or disables translate. The default is true.
     * @param enabled true or false to enable or disable translate
     */
    public synchronized void setTranslateEnable(boolean enabled) {
        translateEnabled = enabled;
    }

    /**
     * Retrieves the state of rotate enabled
     * @return the rotate enable state
     */
    public boolean getRotateEnable() {
        return rotateEnabled;
    }

    /**
     * Retrieves the state of zoom enabled
     * @return the zoom enable state
     */
    public boolean getZoomEnable() {
        return zoomEnabled;
    }

    /**
     * Retrieves the state of translate enabled
     * @return the translate enable state
     */
    public boolean getTranslateEnable() {
        return translateEnabled;
    }
    
    boolean rotate(MouseEvent evt) {
        if (rotateEnabled) {
            if ((leftButton == ROTATE)
                    && (!evt.isAltDown() && !evt.isMetaDown())) {
                return true;
            }
            
            if ((middleButton == ROTATE)
                    && (evt.isAltDown() && !evt.isMetaDown())) {
                return true;
            }
            
            if ((rightButton == ROTATE)
                    && (!evt.isAltDown() && evt.isMetaDown())) {
                return true;
            }
        }
        
        return false;
    }
    
    boolean zoom(MouseEvent evt) {
        if (zoomEnabled) {
            if (evt instanceof java.awt.event.MouseWheelEvent) {
                return true;
            }
            
            if ((leftButton == ZOOM)
                    && (!evt.isAltDown() && !evt.isMetaDown())) {
                return true;
            }
            
            if ((middleButton == ZOOM)
                    && (evt.isAltDown() && !evt.isMetaDown())) {
                return true;
            }
            
            if ((rightButton == ZOOM)
                    && (!evt.isAltDown() && evt.isMetaDown())) {
                return true;
            }
        }
        
        return false;
    }
    
    boolean translate(MouseEvent evt) {
        if (translateEnabled) {
            if ((leftButton == TRANSLATE)
                    && (!evt.isAltDown() && !evt.isMetaDown())) {
                return true;
            }
            
            if ((middleButton == TRANSLATE)
                    && (evt.isAltDown() && !evt.isMetaDown())) {
                return true;
            }
            
            if ((rightButton == TRANSLATE)
                    && (!evt.isAltDown() && evt.isMetaDown())) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Sets the minimum radius for the OrbitBehavior.  The zoom will
     * stop at this distance from the center of rotation.  The default
     * is 0.0.  The minimum will have no affect if the STOP_ZOOM constructor
     * flag is not set.
     * @param r the minimum radius
     * @exception IllegalArgumentException if the radius is less than 0.0
     */
    public synchronized void setMinRadius(double r) {
        if (r < 0.0) {
            throw new IllegalArgumentException(J3dUtilsI18N.getString(
                    "OrbitBehavior1"));
        }
        
        minRadius = r;
    }

    /**
     * Returns the minimum orbit radius.  The zoom will stop at this distance
     * from the center of rotation if the STOP_ZOOM constructor flag is set.
     * @return the minimum radius
     */
    public double getMinRadius() {
        return minRadius;
    }

    /**
     * Set reverse translate behavior.  The default is false.
     * @param state if true, reverse translate behavior
     * @since Java 3D 1.3
     */
    public void setReverseTranslate(boolean state) {
        reverseTrans = state;
    }

    /**
     * Set reverse zoom behavior.  The default is false.
     * @param state if true, reverse zoom behavior
     * @since Java 3D 1.3
     */
    public void setReverseZoom(boolean state) {
        reverseZoom = state;
    }

    /**
     * Set proportional zoom behavior.  The default is false.
     * @param state if true, use proportional zoom behavior
     * @since Java 3D 1.3
     */
    public synchronized void setProportionalZoom(boolean state) {
        proportionalZoom = state;
        
        if (state) {
            zoomMul = NOMINAL_PZOOM_FACTOR * zoomFactor;
        } else {
            zoomMul = NOMINAL_ZOOM_FACTOR * zoomFactor;
        }
    }
}