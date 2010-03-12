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

import org.eclipse.draw2d.ConnectionEndpointLocator;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.PolylineDecoration;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.osee.framework.ui.data.model.editor.model.RelationLinkModel;

/**
 * @author Roberto E. Escobar
 */
public class RelationLinkEditPart extends ConnectionEditPart {

   private Label srcCount, srcName, targetCount, targetName;
   private ConnectionEndpointLocator srcCountLocator, srcNameLocator, targetCountLocator, targetNameLocator;
   private RotatableDecoration srcDecor, targetDecor;

   //   private EReference opposite;

   public RelationLinkEditPart(Object model) {
      super((RelationLinkModel) model);
   }

   public void activate() {
      super.activate();
      getRelationLink().addListener(modelListener);
      //      updateEOpposite(getEReference().getEOpposite());
   }

   //   private String createCountString(EReference ref) {
   //      int lower = ref.getLowerBound();
   //      int upper = ref.getUpperBound();
   //      if (lower == upper) return "" + lower; //$NON-NLS-1$
   //      return lower + ".." + (upper == -1 ? "n" : "" + upper); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
   //   }

   protected IFigure createFigure() {
      PolylineConnection conn = new PolylineConnection();

      // Create the locators
      srcCountLocator = new ConnectionEndpointLocator(conn, false);
      srcNameLocator = new ConnectionEndpointLocator(conn, false);
      srcNameLocator.setVDistance(-4);
      targetCountLocator = new ConnectionEndpointLocator(conn, true);
      targetNameLocator = new ConnectionEndpointLocator(conn, true);
      targetCountLocator.setVDistance(-4);
      return conn;
   }

   private PolygonDecoration createPolygonDecoration() {
      PolygonDecoration decoration = new PolygonDecoration();
      PointList decorationPointList = new PointList();
      decorationPointList.addPoint(0, 0);
      decorationPointList.addPoint(-1, 1);
      decorationPointList.addPoint(-2, 0);
      decorationPointList.addPoint(-1, -1);
      decoration.setTemplate(decorationPointList);
      return decoration;
   }

   private PolylineDecoration createPolylineDecoration() {
      PolylineDecoration decoration = new PolylineDecoration();
      decoration.setScale(10, 5);
      return decoration;
   }

   /**
    * Upon deactivation, detach from the model element as a property change listener.
    */
   public void deactivate() {
      //      updateEOpposite(null);
      getRelationLink().removeListener(modelListener);
      super.deactivate();
   }

   private PolylineConnection getConnection() {
      return (PolylineConnection) getFigure();
   }

   //   protected EReference getEReference() {
   //      return getRefView().getEReference();
   //   }

   protected RelationLinkModel getRelationLink() {
      return (RelationLinkModel) getModel();
   }

   protected void handleModelEvent(Object object) {
      //      updateEOpposite(getEReference().getEOpposite());
      refreshVisuals();
      super.handleModelEvent(object);
   }

   protected void refreshVisuals() {
      super.refreshVisuals();
      updateSourceDecoration();
      updateTargetDecoration();
      updateSourceCount();
      updateSourceName();
      updateTargetCount();
      updateTargetName();
   }

   //   private void updateEOpposite(EReference opp) {
   //      if (opposite == opp) return;
   //      if (opposite != null) opposite.eAdapters().remove(modelListener);
   //      opposite = opp;
   //      if (opposite != null) opposite.eAdapters().add(modelListener);
   //   }

   private void updateSourceCount() {
      //      if (getRelationDataType().isOppositeShown() && getRelationDataType().getOpposite() != null) {
      //         if (getEReference().isContainment()) {
      //            if (srcCount != null) {
      //               getConnection().remove(srcCount);
      //               srcCount = null;
      //            }
      //         } else {
      //            if (srcCount == null) {
      //               srcCount = new Label();
      //               srcCount.setOpaque(true);
      //               getConnection().add(srcCount, srcCountLocator);
      //            }
      //            srcCount.setText(createCountString(getEReference().getEOpposite()));
      //         }
      //      } else if (srcCount != null) {
      //         getConnection().remove(srcCount);
      //         srcCount = null;
      //      }
   }

   private void updateSourceDecoration() {
      //      if (getEReference().isContainment()) {
      if (srcDecor == null) {
         srcDecor = createPolygonDecoration();
         getConnection().setSourceDecoration(srcDecor);
      }
      //      } else 
      //      if (srcDecor != null) {
      //         srcDecor = null;
      getConnection().setSourceDecoration(srcDecor);
      //      }
   }

   private void updateTargetCount() {
      //      if (getEReference().isContainer() && getRefView().isOppositeShown() && getEReference().getEOpposite() != null) {
      //         if (targetCount != null) {
      //            getConnection().remove(targetCount);
      //            targetCount = null;
      //         }
      //      } else {
      //         if (targetCount == null) {
      //            targetCount = new Label();
      //            targetCount.setOpaque(true);
      //            getConnection().add(targetCount, targetCountLocator);
      //         }
      //         targetCount.setText(createCountString(getEReference()));
      //      }
   }

   private void updateTargetDecoration() {
      //      if (getRelationDataType().isOppositeShown() && getRelationDataType().getEOpposite() != null) {
      //         if (getEReference().isContainer()) {
      //            if (targetDecor != null && !(targetDecor instanceof PolygonDecoration)) {
      //               getConnection().setTargetDecoration(null);
      //               targetDecor = null;
      //            }
      //            if (targetDecor == null) {
      //               targetDecor = createPolygonDecoration();
      //               getConnection().setTargetDecoration(targetDecor);
      //            }
      //         } else if (targetDecor != null) {
      //            targetDecor = null;
      //            getConnection().setTargetDecoration(targetDecor);
      //         }
      //      } else if (targetDecor == null) {
      //         targetDecor = createPolylineDecoration();
      //         getConnection().setTargetDecoration(targetDecor);
      //      }
   }

   private void updateTargetName() {
      if (targetName == null) {
         targetName = new Label();
         targetName.setOpaque(true);
         getConnection().add(targetName, targetNameLocator);
      }
      targetName.setText("+" + getRelationLink().getRelation().getSideAName());
   }

   private void updateSourceName() {
      //      if (getRefView().isOppositeShown() && getEReference().getEOpposite() != null) {
      if (srcName == null) {
         srcName = new Label();
         srcName.setOpaque(true);
         getConnection().add(srcName, srcNameLocator);
      }
      srcName.setText("+" + getRelationLink().getRelation().getSideBName());
      //      } else if (srcName != null) {
      //         getConnection().remove(srcName);
      //         srcName = null;
      //      }
   }
}
