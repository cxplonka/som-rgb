/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package som.color.renderer;

import java.util.HashMap;
import java.util.Map;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.GeometryUpdater;
import javax.media.j3d.IndexedLineArray;
import javax.media.j3d.IndexedTriangleArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.PointArray;
import javax.media.j3d.PointAttributes;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.Shape3D;
import rgbcube.j3d.ColorBox;
import som.core.LatticeRenderer;
import som.core.SOMLattice;
import som.core.SOMNode;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class VolumeRGBLattice3DRenderer extends BranchGroup implements LatticeRenderer, GeometryUpdater {

    private SOMLattice lattice;
    private final Shape3D lineShape = new Shape3D();
    private final Shape3D pointShape = new Shape3D();
    private final Shape3D triShape = new Shape3D();
    /* grid node points */    
    private float[] latticeNodes;
    /* grid node colors */
    private byte[] latticeNodesColors;
    /* line indizies for the grid*/
    private int[] lineIndizies;
    /* triangle indizies */
    private int[] triIndizies;
    /* */
    private boolean renderWhite = false;
    /* current somnode marker */
    private final ColorBox marker = new ColorBox(0, 0, 0, 0.015f);
    private final Map<Integer, BranchGroup> markers = new HashMap<Integer, BranchGroup>();

    public VolumeRGBLattice3DRenderer() {
        super();
        initNode();
    }

    private void initNode() {
        setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        marker.setVisible(false);
        marker.getBox().getAppearance().setPolygonAttributes(new PolygonAttributes(
                PolygonAttributes.POLYGON_LINE,
                PolygonAttributes.CULL_NONE, 0));
        marker.getBox().getAppearance().setLineAttributes(new LineAttributes(2f,
                LineAttributes.PATTERN_SOLID, false));
        /* appereance */
        triShape.setAppearance(create());
        pointShape.setAppearance(create());
        lineShape.setAppearance(create());
        /* */
        pointShape.getAppearance().setPointAttributes(new PointAttributes(8f, true));
        /* */
        lineShape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
        triShape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
        pointShape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
        /* */
        addChild(lineShape);
        addChild(pointShape);
        addChild(triShape);
        addChild(marker);
    }

    public void setRenderSOMPointsWhite(boolean value) {
        this.renderWhite = value;
        /* push it to the j3d update thread */
        ((GeometryArray) lineShape.getGeometry()).updateData(this);
    }

    public void setLinesVisible(boolean value) {
        lineShape.getAppearance().getRenderingAttributes().setVisible(value);
    }

    public void setPointsVisible(boolean value) {
        pointShape.getAppearance().getRenderingAttributes().setVisible(value);
    }

    public void setSurfaceVisible(boolean value) {
        triShape.getAppearance().getRenderingAttributes().setVisible(value);
    }

    public ColorBox getMarker() {
        return marker;
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
        lineIndizies = new int[w * h * 4];
        triIndizies = new int[w * h * 6];
        /* fill geometry */
        updateLattice();
        /* create line geometry */
        IndexedLineArray lineGeometry = new IndexedLineArray(latticeNodes.length / 3,
                LineArray.COORDINATES | LineArray.COLOR_3
                | LineArray.BY_REFERENCE | IndexedLineArray.BY_REFERENCE_INDICES
                | IndexedLineArray.USE_COORD_INDEX_ONLY,
                lineIndizies.length);
        lineGeometry.setCapability(LineArray.ALLOW_REF_DATA_WRITE);
        lineGeometry.setColorRefByte(latticeNodesColors);
        lineGeometry.setCoordIndicesRef(lineIndizies);
        lineGeometry.setCoordRefFloat(latticeNodes);
        lineShape.setGeometry(lineGeometry);
        /* create point geometry */
        PointArray pointGeometry = new PointArray(latticeNodes.length / 3, PointArray.COORDINATES
                | PointArray.COLOR_3 | PointArray.BY_REFERENCE);
        pointGeometry.setCoordRefFloat(latticeNodes);
        pointGeometry.setColorRefByte(latticeNodesColors);
        pointShape.setGeometry(pointGeometry);
        /* create triangle geometry */
        IndexedTriangleArray triGeometry = new IndexedTriangleArray(latticeNodes.length / 3,
                IndexedTriangleArray.COORDINATES | IndexedTriangleArray.COLOR_3
                | IndexedTriangleArray.BY_REFERENCE | IndexedTriangleArray.BY_REFERENCE_INDICES
                | IndexedTriangleArray.USE_COORD_INDEX_ONLY,
                triIndizies.length);
        triGeometry.setCapability(LineArray.ALLOW_REF_DATA_WRITE);
        triGeometry.setColorRefByte(latticeNodesColors);
        triGeometry.setCoordIndicesRef(triIndizies);
        triGeometry.setCoordRefFloat(latticeNodes);
        triShape.setGeometry(triGeometry);
    }

    private void updateLattice() {
        int w = lattice.w;
        int h = lattice.h;
        int lidx = 0, tidx = 0;

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int idx1 = (y * w + x) * 3;
                int idx2 = (y * w + x + 1) * 3;
                int idx3 = ((y + 1) * w + x) * 3;
                int idx4 = ((y + 1) * w + x + 1) * 3;
                /* rgb weights x = b, y = g, z = r*/
                SOMNode node = lattice.getNode(x, y);
                latticeNodes[idx1] = (float)node.getWeight(2);
                latticeNodes[idx1 + 1] = (float)node.getWeight(1);
                latticeNodes[idx1 + 2] = (float)node.getWeight(0);
                /* node colors */
                if (renderWhite) {
                    latticeNodesColors[idx1] = (byte) 255;
                    latticeNodesColors[idx1 + 1] = (byte) 255;
                    latticeNodesColors[idx1 + 2] = (byte) 255;
                } else {
                    latticeNodesColors[idx1] = (byte) (node.getWeight(0) * 255);
                    latticeNodesColors[idx1 + 1] = (byte) (node.getWeight(1) * 255);
                    latticeNodesColors[idx1 + 2] = (byte) (node.getWeight(2) * 255);
                }
                /* horizontal line */
                if (x + 1 < w) {
                    lineIndizies[lidx++] = idx1 / 3;
                    lineIndizies[lidx++] = idx2 / 3;
                }
                /* vertical line */
                if (y + 1 < h) {
                    lineIndizies[lidx++] = idx1 / 3;
                    lineIndizies[lidx++] = idx3 / 3;
                }
                /* triangulate the quad */
                if (x + 1 < w && y + 1 < h) {
                    /* lower triangle */
                    triIndizies[tidx++] = idx1 / 3;
                    triIndizies[tidx++] = idx3 / 3;
                    triIndizies[tidx++] = idx4 / 3;
                    /* upper triangle */
                    triIndizies[tidx++] = idx1 / 3;
                    triIndizies[tidx++] = idx2 / 3;
                    triIndizies[tidx++] = idx4 / 3;
                }
            }
        }
    }

    public static void createNormal(float[] points, int idx, float[] normal) {
        float v1x = points[idx] - points[idx + 3];
        float v1y = points[idx + 1] - points[idx + 4];
        float v1z = points[idx + 2] - points[idx + 5];

        float v2x = points[idx] - points[idx + 6];
        float v2y = points[idx + 1] - points[idx + 7];
        float v2z = points[idx + 2] - points[idx + 8];

        float x = v1y * v2z - v1z * v2y;
        float y = v2x * v1z - v2z * v1x;
        float cvx = v1x * v2y - v1y * v2x;
        float cvy = x;
        float cvz = y;

        double norm = 1.0 / Math.sqrt(cvx * cvx + cvy * cvy + cvz * cvz);
        normal[0] = (float) (cvx * norm);
        normal[1] = (float) (cvy * norm);
        normal[2] = (float) (cvz * norm);
    }

    @Override
    public void update(int iteration) {
        /* push it to the j3d update thread */
        ((GeometryArray) lineShape.getGeometry()).updateData(this);
    }

    @Override
    public void updateData(Geometry gmtr) {
        updateLattice();
    }

    @Override
    public void stateChanged(SOMNode node, int state) {
        int idx = node.y * lattice.w + node.x;
        if (state == LatticeRenderer.STATE_NODE_SELECTED) {
            BranchGroup markerGr = createMarkerGroup(
                    (float) node.getWeight(0),
                    (float) node.getWeight(1),
                    (float) node.getWeight(2));
            addChild(markerGr);
            markers.put(idx, markerGr);
        } else if (state == LatticeRenderer.STATE_NODE_DESELECTED) {
            BranchGroup markerGr = markers.remove(idx);
            if (markerGr != null) {
                markerGr.detach();
            }
        }
    }

    private BranchGroup createMarkerGroup(float r, float g, float b) {
        BranchGroup ret = new BranchGroup();
        ret.setCapability(BranchGroup.ALLOW_DETACH);
        ret.addChild(new ColorBox(r, g, b, 0.015f));
        return ret;
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