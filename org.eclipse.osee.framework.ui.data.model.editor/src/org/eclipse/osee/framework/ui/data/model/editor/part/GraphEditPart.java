/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.data.model.editor.part;

import java.util.List;
import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.osee.framework.ui.data.model.editor.model.ODMGraph;
import org.eclipse.osee.framework.ui.data.model.editor.policy.ODMLayoutEditPolicy;

/**
 * @author Roberto E. Escobar
 */
public class GraphEditPart extends AbstractGraphicalEditPart {

   private GraphicalViewer viewer;

   public GraphEditPart(GraphicalViewer viewer) {
      super();
      this.viewer = viewer;
   }

   /* (non-Javadoc)
    * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
    */
   protected void createEditPolicies() {
      installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
      installEditPolicy(EditPolicy.LAYOUT_ROLE, new ODMLayoutEditPolicy());
   }

   /* (non-Javadoc)
    * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
    */
   protected IFigure createFigure() {
      Figure f = new FreeformLayer();
      f.setBorder(new MarginBorder(3));
      f.setLayoutManager(new FreeformLayout());

      ConnectionLayer connLayer = (ConnectionLayer) getLayer(LayerConstants.CONNECTION_LAYER);
      connLayer.setConnectionRouter(new BendpointConnectionRouter());
      return f;
   }

   protected List getModelChildren() {
      ODMGraph graph = (ODMGraph) getModel();
      return graph.getTypes();
   }

   private void scrollTo(Rectangle fbounds) {
      scrollTo(fbounds.x + fbounds.width / 2, fbounds.y + fbounds.height / 2);
   }

   private void scrollTo(int ax, int ay) {
      Viewport viewport = ((FigureCanvas) viewer.getControl()).getViewport();
      Rectangle vbounds = viewport.getBounds();
      Point p = new Point(ax, ay);
      //		target.translateToAbsolute(p); // TODO
      int x = p.x - vbounds.width / 2;
      int y = p.y - vbounds.height / 2;
      viewport.setHorizontalLocation(x);
      viewport.setVerticalLocation(y);
   }

   private void scrollTo(IFigure target) {
      scrollTo(target.getBounds());
   }

   class ConnectionMouseListener implements MouseMotionListener, MouseListener {

      private PolylineConnection connection;

      public ConnectionMouseListener(PolylineConnection connection) {
         this.connection = connection;
      }

      public void mouseDragged(MouseEvent event) {
      }

      public void mouseEntered(MouseEvent event) {
      }

      public void mouseExited(MouseEvent event) {
         connection.setLineWidth(1);
      }

      public void mouseHover(MouseEvent event) {
         connection.setLineWidth(2);
      }

      public void mouseMoved(MouseEvent event) {
      }

      public void mouseDoubleClicked(MouseEvent event) {
      }

      public void mousePressed(MouseEvent event) {
         //         IFigure figure = (IFigure) connection.getTargetAnchor().getOwner();
         //         scrollTo(figure);
         //         Map<?, ?> map = viewer.getEditPartRegistry();
         //         EditPart editPart = (EditPart) map.get(figure.getNode());
         //         if (editPart != null) viewer.select(editPart);
      }

      public void mouseReleased(MouseEvent event) {
      }
   }
}
