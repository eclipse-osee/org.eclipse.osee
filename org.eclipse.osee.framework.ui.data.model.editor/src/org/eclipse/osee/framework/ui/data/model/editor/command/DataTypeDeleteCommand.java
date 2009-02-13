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

import java.util.Iterator;
import java.util.List;
import org.eclipse.gef.commands.Command;
import org.eclipse.osee.framework.ui.data.model.editor.model.ConnectionModel;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.ODMGraph;

/**
 * @author Roberto E. Escobar
 */
public class DataTypeDeleteCommand extends Command {
   private final DataType child;

   private final ODMGraph parent;
   private List sourceConnections;
   private List targetConnections;
   private boolean wasRemoved;

   public DataTypeDeleteCommand(ODMGraph parent, DataType child) {
      if (parent == null || child == null) {
         throw new IllegalArgumentException();
      }
      setLabel("Data Type Deletion");
      this.parent = parent;
      this.child = child;
   }

   private void addConnections(List connections) {
      for (Iterator iter = connections.iterator(); iter.hasNext();) {
         ConnectionModel conn = (ConnectionModel) iter.next();
         conn.reconnect();
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.gef.commands.Command#canUndo()
    */
   public boolean canUndo() {
      return wasRemoved;
   }

   /* (non-Javadoc)
    * @see org.eclipse.gef.commands.Command#execute()
    */
   public void execute() {
      sourceConnections = child.getSourceConnections();
      targetConnections = child.getTargetConnections();
      redo();
   }

   public void redo() {
      wasRemoved = parent.remove(child);
      if (wasRemoved) {
         removeConnections(sourceConnections);
         removeConnections(targetConnections);
      }
   }

   private void removeConnections(List connections) {
      for (Iterator iter = connections.iterator(); iter.hasNext();) {
         ConnectionModel conn = (ConnectionModel) iter.next();
         conn.disconnect();
      }
   }

   public void undo() {
      if (parent.add(child)) {
         addConnections(sourceConnections);
         addConnections(targetConnections);
      }
   }
}