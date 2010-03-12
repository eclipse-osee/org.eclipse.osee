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

import org.eclipse.draw2d.AbsoluteBendpoint;
import org.eclipse.draw2d.Bendpoint;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
import org.eclipse.osee.framework.ui.data.model.editor.model.ConnectionModel;

/**
 * @author Roberto E. Escobars
 */
public class MoveBendpointCommand extends Command {

   private ConnectionModel connectionModel;
   private int index;
   private Bendpoint oldBendpoint, newBendpoint;

   public MoveBendpointCommand(ConnectionModel connectionModel, Point location, int index) {
      super("Move Bendpoint");
      this.connectionModel = connectionModel;
      this.index = index;
      newBendpoint = new AbsoluteBendpoint(location);
      oldBendpoint = (Bendpoint) connectionModel.getBendpoints().get(index);
   }

   public boolean canExecute() {
      return connectionModel != null && index >= 0 && newBendpoint != null && oldBendpoint != null;
   }

   public void execute() {
      connectionModel.getBendpoints().set(index, newBendpoint);
   }

   public void undo() {
      connectionModel.getBendpoints().set(index, oldBendpoint);
   }

}
