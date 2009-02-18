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
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.LayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.tools.DragEditPartsTracker;
import org.eclipse.osee.framework.ui.data.model.editor.model.ConnectionModel;
import org.eclipse.osee.framework.ui.data.model.editor.model.NodeModel;

/**
 * @author Roberto E. Escobar
 */
public abstract class NodeModelEditPart extends BaseEditPart implements NodeEditPart {

   private ChopboxAnchor anchor;

   public NodeModelEditPart(NodeModel model) {
      super(model);
   }

   public boolean canDeleteFromDiagram() {
      return true;
   }

   protected void createEditPolicies() {
      super.createEditPolicies();
      installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, null);
      installEditPolicy(EditPolicy.LAYOUT_ROLE, new LayoutEditPolicy() {
         protected EditPolicy createChildEditPolicy(EditPart child) {
            return null;
         }

         protected Command getCreateCommand(CreateRequest request) {
            return null;
         }

         protected Command getDeleteDependantCommand(Request request) {
            return null;
         }

         protected Command getMoveChildrenCommand(Request request) {
            return null;
         }
      });
   }

   public DragTracker getDragTracker(Request request) {
      return new DragEditPartsTracker(this);
   }

   protected NodeModel getNodeModel() {
      return (NodeModel) getModel();
   }

   protected List<ConnectionModel> getModelSourceConnections() {
      return getNodeModel().getOutgoingConnections();
   }

   protected List<ConnectionModel> getModelTargetConnections() {
      return getNodeModel().getIncomingConnections();
   }

   public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
      if (anchor == null) anchor = new ChopboxAnchor(getFigure());
      return anchor;
   }

   public ConnectionAnchor getSourceConnectionAnchor(Request request) {
      if (anchor == null) anchor = new ChopboxAnchor(getFigure());
      return anchor;
   }

   public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
      if (anchor == null) anchor = new ChopboxAnchor(getFigure());
      return anchor;
   }

   public ConnectionAnchor getTargetConnectionAnchor(Request request) {
      if (anchor == null) anchor = new ChopboxAnchor(getFigure());
      return anchor;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.data.model.editor.part.BaseEditPart#handleModelChanged(java.lang.Object)
    */
   @Override
   protected void handleModelEvent(Object msg) {
      refreshVisuals();
   }

   protected void refreshVisuals() {
      super.refreshVisuals();
      Rectangle constraint = new Rectangle(0, 0, -1, -1);
      constraint.setLocation(getNodeModel().getLocation());
      constraint.width = getNodeModel().getWidth();
      ((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), constraint);
   }
}
