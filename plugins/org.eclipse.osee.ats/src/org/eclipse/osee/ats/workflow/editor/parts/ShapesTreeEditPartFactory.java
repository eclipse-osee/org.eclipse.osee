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

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.osee.ats.workflow.editor.model.Shape;
import org.eclipse.osee.ats.workflow.editor.model.WorkflowDiagram;

/**
 * Factory that maps model elements to TreeEditParts. TreeEditParts are used in the outline view of the ShapesEditor.
 * 
 * @author Donald G. Dunne
 */
public class ShapesTreeEditPartFactory implements EditPartFactory {

   @Override
   public EditPart createEditPart(EditPart context, Object model) {
      if (model instanceof Shape) {
         return new ShapeTreeEditPart((Shape) model);
      }
      if (model instanceof WorkflowDiagram) {
         return new DiagramTreeEditPart((WorkflowDiagram) model);
      }
      return null; // will not show an entry for the corresponding model instance
   }

}
