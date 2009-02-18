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
import org.eclipse.osee.framework.ui.data.model.editor.model.NodeModel;

/**
 * @author Roberto E. Escobar
 */
public class ChangeBoundsCommand extends Command {

   private NodeModel node;
   private Point newLocation, oldLocation;
   private int newWidth, oldWidth;

   public ChangeBoundsCommand(NodeModel node, Point newLocation, int newWidth) {
      super("Change Bounds");
      setNode(node);
      setNewLocation(newLocation);
      this.newWidth = newWidth;
   }

   public boolean canExecute() {
      return node != null && newLocation != null && (newWidth == -1 || newWidth > 0);
   }

   public void execute() {
      oldLocation = node.getLocation();
      oldWidth = node.getWidth();
      redo();
   }

   public void redo() {
      node.setLocation(newLocation);
      node.setWidth(newWidth);
   }

   public void setNode(NodeModel node) {
      this.node = node;
   }

   public void setNewLocation(Point loc) {
      newLocation = loc;
   }

   public void undo() {
      node.setWidth(oldWidth);
      node.setLocation(oldLocation);
   }

}
