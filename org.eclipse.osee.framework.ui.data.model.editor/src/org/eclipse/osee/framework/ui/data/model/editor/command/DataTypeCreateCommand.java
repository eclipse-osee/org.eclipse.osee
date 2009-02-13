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

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.ODMGraph;

/**
 * @author Roberto E. Escobar
 */
public class DataTypeCreateCommand extends Command {

   private DataType dataType;
   private final ODMGraph parent;
   private Rectangle bounds;

   public DataTypeCreateCommand(DataType dataType, ODMGraph parent, Rectangle bounds) {
      this.dataType = dataType;
      this.parent = parent;
      this.bounds = bounds;
      setLabel("Type Creation");
   }

   public boolean canExecute() {
      return dataType != null && parent != null && bounds != null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.gef.commands.Command#execute()
    */
   public void execute() {
      dataType.setLocation(bounds.getLocation());
      Dimension size = bounds.getSize();
      if (size.width > 0 && size.height > 0) dataType.setSize(size);
      redo();
   }

   /* (non-Javadoc)
    * @see org.eclipse.gef.commands.Command#redo()
    */
   public void redo() {
      parent.add(dataType);
   }

   /* (non-Javadoc)
    * @see org.eclipse.gef.commands.Command#undo()
    */
   public void undo() {
      parent.remove(dataType);
   }

}