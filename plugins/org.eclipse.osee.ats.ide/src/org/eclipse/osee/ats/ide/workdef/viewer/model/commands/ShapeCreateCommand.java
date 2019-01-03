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
package org.eclipse.osee.ats.ide.workdef.viewer.model.commands;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.eclipse.osee.ats.ide.workdef.viewer.model.Shape;
import org.eclipse.osee.ats.ide.workdef.viewer.model.WorkDefinitionDiagram;

/**
 * A command to add a Shape to a ShapeDiagram. The command can be undone or redone.
 * 
 * @author Donald G. Dunne
 */
public class ShapeCreateCommand extends Command {

   /** The new shape. */
   private final Shape newShape;
   /** ShapeDiagram to add to. */
   private final WorkDefinitionDiagram parent;
   /** The bounds of the new Shape. */
   private final Rectangle bounds;

   /**
    * Create a command that will add a new Shape to a WorkflowDiagram.
    * 
    * @param newShape the new Shape that is to be added
    * @param parent the WorkflowDiagram that will hold the new element
    * @param bounds the bounds of the new shape; the size can be (-1, -1) if not known
    * @throws IllegalArgumentException if any parameter is null, or the request does not provide a new Shape instance
    */
   public ShapeCreateCommand(Shape newShape, WorkDefinitionDiagram parent, Rectangle bounds) {
      this.newShape = newShape;
      this.newShape.setWorkflowDiagram(parent);
      this.parent = parent;
      this.bounds = bounds;
      setLabel("shape creation");
   }

   /**
    * Can execute if all the necessary information has been provided.
    * 
    * @see org.eclipse.gef.commands.Command#canExecute()
    */
   @Override
   public boolean canExecute() {
      boolean create = newShape != null && parent != null && bounds != null;
      if (!create) {
         return false;
      }
      if (parent.hasChild(newShape)) {
         return false;
      }
      return true;
   }

   @Override
   public void execute() {
      newShape.setLocation(bounds.getLocation());
      Dimension size = bounds.getSize();
      if (size.width > 0 && size.height > 0) {
         newShape.setSize(size);
      }
      redo();
   }

   @Override
   public void redo() {
      parent.addChild(newShape);
   }

   @Override
   public void undo() {
      parent.removeChild(newShape);
   }

}