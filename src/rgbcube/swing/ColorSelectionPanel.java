/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ColorSelectionPanel.java
 *
 * Created on 20.05.2011, 13:30:34
 */
package rgbcube.swing;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import som.core.LatticeRenderer;
import som.core.SOMLattice;
import som.core.SOMNode;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class ColorSelectionPanel extends javax.swing.JPanel implements PropertyChangeListener, LatticeRenderer {

    public static final String PROPERTY_I_LOVE_TRACKBALL = "use_trackball";
    public static final String PROPERTY_SHOW_BOUNDS = "show_bounds";
    public static final String PROPERTY_SHOW_BOUNDS_COLOR = "show_bounds_color";
    public static final String PROPERTY_ADD_SPHERE = "add_sphere";
    public static final String PROPERTY_REMOVE_SPHERE = "remove_sphere";
    public static final String PROPERTY_SELECTION_SPHERE = "selection_sphere";
    public static final String PROPERTY_COLOR_CHANGE = "color_changed";
    public static final String PROPERTY_SOM_SURFACE = "som_surface";
    public static final String PROPERTY_SOM_POINTS = "som_points";
    public static final String PROPERTY_SOM_LINES = "som_lines";
    public static final String PROPERTY_SOM_RENDER_WHITE = "som_render_white";
    public static final String PROPERTY_SOM_RANDOM_INIT = "som_random_init";
    private int scount = 0;

    /** Creates new form ColorSelectionPanel */
    public ColorSelectionPanel() {
        initComponents();
        initOwnComponents();
    }

    public Object getSelectedValue() {
        return jList1.getSelectedValue();
    }

    public void setSelectedColor(Color c) {
        color.setColor(c);
    }

    public void setSOMAction(Action action) {
        jButton1.setAction(action);
        /* let's fire settings */
        showPointsActionPerformed(null);
        showLinesActionPerformed(null);
        showSurfaceActionPerformed(null);
        jCheckBox1ActionPerformed(null);
        jCheckBox2ActionPerformed(null);
        jCheckBox3ActionPerformed(null);
        renderWhiteActionPerformed(null);
    }

    private void initOwnComponents() {
        jList1.setModel(new DefaultListModel());
        /* no preview please */
        color.setPreviewPanel(new JPanel());
        color.getSelectionModel().addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                ColorSelectionPanel.this.firePropertyChange(PROPERTY_COLOR_CHANGE, null,
                        color.getSelectionModel().getSelectedColor());
            }
        });
        jList1.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    ColorSelectionPanel.this.firePropertyChange(PROPERTY_SELECTION_SPHERE, null,
                            jList1.getSelectedValue());
                }
            }
        });
        add.addPropertyChangeListener(ClickLabel.PROPERTY_CLICKED, this);
        remove.addPropertyChangeListener(ClickLabel.PROPERTY_CLICKED, this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(ClickLabel.PROPERTY_CLICKED)) {
            if (evt.getSource().equals(add)) {
                DefaultListModel model = (DefaultListModel) jList1.getModel();
                String name = String.format("Sphere %s", scount++);
                model.addElement(name);
                /* */
                firePropertyChange(PROPERTY_ADD_SPHERE, null, name);
                /* */
                jList1.setSelectedValue(name, true);
            } else if (evt.getSource().equals(remove)) {
                int idx = jList1.getSelectedIndex();
                if (idx >= 0) {
                    DefaultListModel model = (DefaultListModel) jList1.getModel();
                    Object ret = model.remove(idx);                    
                    /* */
                    firePropertyChange(PROPERTY_REMOVE_SPHERE, null, ret);
                }
            }
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        add = new rgbcube.swing.ClickLabel();
        remove = new rgbcube.swing.ClickLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        showPoints = new javax.swing.JCheckBox();
        showSurface = new javax.swing.JCheckBox();
        showLines = new javax.swing.JCheckBox();
        renderWhite = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jCheckBox3 = new javax.swing.JCheckBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        color = new javax.swing.JColorChooser();

        setBorder(javax.swing.BorderFactory.createTitledBorder("RGB"));
        setLayout(new java.awt.BorderLayout());

        jScrollPane1.setViewportView(jList1);

        add.setText("<html><u>Add</u></html>");

        remove.setText("<html><u>Remove</u></html>");

        jCheckBox1.setSelected(true);
        jCheckBox1.setText("Show Bounds");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jCheckBox2.setText("Maybe i love the TrackBall!?");
        jCheckBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox2ActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("SOM"));

        jButton1.setText("Let's SOM!");

        showPoints.setSelected(true);
        showPoints.setText("Show Points");
        showPoints.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showPointsActionPerformed(evt);
            }
        });

        showSurface.setText("Show Surface");
        showSurface.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showSurfaceActionPerformed(evt);
            }
        });

        showLines.setSelected(true);
        showLines.setText("Show Lines");
        showLines.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showLinesActionPerformed(evt);
            }
        });

        renderWhite.setText("Render White");
        renderWhite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                renderWhiteActionPerformed(evt);
            }
        });

        jLabel1.setText("Iteration:");

        jLabel2.setText("0");

        jButton2.setText("Random Init");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(showPoints, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE)
                            .addComponent(showSurface))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(showLines)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jButton2)
                                .addComponent(renderWhite)))
                        .addGap(15, 15, 15))
                    .addComponent(jButton1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(showSurface)
                    .addComponent(showLines))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(showPoints)
                    .addComponent(renderWhite))
                .addGap(7, 7, 7)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap())
        );

        jCheckBox3.setSelected(true);
        jCheckBox3.setText("Show Bounds Color");
        jCheckBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(add, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(remove, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox2)
                            .addComponent(jCheckBox1)
                            .addComponent(jCheckBox3))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jCheckBox2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox3))
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(add, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(remove, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(jPanel1, java.awt.BorderLayout.WEST);

        jScrollPane2.setViewportView(color);

        add(jScrollPane2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        boolean selected = jCheckBox1.isSelected();
        firePropertyChange(PROPERTY_SHOW_BOUNDS, !selected, selected);
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jCheckBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox2ActionPerformed
        boolean selected = jCheckBox2.isSelected();
        firePropertyChange(PROPERTY_I_LOVE_TRACKBALL, !selected, selected);
    }//GEN-LAST:event_jCheckBox2ActionPerformed

    private void jCheckBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox3ActionPerformed
        boolean selected = jCheckBox3.isSelected();
        firePropertyChange(PROPERTY_SHOW_BOUNDS_COLOR, !selected, selected);
    }//GEN-LAST:event_jCheckBox3ActionPerformed

    private void showPointsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showPointsActionPerformed
        boolean selected = showPoints.isSelected();
        firePropertyChange(PROPERTY_SOM_POINTS, !selected, selected);
    }//GEN-LAST:event_showPointsActionPerformed

    private void showSurfaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showSurfaceActionPerformed
        boolean selected = showSurface.isSelected();
        firePropertyChange(PROPERTY_SOM_SURFACE, !selected, selected);
    }//GEN-LAST:event_showSurfaceActionPerformed

    private void showLinesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showLinesActionPerformed
        boolean selected = showLines.isSelected();
        firePropertyChange(PROPERTY_SOM_LINES, !selected, selected);
    }//GEN-LAST:event_showLinesActionPerformed

    private void renderWhiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_renderWhiteActionPerformed
        boolean selected = renderWhite.isSelected();
        firePropertyChange(PROPERTY_SOM_RENDER_WHITE, !selected, selected);
    }//GEN-LAST:event_renderWhiteActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        firePropertyChange(PROPERTY_SOM_RANDOM_INIT, false, true);
    }//GEN-LAST:event_jButton2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private rgbcube.swing.ClickLabel add;
    private javax.swing.JColorChooser color;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private rgbcube.swing.ClickLabel remove;
    private javax.swing.JCheckBox renderWhite;
    private javax.swing.JCheckBox showLines;
    private javax.swing.JCheckBox showPoints;
    private javax.swing.JCheckBox showSurface;
    // End of variables declaration//GEN-END:variables

    @Override
    public void registerLattice(SOMLattice lat, double[] inputVector) {        
    }

    @Override
    public void stateChanged(SOMNode node, int state) {        
    }

    @Override
    public void update(int iteration) {
        jLabel2.setText(Integer.toString(iteration));
    }
}