/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.workflow.editor.parts;

import java.util.logging.Level;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.workflow.editor.model.CancelledWorkPageShape;
import org.eclipse.osee.ats.workflow.editor.model.CompletedWorkPageShape;
import org.eclipse.osee.ats.workflow.editor.model.Relation;
import org.eclipse.osee.ats.workflow.editor.model.Shape;
import org.eclipse.osee.ats.workflow.editor.model.WorkPageShape;
import org.eclipse.osee.ats.workflow.editor.model.WorkflowDiagram;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * Factory that maps model elements to edit parts.
 * 
 * @author Donald G. Dunne
 */
public class ShapesEditPartFactory implements EditPartFactory {

   @Override
   public EditPart createEditPart(EditPart context, Object modelElement) {
      // get EditPart for model element
      EditPart part = null;
      try {
         part = getPartForElement(modelElement);
         // store model element in EditPart
         part.setModel(modelElement);
      } catch (OseeStateException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return part;
   }

   /**
    * Maps an object to an EditPart.
    * 
    * @throws RuntimeException if no match was found (programming error)
    */
   private EditPart getPartForElement(Object modelElement) throws OseeStateException {
      if (modelElement instanceof WorkflowDiagram) {
         return new DiagramEditPart();
      }
      if (modelElement instanceof CompletedWorkPageShape) {
         return new WorkPageEditPart((CompletedWorkPageShape) modelElement);
      }
      if (modelElement instanceof CancelledWorkPageShape) {
         return new WorkPageEditPart((CancelledWorkPageShape) modelElement);
      }
      if (modelElement instanceof WorkPageShape) {
         return new WorkPageEditPart((WorkPageShape) modelElement);
      }
      if (modelElement instanceof Shape) {
         return new ShapeEditPart();
      }
      if (modelElement instanceof Relation) {
         return new ConnectionEditPart();
      }
      throw new OseeStateException(
         "Can't create part for model element: " + (modelElement != null ? modelElement.getClass().getName() : "null"));
   }

}