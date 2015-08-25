/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rgbcube.j3d.navigation;

import java.util.Enumeration;
import javax.media.j3d.Alpha;
import javax.media.j3d.Interpolator;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnElapsedFrames;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class RotInterpolator extends Interpolator {

    float prevAlphaValue = Float.NaN;
    private WakeupCriterion passiveWakeupCriterion =
            (WakeupCriterion) new WakeupOnElapsedFrames(0, true);

    public RotInterpolator() {
    }

    public RotInterpolator(Alpha alpha, TransformGroup target) {
        super(alpha);
    }

    public void computeTransform(float alphaValue) {
    }

    @Override
    public void processStimulus(Enumeration criteria) {
        // Handle stimulus
        WakeupCriterion criterion = passiveWakeupCriterion;
        Alpha alpha = getAlpha();

        if (alpha != null) {
            float value = alpha.value();
            if (value != prevAlphaValue) {
                computeTransform(value);
                prevAlphaValue = value;
            }
            if (!alpha.finished() && !alpha.isPaused()) {
                criterion = defaultWakeupCriterion;
            }
        }
        wakeupOn(criterion);
    }
}