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

import org.eclipse.draw2d.Bendpoint;
import org.eclipse.gef.commands.Command;
import org.eclipse.osee.framework.ui.data.model.editor.model.ConnectionModel;

/**
 * @author Roberto E. Escobar
 */
public class DeleteBendpointCommand extends Command {

   private ConnectionModel link;
   private Bendpoint bendpoint;
   private int index;

   public DeleteBendpointCommand(ConnectionModel link, int index) {
      super("Delete Bendpoint");
      this.link = link;
      this.index = index;
      bendpoint = (Bendpoint) link.getBendpoints().get(index);
   }

   public boolean canExecute() {
      return index >= 0 && bendpoint != null && link != null;
   }

   public void execute() {
      redo();
   }

   public void redo() {
      link.getBendpoints().remove(index);
   }

   public void undo() {
      link.getBendpoints().add(index, bendpoint);
   }

}
