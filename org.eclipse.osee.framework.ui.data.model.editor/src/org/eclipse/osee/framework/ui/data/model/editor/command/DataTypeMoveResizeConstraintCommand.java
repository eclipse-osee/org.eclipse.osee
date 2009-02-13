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

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataType;

/**
 * @author Roberto E. Escobar
 */
public class DataTypeMoveResizeConstraintCommand extends Command {
   private final Rectangle newBounds;
   private Rectangle oldBounds;
   private final ChangeBoundsRequest request;
   private final DataType dataType;

   public DataTypeMoveResizeConstraintCommand(DataType dataType, ChangeBoundsRequest req, Rectangle newBounds) {
      if (dataType == null || req == null || newBounds == null) {
         throw new IllegalArgumentException();
      }
      this.dataType = dataType;
      this.request = req;
      this.newBounds = newBounds.getCopy();
      setLabel("move / resize");
   }

   /* (non-Javadoc)
    * @see org.eclipse.gef.commands.Command#canExecute()
    */
   public boolean canExecute() {
      Object type = request.getType();
      // make sure the Request is of a type we support:
      return (RequestConstants.REQ_MOVE.equals(type) || RequestConstants.REQ_MOVE_CHILDREN.equals(type) || RequestConstants.REQ_RESIZE.equals(type) || RequestConstants.REQ_RESIZE_CHILDREN.equals(type));
   }

   /* (non-Javadoc)
    * @see org.eclipse.gef.commands.Command#execute()
    */
   public void execute() {
      oldBounds = new Rectangle(dataType.getLocation(), dataType.getSize());
      redo();
   }

   /* (non-Javadoc)
    * @see org.eclipse.gef.commands.Command#redo()
    */
   public void redo() {
      dataType.setSize(newBounds.getSize());
      dataType.setLocation(newBounds.getLocation());
   }

   /* (non-Javadoc)
    * @see org.eclipse.gef.commands.Command#undo()
    */
   public void undo() {
      dataType.setSize(oldBounds.getSize());
      dataType.setLocation(oldBounds.getLocation());
   }
}
