/*
 * Trackball code:
 *
 * Implementation of a virtual trackball.
 * Implemented by Gavin Bell, lots of ideas from Thant Tessman and
 *   the August '88 issue of Siggraph's "Computer Graphics," pp. 121-129.
 *
 * Vector manip code:
 *
 * Original code from:
 * David M. Ciemiewicz, Mark Grossman, Henry Moreton, and Paul Haeberli
 *
 * Much mucking with by:
 * Gavin Bell
 *
 * Java version: http://viewport3d.com/trackball.htm
 * Javier Perez
 *
 */
package rgbcube.j3d.navigation;

class Trackball {

    /*
     * This size should really be based on the distance from the center of
     * rotation to the point on the object underneath the mouse.  That
     * point would then track the mouse as closely as possible.  This is a
     * simple example, though, so that is left as an Exercise for the
     * Programmer.
     */
    static final double TRACKBALLSIZE = 0.8;

    static void vzero(double[] v) {
        v[0] = 0.0f;
        v[1] = 0.0f;
        v[2] = 0.0f;
    }

    static void vset(double[] v, double x, double y, double z) {
        v[0] = x;
        v[1] = y;
        v[2] = z;
    }

    static void vsub(double[] src1, double[] src2, double[] dst) {
        dst[0] = src1[0] - src2[0];
        dst[1] = src1[1] - src2[1];
        dst[2] = src1[2] - src2[2];
    }

    static void vcopy(double[] v1, double[] v2) {
        for (int i = 0; i < 3; i++) {
            v2[i] = v1[i];
        }
    }

    static void vcross(double[] v1, double[] v2, double[] cross) {
        double temp[] = new double[3];

        temp[0] = (v1[1] * v2[2]) - (v1[2] * v2[1]);
        temp[1] = (v1[2] * v2[0]) - (v1[0] * v2[2]);
        temp[2] = (v1[0] * v2[1]) - (v1[1] * v2[0]);
        vcopy(temp, cross);
    }

    static double vlength(double[] v) {
        return (double) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
    }

    static void vscale(double[] v, double div) {
        v[0] *= div;
        v[1] *= div;
        v[2] *= div;
    }

    static void vnormal(double[] v) {
        vscale(v, 1.0f / vlength(v));
    }

    static double vdot(double[] v1, double[] v2) {
        return v1[0] * v2[0] + v1[1] * v2[1] + v1[2] * v2[2];
    }

    static void vadd(double[] src1, double[] src2, double[] dst) {
        dst[0] = src1[0] + src2[0];
        dst[1] = src1[1] + src2[1];
        dst[2] = src1[2] + src2[2];
    }

    /*
     * Ok, simulate a track-ball.  Project the points onto the virtual
     * trackball, then figure out the axis of rotation, which is the cross
     * product of P1 P2 and O P1 ( O is the center of the ball, 0,0,0 )
     * Note:  This is a deformed trackball-- is a trackball in the center,
     * but is deformed into a hyperbolic sheet of rotation away from the
     * center.  This particular function was chosen after trying out
     * several variations.
     *
     * It is assumed that the arguments to this routine are in the range
     * ( -1.0 ... 1.0 )
     */
    public static void trackball(double q[], double p1x, double p1y, double p2x, double p2y, double trackballSize) {
        double a[]; /* Axis of rotation */
        double phi;  /* how much to rotate about axis */
        double p1[], p2[], d[];
        double t;

        a = new double[3];
        p1 = new double[3];
        p2 = new double[3];
        d = new double[3];

        if (p1x == p2x && p1y == p2y) {
            /* Zero rotation */
            vzero(q);
            q[3] = 1.0f;
            return;
        }

        /*
         * First, figure out z-coordinates for projection of P1 and P2 to
         * deformed sphere
         */
        vset(p1, p1x, p1y, tb_project_to_sphere(trackballSize, p1x, p1y));
        vset(p2, p2x, p2y, tb_project_to_sphere(trackballSize, p2x, p2y));

        /*
         *  Now, we want the cross product of P1 and P2
         */
        vcross(p2, p1, a);

        /*
         *  Figure out how much to rotate around that axis.
         */
        vsub(p1, p2, d);
        t = vlength(d) / (2.0f * trackballSize);

        /*
         * Avoid problems with out-of-control values...
         */
        if (t > 1.0) {
            t = 1.0f;
        }
        if (t < -1.0) {
            t = -1.0f;
        }
        phi = 2.0f * (double) Math.asin(t);

        axis_to_quat(a, phi, q);
    }

    /*
     *  Given an axis and angle, compute quaternion.
     */
    static void axis_to_quat(double a[], double phi, double q[]) {
        vnormal(a);
        vcopy(a, q);
        vscale(q, (double) Math.sin(phi / 2.0));
        q[3] = (double) Math.cos(phi / 2.0);
    }

    /*
     * Project an x,y pair onto a sphere of radius r OR a hyperbolic sheet
     * if we are away from the center of the sphere.
     */
    static double tb_project_to_sphere(double r, double x, double y) {
        double d, t, z;

        d = (double) Math.sqrt(x * x + y * y);
        if (d < r * 0.70710678118654752440) {    /* Inside sphere */
            z = (double) Math.sqrt(r * r - d * d);
        } else {           /* On hyperbola */
            t = (double) (r / 1.41421356237309504880);
            z = t * t / d;
        }
        return z;
    }

    /*
     * Given two rotations, e1 and e2, expressed as quaternion rotations,
     * figure out the equivalent single rotation and stuff it into dest.
     *
     * This routine also normalizes the result every RENORMCOUNT times it is
     * called, to keep error from creeping in.
     *
     * NOTE: This routine is written so that q1 or q2 may be the same
     * as dest ( or each other ).
     */
    static final int RENORMCOUNT = 97;

    public static void add_quats(double q1[], double q2[], double dest[]) {
        int count = 0;
        double t1[], t2[], t3[];
        double tf[];

        t1 = new double[4];
        t2 = new double[4];
        t3 = new double[4];
        tf = new double[4];

        vcopy(q1, t1);
        vscale(t1, q2[3]);

        vcopy(q2, t2);
        vscale(t2, q1[3]);

        vcross(q2, q1, t3);
        vadd(t1, t2, tf);
        vadd(t3, tf, tf);
        tf[3] = q1[3] * q2[3] - vdot(q1, q2);

        dest[0] = tf[0];
        dest[1] = tf[1];
        dest[2] = tf[2];
        dest[3] = tf[3];

        if (++count > RENORMCOUNT) {
            count = 0;
            normalize_quat(dest);
        }
    }

    /*
     * Quaternions always obey:  a^2 + b^2 + c^2 + d^2 = 1.0
     * If they don't add up to 1.0, dividing by their magnitued will
     * renormalize them.
     *
     * Note: See the following for more information on quaternions:
     *
     * - Shoemake, K., Animating rotation with quaternion curves, Computer
     *   Graphics 19, No 3 ( Proc. SIGGRAPH'85 ), 245-254, 1985.
     * - Pletinckx, D., Quaternion calculus as a basic tool in computer
     *   graphics, The Visual Computer 5, 2-13, 1989.
     */
    static void normalize_quat(double q[]) {
        int i;
        double mag;

        mag = (q[0] * q[0] + q[1] * q[1] + q[2] * q[2] + q[3] * q[3]);
        for (i = 0; i < 4; i++) {
            q[i] /= mag;
        }
    }

    /*
     * Build a rotation matrix, given a quaternion rotation.
     *
     */
    static void build_rotmatrix(double m[], double q[]) {
        m[ 0] = 1.0f - 2.0f * (q[1] * q[1] + q[2] * q[2]);
        m[ 1] = 2.0f * (q[0] * q[1] - q[2] * q[3]);
        m[ 2] = 2.0f * (q[2] * q[0] + q[1] * q[3]);
        m[ 3] = 0.0f;

        m[ 4] = 2.0f * (q[0] * q[1] + q[2] * q[3]);
        m[ 5] = 1.0f - 2.0f * (q[2] * q[2] + q[0] * q[0]);
        m[ 6] = 2.0f * (q[1] * q[2] - q[0] * q[3]);
        m[ 7] = 0.0f;

        m[ 8] = 2.0f * (q[2] * q[0] - q[1] * q[3]);
        m[ 9] = 2.0f * (q[1] * q[2] + q[0] * q[3]);
        m[10] = 1.0f - 2.0f * (q[1] * q[1] + q[0] * q[0]);
        m[11] = 0.0f;

        m[12] = 0.0f;
        m[13] = 0.0f;
        m[14] = 0.0f;
        m[15] = 1.0f;
    }
}