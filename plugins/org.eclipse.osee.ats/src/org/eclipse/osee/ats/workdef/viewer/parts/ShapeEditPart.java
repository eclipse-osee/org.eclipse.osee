/*******************************************************************************
 * Copyright (c) 2004, 2005 Donald G. Dunne and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *����Donald G. Dunne - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.workdef.viewer.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.workdef.viewer.model.Connection;
import org.eclipse.osee.ats.workdef.viewer.model.ModelElement;
import org.eclipse.osee.ats.workdef.viewer.model.RectangleShape;
import org.eclipse.osee.ats.workdef.viewer.model.Shape;
import org.eclipse.osee.ats.workdef.viewer.model.commands.ConnectionCreateCommand;
import org.eclipse.osee.ats.workdef.viewer.model.commands.ConnectionReconnectCommand;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * EditPart used for Shape instances (more specific for EllipticalShape and RectangularShape instances).
 * <p>
 * This edit part must implement the PropertyChangeListener interface, so it can be notified of property changes in the
 * corresponding model element.
 * </p>
 * 
 * @author Donald G. Dunne
 */
class ShapeEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener, NodeEditPart {

   private ConnectionAnchor anchor;

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
      // allow removal of the associated model element
      installEditPolicy(EditPolicy.COMPONENT_ROLE, new ShapeComponentEditPolicy());
      // allow the creation of connections and
      // and the reconnection of connections between Shape instances
      installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new GraphicalNodeEditPolicy() {
         @Override
         protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
            ConnectionCreateCommand cmd = (ConnectionCreateCommand) request.getStartCommand();
            cmd.setTarget((Shape) getHost().getModel());
            return cmd;
         }

         @Override
         protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
            Shape source = (Shape) getHost().getModel();
            Class<?> clazz = (Class<?>) request.getNewObjectType();
            ConnectionCreateCommand cmd = new ConnectionCreateCommand(source, clazz);
            request.setStartCommand(cmd);
            return cmd;
         }

         @Override
         protected Command getReconnectSourceCommand(ReconnectRequest request) {
            Connection conn = (Connection) request.getConnectionEditPart().getModel();
            Shape newSource = (Shape) getHost().getModel();
            ConnectionReconnectCommand cmd = new ConnectionReconnectCommand(conn);
            cmd.setNewSource(newSource);
            return cmd;
         }

         @Override
         protected Command getReconnectTargetCommand(ReconnectRequest request) {
            Connection conn = (Connection) request.getConnectionEditPart().getModel();
            Shape newTarget = (Shape) getHost().getModel();
            ConnectionReconnectCommand cmd = new ConnectionReconnectCommand(conn);
            cmd.setNewTarget(newTarget);
            return cmd;
         }
      });
   }

   @Override
   protected IFigure createFigure() {
      IFigure f;
      try {
         f = createFigureForModel();
         f.setOpaque(true); // non-transparent figure
         f.setBackgroundColor(ColorConstants.green);
         return f;
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }

   /**
    * Return a IFigure depending on the instance of the current model element. This allows this EditPart to be used for
    * both subclasses of Shape.
    */
   protected IFigure createFigureForModel()  {
      if (getModel() instanceof RectangleShape) {
         return new RoundedRectangle();
      }
      // if Shapes gets extended the conditions above must be updated
      throw new OseeArgumentException("Unsupported Shape");
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

   private Shape getCastedModel() {
      return (Shape) getModel();
   }

   protected ConnectionAnchor getConnectionAnchor()  {
      if (anchor == null) {
         if (getModel() instanceof RectangleShape) {
            anchor = new ChopboxAnchor(getFigure());
         } else {
            // if Shapes gets extended the conditions above must be updated
            throw new OseeArgumentException("unexpected model");
         }
      }
      return anchor;
   }

   @Override
   protected List<?> getModelSourceConnections() {
      return getCastedModel().getSourceConnections();
   }

   @Override
   protected List<?> getModelTargetConnections() {
      return getCastedModel().getTargetConnections();
   }

   @Override
   public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
      try {
         return getConnectionAnchor();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }

   @Override
   public ConnectionAnchor getSourceConnectionAnchor(Request request) {
      try {
         return getConnectionAnchor();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }

   @Override
   public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
      try {
         return getConnectionAnchor();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }

   @Override
   public ConnectionAnchor getTargetConnectionAnchor(Request request) {
      try {
         return getConnectionAnchor();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }

   @Override
   public void propertyChange(PropertyChangeEvent evt) {
      String prop = evt.getPropertyName();
      if (Shape.SIZE_PROP.equals(prop) || Shape.LOCATION_PROP.equals(prop)) {
         refreshVisuals();
      } else if (Shape.SOURCE_CONNECTIONS_PROP.equals(prop)) {
         refreshSourceConnections();
      } else if (Shape.TARGET_CONNECTIONS_PROP.equals(prop)) {
         refreshTargetConnections();
      }
   }

   @Override
   protected void refreshVisuals() {
      // notify parent container of changed position & location
      // if this line is removed, the XYLayoutManager used by the parent container
      // (the Figure of the ShapesDiagramEditPart), will not know the bounds of this figure
      // and will not draw it correctly.
      Rectangle bounds = new Rectangle(getCastedModel().getLocation(), getCastedModel().getSize());
      ((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), bounds);
   }
}