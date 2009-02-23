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
package org.eclipse.osee.framework.ui.data.model.editor.command;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
import org.eclipse.osee.framework.ui.data.model.editor.model.ArtifactDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.ODMDiagram;

/**
 * @author Roberto E. Escobar
 */
public class CreateNodeCommand extends Command {

   private int width = -1;
   private Point location;
   private DataType node;
   private ODMDiagram diagram;

   public CreateNodeCommand(DataType newObject, ODMDiagram diagram, Point location) {
      super("Create Node");
      this.node = newObject;
      this.diagram = diagram;
      this.location = location;
   }

   public CreateNodeCommand(DataType newObject, ODMDiagram parent, Point location, int width) {
      this(newObject, parent, location);
      this.width = width;
   }

   public boolean canExecute() {
      return node != null && diagram != null && location != null && (width == -1 || width > 0);
   }

   public void execute() {
      redo();
   }

   public void redo() {
      if (node instanceof ArtifactDataType) {
         node.setLocation(location);
         node.setWidth(width);
         diagram.add(node);
      }
      // if (node instanceof StickyNote) ((StickyNote) node).setText("Comment");
   }

   public void undo() {
      if (node instanceof ArtifactDataType) {
         diagram.remove(node);
      }
   }
}
