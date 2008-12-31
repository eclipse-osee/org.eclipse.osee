/*******************************************************************************
 * Copyright (c) 2004, 2005 Donald G. Dunne and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Donald G. Dunne - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.workflow.editor.model.commands;

import java.util.Iterator;
import java.util.List;
import org.eclipse.gef.commands.Command;
import org.eclipse.osee.ats.workflow.editor.model.Connection;
import org.eclipse.osee.ats.workflow.editor.model.Shape;
import org.eclipse.osee.ats.workflow.editor.model.WorkflowDiagram;

/**
 * A command to remove a shape from its parent. The command can be undone or redone.
 * 
 * @author Donald G. Dunne
 */
public class ShapeDeleteCommand extends Command {
   /** Shape to remove. */
   private final Shape child;

   /** ShapeDiagram to remove from. */
   private final WorkflowDiagram parent;
   /** Holds a copy of the outgoing connections of child. */
   private List sourceConnections;
   /** Holds a copy of the incoming connections of child. */
   private List targetConnections;
   /** True, if child was removed from its parent. */
   private boolean wasRemoved;

   /**
    * Create a command that will remove the shape from its parent.
    * 
    * @param parent the WorkflowDiagram containing the child
    * @param child the Shape to remove
    * @throws IllegalArgumentException if any parameter is null
    */
   public ShapeDeleteCommand(WorkflowDiagram parent, Shape child) {
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
         Connection conn = (Connection) iter.next();
         conn.reconnect();
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.gef.commands.Command#canUndo()
    */
   @Override
   public boolean canUndo() {
      return wasRemoved;
   }

   /* (non-Javadoc)
    * @see org.eclipse.gef.commands.Command#execute()
    */
   @Override
   public void execute() {
      // store a copy of incoming & outgoing connections before proceeding 
      sourceConnections = child.getSourceConnections();
      targetConnections = child.getTargetConnections();
      redo();
   }

   /* (non-Javadoc)
    * @see org.eclipse.gef.commands.Command#redo()
    */
   @Override
   public void redo() {
      // remove the child and disconnect its connections
      wasRemoved = parent.removeChild(child);
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
         Connection conn = (Connection) iter.next();
         conn.disconnect();
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.gef.commands.Command#undo()
    */
   @Override
   public void undo() {
      // add the child and reconnect its connections
      if (parent.addChild(child)) {
         addConnections(sourceConnections);
         addConnections(targetConnections);
      }
   }
}