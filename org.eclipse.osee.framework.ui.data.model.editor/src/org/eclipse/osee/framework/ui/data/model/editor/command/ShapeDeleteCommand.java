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
public class ShapeDeleteCommand extends Command {
   private final DataType child;

   private final ODMGraph parent;
   private List sourceConnections;
   private List targetConnections;
   private boolean wasRemoved;

   /**
    * Create a command that will remove the shape from its parent.
    * 
    * @param parent the ShapesDiagram containing the child
    * @param child the Shape to remove
    * @throws IllegalArgumentException if any parameter is null
    */
   public ShapeDeleteCommand(ODMGraph parent, DataType child) {
      if (parent == null || child == null) {
         throw new IllegalArgumentException();
      }
      setLabel("shape deletion");
      this.parent = parent;
      this.child = child;
   }

   /**
    * Reconnects a List of Connections with their previous endpoints.
    * 
    * @param connections a non-null List of connections
    */
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
      // store a copy of incoming & outgoing connections before proceeding 
      sourceConnections = child.getSourceConnections();
      targetConnections = child.getTargetConnections();
      redo();
   }

   /* (non-Javadoc)
    * @see org.eclipse.gef.commands.Command#redo()
    */
   public void redo() {
      // remove the child and disconnect its connections
      wasRemoved = parent.remove(child);
      if (wasRemoved) {
         removeConnections(sourceConnections);
         removeConnections(targetConnections);
      }
   }

   /**
    * Disconnects a List of Connections from their endpoints.
    * 
    * @param connections a non-null List of connections
    */
   private void removeConnections(List connections) {
      for (Iterator iter = connections.iterator(); iter.hasNext();) {
         ConnectionModel conn = (ConnectionModel) iter.next();
         conn.disconnect();
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.gef.commands.Command#undo()
    */
   public void undo() {
      if (parent.add(child)) {
         addConnections(sourceConnections);
         addConnections(targetConnections);
      }
   }
}