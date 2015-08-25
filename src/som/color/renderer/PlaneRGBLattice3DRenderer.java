/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package som.color.renderer;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.GeometryUpdater;
import javax.media.j3d.IndexedTriangleArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.Link;
import javax.media.j3d.Node;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.SharedGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;
import som.core.LatticeRenderer;
import som.core.SOMLattice;
import som.core.SOMNode;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class PlaneRGBLattice3DRenderer extends BranchGroup implements LatticeRenderer, GeometryUpdater {

    private final Shape3D gridShape = new Shape3D();
    private final SharedGroup sharedGroup = new SharedGroup();
    private SOMLattice lattice;
    /* grid node points */
    private float[] latticeNodes;
    /* grid node colors */
    private byte[] latticeNodesColors;
    /* triangle indizies */
    private int[] triIndizies;

    public PlaneRGBLattice3DRenderer() {
        super();
        initNode();
    }

    private void initNode() {
        gridShape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
        gridShape.setAppearance(create());
        sharedGroup.addChild(gridShape);
        sharedGroup.compile();
        addChild(offSet(0, 0, 0));
    }

    private Node offSet(double x, double y, double z) {
        Transform3D t = new Transform3D();
        t.setTranslation(new Vector3d(x, y, z));
        TransformGroup tgr = new TransformGroup(t);
        tgr.addChild(new Link(sharedGroup));
        return tgr;
    }

    @Override
    public void registerLattice(SOMLattice lat, double[] inputVector) {
        this.lattice = lat;
        /* som map dimension */
        int w = lat.w;
        int h = lat.h;
        /* */
        latticeNodes = new float[w * h * 3];
        latticeNodesColors = new byte[w * h * 3];
        triIndizies = new int[w * h * 6];
        updateLattice();
        /* */
        IndexedTriangleArray triGeometry = new IndexedTriangleArray(latticeNodes.length / 3,
                IndexedTriangleArray.COORDINATES | IndexedTriangleArray.COLOR_3
                | IndexedTriangleArray.BY_REFERENCE | IndexedTriangleArray.BY_REFERENCE_INDICES
                | IndexedTriangleArray.USE_COORD_INDEX_ONLY,
                triIndizies.length);
        triGeometry.setCapability(LineArray.ALLOW_REF_DATA_WRITE);
        triGeometry.setColorRefByte(latticeNodesColors);
        triGeometry.setCoordIndicesRef(triIndizies);
        triGeometry.setCoordRefFloat(latticeNodes);
        gridShape.setGeometry(triGeometry);
    }

    private void updateLattice() {
        int w = lattice.w;
        int h = lattice.h;
        int tidx = 0;
        float dx = 1f / w;
        float dy = 1f / h;

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int idx1 = (y * w + x) * 3;
                int idx2 = (y * w + x + 1) * 3;
                int idx3 = ((y + 1) * w + x) * 3;
                int idx4 = ((y + 1) * w + x + 1) * 3;
                /* rgb weights x = b, y = g, z = r*/
                SOMNode node = lattice.getNode(x, y);
                latticeNodes[idx1] = x * dx;
                latticeNodes[idx1 + 1] = 0;
                latticeNodes[idx1 + 2] = y * dy;
                /* node colors */
                latticeNodesColors[idx1] = (byte) (node.getWeight(0) * 255);
                latticeNodesColors[idx1 + 1] = (byte) (node.getWeight(1) * 255);
                latticeNodesColors[idx1 + 2] = (byte) (node.getWeight(2) * 255);
                /* triangulate the quad */
                if (x + 1 < w && y + 1 < h) {
                    /* lower triangle */
                    triIndizies[tidx++] = idx1 / 3;
                    triIndizies[tidx++] = idx4 / 3;
                    triIndizies[tidx++] = idx3 / 3;
                    /* upper triangle */
                    triIndizies[tidx++] = idx1 / 3;
                    triIndizies[tidx++] = idx2 / 3;
                    triIndizies[tidx++] = idx4 / 3;
                }
            }
        }
    }

    @Override
    public void stateChanged(SOMNode node, int state) {
    }

    @Override
    public void update(int iteration) {
        /* push it to the j3d update thread */
        ((GeometryArray) gridShape.getGeometry()).updateData(this);
    }

    @Override
    public void updateData(Geometry gmtr) {
        updateLattice();
    }

    private Appearance create() {
        Appearance ret = new Appearance();
        RenderingAttributes ratt = new RenderingAttributes();
        ratt.setCapability(RenderingAttributes.ALLOW_VISIBLE_WRITE);
        ret.setRenderingAttributes(ratt);

        ret.setPolygonAttributes(new PolygonAttributes(
                PolygonAttributes.POLYGON_FILL,
                PolygonAttributes.CULL_NONE, 0));
        return ret;
    }
}