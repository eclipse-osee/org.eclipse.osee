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

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.osee.ats.ide.workdef.viewer.model.Shape;
import org.eclipse.osee.ats.ide.workdef.viewer.model.WorkDefinitionDiagram;
import org.eclipse.osee.ats.ide.workdef.viewer.model.commands.ShapeDeleteCommand;

/**
 * This edit policy enables the removal of a Shapes instance from its container.
 * 
 * @see ShapeEditPart#createEditPolicies()
 * @see ShapeTreeEditPart#createEditPolicies()
 * @author Donald G. Dunne
 */
class ShapeComponentEditPolicy extends ComponentEditPolicy {

   @Override
   protected Command createDeleteCommand(GroupRequest deleteRequest) {
      Object parent = getHost().getParent().getModel();
      Object child = getHost().getModel();
      if (parent instanceof WorkDefinitionDiagram && child instanceof Shape) {
         return new ShapeDeleteCommand((WorkDefinitionDiagram) parent, (Shape) child);
      }
      return super.createDeleteCommand(deleteRequest);
   }
}