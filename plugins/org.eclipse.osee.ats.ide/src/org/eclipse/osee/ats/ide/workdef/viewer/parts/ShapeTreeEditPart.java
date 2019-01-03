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
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.osee.ats.ide.workdef.viewer.model.ModelElement;
import org.eclipse.osee.ats.ide.workdef.viewer.model.Shape;
import org.eclipse.swt.graphics.Image;

/**
 * TreeEditPart used for Shape instances (more specific for EllipticalShape and RectangularShape instances). This is
 * used in the Outline View of the ShapesEditor.
 * <p>
 * This edit part must implement the PropertyChangeListener interface, so it can be notified of property changes in the
 * corresponding model element.
 * </p>
 * 
 * @author Donald G. Dunne
 */
class ShapeTreeEditPart extends AbstractTreeEditPart implements PropertyChangeListener {

   /**
    * Create a new instance of this edit part using the given model element.
    * 
    * @param model a non-null Shapes instance
    */
   ShapeTreeEditPart(Shape model) {
      super(model);
   }

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

   @Override
   protected Image getImage() {
      return getCastedModel().getIcon();
   }

   @Override
   protected String getText() {
      return getCastedModel().toString();
   }

   @Override
   public void propertyChange(PropertyChangeEvent evt) {
      refreshVisuals(); // this will cause an invocation of getImage() and getText(), see below
   }
}