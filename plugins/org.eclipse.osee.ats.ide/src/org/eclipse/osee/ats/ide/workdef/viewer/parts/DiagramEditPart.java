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
import java.util.List;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ShortestPathConnectionRouter;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.osee.ats.ide.workdef.viewer.model.ModelElement;
import org.eclipse.osee.ats.ide.workdef.viewer.model.Shape;
import org.eclipse.osee.ats.ide.workdef.viewer.model.StateDefShape;
import org.eclipse.osee.ats.ide.workdef.viewer.model.WorkDefinitionDiagram;
import org.eclipse.osee.ats.ide.workdef.viewer.model.commands.ShapeCreateCommand;
import org.eclipse.osee.ats.ide.workdef.viewer.model.commands.ShapeSetConstraintCommand;

/**
 * EditPart for the a WorkflowDiagram instance.
 * <p>
 * This edit part server as the main diagram container, the white area where everything else is in. Also responsible for
 * the container's layout (the way the container rearanges is contents) and the container's capabilities (edit
 * policies).
 * </p>
 * <p>
 * This edit part must implement the PropertyChangeListener interface, so it can be notified of property changes in the
 * corresponding model element.
 * </p>
 * 
 * @author Donald G. Dunne
 */
public class DiagramEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener {

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
      // disallows the removal of this edit part from its parent
      installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
      // handles constraint changes (e.g. moving and/or resizing) of model elements
      // and creation of new model elements
      installEditPolicy(EditPolicy.LAYOUT_ROLE, new ShapesXYLayoutEditPolicy());
   }

   @Override
   protected IFigure createFigure() {
      Figure f = new FreeformLayer();
      f.setBorder(new MarginBorder(3));
      f.setLayoutManager(new FreeformLayout());

      // Create the static router for the connection layer
      ConnectionLayer connLayer = (ConnectionLayer) getLayer(LayerConstants.CONNECTION_LAYER);
      connLayer.setConnectionRouter(new ShortestPathConnectionRouter(f));

      return f;
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

   private WorkDefinitionDiagram getCastedModel() {
      return (WorkDefinitionDiagram) getModel();
   }

   @Override
   protected List<?> getModelChildren() {
      return getCastedModel().getChildren(); // return a list of shapes
   }

   @Override
   public void propertyChange(PropertyChangeEvent evt) {
      String prop = evt.getPropertyName();
      // these properties are fired when Shapes are added into or removed from
      // the ShapeDiagram instance and must cause a call of refreshChildren()
      // to update the diagram's contents.
      if (WorkDefinitionDiagram.CHILD_ADDED_PROP.equals(prop) || WorkDefinitionDiagram.CHILD_REMOVED_PROP.equals(
         prop)) {
         refreshChildren();
      }
   }

   /**
    * EditPolicy for the Figure used by this edit part. Children of XYLayoutEditPolicy can be used in Figures with
    * XYLayout.
    * 
    * @author Donald G. Dunne
    */
   private static class ShapesXYLayoutEditPolicy extends XYLayoutEditPolicy {

      @Override
      protected Command createChangeConstraintCommand(ChangeBoundsRequest request, EditPart child, Object constraint) {
         if (child instanceof ShapeEditPart && constraint instanceof Rectangle) {
            // return a command that can move and/or resize a Shape
            return new ShapeSetConstraintCommand((Shape) child.getModel(), request, (Rectangle) constraint);
         }
         return super.createChangeConstraintCommand(request, child, constraint);
      }

      @Override
      protected Command createChangeConstraintCommand(EditPart child, Object constraint) {
         // not used in this example
         return null;
      }

      @Override
      protected Command getCreateCommand(CreateRequest request) {
         Object childClass = request.getNewObjectType();
         if (StateDefShape.class.isAssignableFrom((Class<?>) childClass)) {
            // return a command that can add a Shape to a WorkflowDiagram
            return new ShapeCreateCommand((Shape) request.getNewObject(), (WorkDefinitionDiagram) getHost().getModel(),
               (Rectangle) getConstraintFor(request));
         }
         return null;
      }
   }

}