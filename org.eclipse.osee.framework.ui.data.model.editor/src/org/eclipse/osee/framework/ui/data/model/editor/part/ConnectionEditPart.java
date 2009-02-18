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
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.ConnectionEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.osee.framework.ui.data.model.editor.command.DeleteCommand;
import org.eclipse.osee.framework.ui.data.model.editor.model.ConnectionModel;
import org.eclipse.osee.framework.ui.data.model.editor.model.IModelListener;
import org.eclipse.osee.framework.ui.data.model.editor.policy.LinkBendpointEditPolicy;
import org.eclipse.osee.framework.ui.data.model.editor.policy.LinkEndpointEditPolicy;
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
      installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new LinkEndpointEditPolicy());
      installEditPolicy(EditPolicy.CONNECTION_ROLE, new ConnectionEditPolicy() {
         protected Command getDeleteCommand(GroupRequest request) {
            Boolean isBooleanObject = (Boolean) request.getExtendedData().get(DeleteCommand.DELETE_FROM_ODM);
            boolean isHardDelete = isBooleanObject == null ? false : isBooleanObject.booleanValue();
            DeleteCommand cmd = new DeleteCommand(isHardDelete);
            cmd.setPartToBeDeleted(getHost().getModel());
            return cmd;
         }
      });
      installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE, new LinkBendpointEditPolicy());
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
