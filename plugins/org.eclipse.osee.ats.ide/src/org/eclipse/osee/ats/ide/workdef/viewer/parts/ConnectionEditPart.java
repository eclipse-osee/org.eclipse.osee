/*******************************************************************************
 * Copyright (c) 2004, 2005 Donald G. Dunne and others.
�* All rights reserved. This program and the accompanying materials
�* are made available under the terms of the Eclipse Public License v1.0
�* which accompanies this distribution, and is available at
�* http://www.eclipse.org/legal/epl-v10.html
�*
�* Contributors:
�*����Donald G. Dunne - initial API and implementation
�*******************************************************************************/
package org.eclipse.osee.ats.ide.workdef.viewer.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.ConnectionEditPolicy;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.osee.ats.ide.workdef.viewer.model.Connection;
import org.eclipse.osee.ats.ide.workdef.viewer.model.ModelElement;
import org.eclipse.osee.ats.ide.workdef.viewer.model.TransitionConnection;
import org.eclipse.osee.ats.ide.workdef.viewer.model.commands.ConnectionDeleteCommand;

/**
 * Edit part for Connection model elements.
 * <p>
 * This edit part must implement the PropertyChangeListener interface, so it can be notified of property changes in the
 * corresponding model element.
 * </p>
 * 
 * @author Donald G. Dunne
 */
class ConnectionEditPart extends AbstractConnectionEditPart implements PropertyChangeListener {

   /**
    * Upon activation, attach to the model element as a property change listener.
    */
   @Override
   public void activate() {
      if (!isActive()) {
         super.activate();
         ((ModelElement) getModel()).addPropertyChangeListener(this);
      }
   }

   @Override
   protected void createEditPolicies() {
      // Selection handle edit policy.
      // Makes the connection show a feedback, when selected by the user.
      installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new ConnectionEndpointEditPolicy());
      // Allows the removal of the connection model element
      installEditPolicy(EditPolicy.CONNECTION_ROLE, new ConnectionEditPolicy() {
         @Override
         protected Command getDeleteCommand(GroupRequest request) {
            return new ConnectionDeleteCommand(getCastedModel());
         }
      });
   }

   @Override
   protected IFigure createFigure() {
      PolylineConnection connection = (PolylineConnection) super.createFigure();
      connection.setTargetDecoration(new PolygonDecoration()); // arrow at target endpoint
      connection.setLineStyle(getCastedModel().getLineStyle()); // line drawing style
      connection.setForegroundColor(getCastedModel().getForegroundColor());
      connection.setLineWidth(getCastedModel().getLineWidth());
      if (getCastedModel().getLabel() != null) {
         connection.setToolTip(new Label(getCastedModel().getLabel()));
      }
      return connection;
   }

   /**
    * Upon deactivation, detach from the model element as a property change listener.
    */
   @Override
   public void deactivate() {
      if (isActive()) {
         super.deactivate();
         ((ModelElement) getModel()).removePropertyChangeListener(this);
      }
   }

   private Connection getCastedModel() {
      return (Connection) getModel();
   }

   @Override
   public void propertyChange(PropertyChangeEvent event) {
      String property = event.getPropertyName();
      if (TransitionConnection.TYPE_PROP.equals(property)) {
         ((PolylineConnection) getFigure()).setLineStyle(getCastedModel().getLineStyle());
      }
   }

}