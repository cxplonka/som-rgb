/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rgbcube.j3d;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.J3DBuffer;
import javax.media.j3d.PointArray;
import javax.media.j3d.PointAttributes;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import rgbcube.j3d.util.LinearColorMap;
import rgbcube.j3d.util.PointGeometry;
import rgbcube.j3d.util.Range;
import rgbcube.j3d.viewer.Utils;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class StaticPointSetShape extends Shape3D {

    private final LinearColorMap _colorMap = new LinearColorMap(
            new float[]{0f, 0.25f, 0.5f, 0.75f, 1f},
            new Color[]{Color.BLUE, Color.GREEN, Color.YELLOW, Color.RED, Color.MAGENTA});

    public StaticPointSetShape(PointGeometry geometry) {
        super();
        initPrimitive(geometry);
    }

    private Range createRange(FloatBuffer buffer) {
        float[] coord = new float[3];
        Range ret = new Range(Float.MAX_VALUE, -Float.MAX_VALUE);
        while (buffer.hasRemaining()) {
            buffer.get(coord);
            ret.add(coord[2]);
        }
        buffer.rewind();
        return ret;
    }

    private void initPrimitive(PointGeometry geometry) {
        setPickable(false);

        /* create colors */
        FloatBuffer coordBuffer = geometry.coordBuffer;
        Range range = createRange(coordBuffer);
        /* */
        float[] coord = new float[3];
        ByteBuffer rgbBuffer = Utils.createDirectByteBuffer(coordBuffer.capacity());
        while (coordBuffer.hasRemaining()) {
            coordBuffer.get(coord);
            int argb = _colorMap.getRGB(range.normalize(coord[2]));
            rgbBuffer.put((byte) ((argb >> 16) & 0xFF));
            rgbBuffer.put((byte) ((argb >> 8) & 0xFF));
            rgbBuffer.put((byte) (argb & 0xFF));
        }
        coordBuffer.rewind();
        /* create appearance */
        Appearance app = new Appearance();

        app.setColoringAttributes(new ColoringAttributes(new Color3f(Color.GREEN),
                ColoringAttributes.NICEST));
        app.setPointAttributes(new PointAttributes(2f, false));
        setAppearance(app);

        int flags = PointArray.COORDINATES | PointArray.BY_REFERENCE
                | PointArray.USE_NIO_BUFFER | PointArray.COLOR_3;
        PointArray pointGeometry = new PointArray(geometry.coordBuffer.capacity() / 3, flags);
        pointGeometry.setCoordRefBuffer(new J3DBuffer(geometry.coordBuffer));
        pointGeometry.setColorRefBuffer(new J3DBuffer(rgbBuffer));

        setGeometry(pointGeometry);
    }
}