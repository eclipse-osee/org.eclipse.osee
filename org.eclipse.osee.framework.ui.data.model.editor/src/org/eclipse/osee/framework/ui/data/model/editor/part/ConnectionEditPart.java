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

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.BendpointEditPolicy;
import org.eclipse.gef.editpolicies.ConnectionEditPolicy;
import org.eclipse.gef.requests.BendpointRequest;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.osee.framework.ui.data.model.editor.command.CreateBendpointCommand;
import org.eclipse.osee.framework.ui.data.model.editor.command.DeleteBendpointCommand;
import org.eclipse.osee.framework.ui.data.model.editor.command.DeleteCommand;
import org.eclipse.osee.framework.ui.data.model.editor.command.MoveBendpointCommand;
import org.eclipse.osee.framework.ui.data.model.editor.model.ConnectionModel;
import org.eclipse.osee.framework.ui.data.model.editor.model.IModelListener;
import org.eclipse.osee.framework.ui.data.model.editor.policy.ConnectionModelEndpointEditPolicy;
import org.eclipse.osee.framework.ui.data.model.editor.property.PropertySourceFactory;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * @author Roberto E. Escobar
 */
public class ConnectionEditPart extends AbstractConnectionEditPart {

   protected IModelListener modelListener = new IModelListener() {

      @Override
      public void onModelEvent(Object object) {
         handleModelEvent(object);
      }
   };

   public ConnectionEditPart(Object connectionModel) {
      super();
      setModel((ConnectionModel) connectionModel);
   }

   public void activate() {
      super.activate();
      getConnectionModel().addListener(modelListener);
   }

   protected void createEditPolicies() {
      installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new ConnectionModelEndpointEditPolicy());

      installEditPolicy(EditPolicy.CONNECTION_ROLE, new ConnectionEditPolicy() {
         protected Command getDeleteCommand(GroupRequest request) {
            Command toReturn = UnexecutableCommand.INSTANCE;
            Object model = ((AbstractGraphicalEditPart) getParent()).getModel();
            if (model != null) {
               Boolean isBooleanObject = (Boolean) request.getExtendedData().get(DeleteCommand.DELETE_FROM_ODM_DIAGRAM);
               boolean isDeleteFromDiagram = isBooleanObject == null ? false : isBooleanObject.booleanValue();

               DeleteCommand deleteCmd = new DeleteCommand();
               deleteCmd.setPartToBeDeleted(getHost().getModel(), model, isDeleteFromDiagram);
               toReturn = deleteCmd;
            }
            return toReturn;
         }
      });

      installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE, new BendpointEditPolicy() {

         protected Command getCreateBendpointCommand(BendpointRequest request) {
            Point location = request.getLocation();
            getConnection().translateToRelative(location);
            return new CreateBendpointCommand((ConnectionModel) request.getSource().getModel(), location,
                  request.getIndex());
         }

         protected Command getDeleteBendpointCommand(BendpointRequest request) {
            return new DeleteBendpointCommand((ConnectionModel) getHost().getModel(), request.getIndex());
         }

         protected Command getMoveBendpointCommand(BendpointRequest request) {
            Point location = request.getLocation();
            getConnection().translateToRelative(location);
            return new MoveBendpointCommand((ConnectionModel) request.getSource().getModel(), location,
                  request.getIndex());
         }
      });

   }

   protected IFigure createFigure() {
      PolylineConnection conn = new PolylineConnection();
      conn.setLineStyle(Graphics.LINE_DASHDOT);
      return conn;
   }

   public void deactivate() {
      getConnectionModel().removeListener(modelListener);
      super.deactivate();
   }

   @SuppressWarnings("unchecked")
   public Object getAdapter(Class adapter) {
      if (IPropertySource.class == adapter) {
         return PropertySourceFactory.getPropertySource(getModel());
      }
      return super.getAdapter(adapter);
   }

   protected ConnectionModel getConnectionModel() {
      return (ConnectionModel) getModel();
   }

   protected void handleModelEvent(Object object) {
      refreshVisuals();
      refreshSourceAnchor();
      refreshTargetAnchor();
   }

   protected void refreshVisuals() {
      getConnectionFigure().setRoutingConstraint(getConnectionModel().getBendpoints());
   }

}
