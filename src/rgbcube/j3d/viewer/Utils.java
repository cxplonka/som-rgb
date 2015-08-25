/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rgbcube.j3d.viewer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import rgbcube.j3d.util.PointGeometry;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class Utils {

    public static Transform3D centerView(Canvas3D canvas, BoundingSphere sphere) {
        Transform3D locator = new Transform3D();
        Point3d center = new Point3d();
        sphere.getCenter(center);

        locator.lookAt(centerView(sphere, canvas.getView().getFieldOfView()),
                center, new Vector3d(0, 0, 1));
        locator.invert();
        return locator;
    }

    private static Point3d centerView(BoundingSphere bounds, double fieldOfView) {
        double eyeDist = (1.0 * bounds.getRadius()) / Math.tan(fieldOfView / 2.0);

        Point3d center = new Point3d();
        bounds.getCenter(center);

        Vector3d direction = new Vector3d(0, -1, 0);

        Vector3d x = new Vector3d(center);
        direction.normalize();
        direction.scale(eyeDist);
        x.add(direction);

        return new Point3d(x);
    }

    public static IntBuffer createDirectIntBuffer(final int size) {
        return ByteBuffer.allocateDirect(4 * size).order(ByteOrder.nativeOrder()).asIntBuffer();
    }

    public static FloatBuffer createDirectFloatBuffer(final int size) {
        return ByteBuffer.allocateDirect(4 * size).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }

    public static ByteBuffer createDirectByteBuffer(final int size) {
        return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
    }

    public static PointGeometry loadMRA(File file) throws IOException {
        BufferedReader br = null;

        int linecount = lineNumber(file);
        FloatBuffer coordBuffer = createDirectFloatBuffer(linecount * 3);

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            /* reading the content */
            while (br.ready()) {
                String[] token = br.readLine().trim().split(" ");
                /* ignore first col, the x, y, z */
                coordBuffer.put(Float.parseFloat(token[1]));
                coordBuffer.put(Float.parseFloat(token[2]));
                coordBuffer.put(Float.parseFloat(token[3]));
            }
            coordBuffer.rewind();
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            if (br != null) {
                br.close();
            }
        }
        return new PointGeometry(coordBuffer);
    }

    protected static int lineNumber(File file) {
        LineNumberReader reader = null;
        try {
            reader = new LineNumberReader(new FileReader(file));
            reader.skip(Long.MAX_VALUE);
            return reader.getLineNumber() + 1;
        } catch (Exception e) {
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                }
            }
        }
        return 0;
    }
}