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

import java.util.Iterator;
import org.eclipse.gef.commands.Command;
import org.eclipse.osee.ats.ide.workdef.viewer.model.Connection;
import org.eclipse.osee.ats.ide.workdef.viewer.model.DefaultTransitionConnection;
import org.eclipse.osee.ats.ide.workdef.viewer.model.ReturnTransitionConnection;
import org.eclipse.osee.ats.ide.workdef.viewer.model.Shape;
import org.eclipse.osee.ats.ide.workdef.viewer.model.TransitionConnection;

/**
 * A command to create a connection between two shapes. The command can be undone or redone.
 * <p>
 * This command is designed to be used together with a GraphicalNodeEditPolicy. To use this command properly, following
 * steps are necessary:
 * </p>
 * <ol>
 * <li>Create a subclass of GraphicalNodeEditPolicy.</li>
 * <li>Override the <tt>getConnectionCreateCommand(...)</tt> method, to create a new instance of this class and put it
 * into the CreateConnectionRequest.</li>
 * <li>Override the <tt>getConnectionCompleteCommand(...)</tt> method, to obtain the Command from the ConnectionRequest,
 * call setTarget(...) to set the target endpoint of the connection and return this command instance.</li>
 * </ol>
 * 
 * @see org.eclipse.osee.ats.ide.workflow.editor.parts.ShapeEditPart#createEditPolicies() for an example of the above
 * procedure.
 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy
 * @author Donald G. Dunne
 */
public class ConnectionCreateCommand extends Command {
   /** The connection instance. */
   private Connection connection;

   /** Start endpoint for the connection. */
   private final org.eclipse.osee.ats.ide.workdef.viewer.model.Shape source;
   /** Target endpoint for the connection. */
   private Shape target;
   private final Class<?> clazz;

   /**
    * Instantiate a command that can create a connection between two shapes.
    * 
    * @param source the source endpoint (a non-null Shape instance)
    * @param lineStyle the desired line style. See Connection#setLineStyle(int) for details
    * @throws IllegalArgumentException if source is null
    * @see Connection#setLineStyle(int)
    */
   @SuppressWarnings("rawtypes")
   public ConnectionCreateCommand(Shape source, Class clazz) {
      this.clazz = clazz;
      if (source == null) {
         throw new IllegalArgumentException();
      }
      setLabel("connection creation");
      this.source = source;
   }

   @Override
   public boolean canExecute() {
      // disallow source -> source connections
      if (source.equals(target)) {
         return false;
      }
      // return false, if the source -> target connection exists already
      for (Iterator<?> iter = source.getSourceConnections().iterator(); iter.hasNext();) {
         Connection conn = (Connection) iter.next();
         if (conn != null && conn.getTarget().equals(target)) {
            return false;
         }
      }
      return true;
   }

   @Override
   public void execute() {
      // create a new connection between source and target
      if (clazz == DefaultTransitionConnection.class) {
         connection = new DefaultTransitionConnection(source, target);
      } else if (clazz == ReturnTransitionConnection.class) {
         connection = new ReturnTransitionConnection(source, target);
      } else if (clazz == TransitionConnection.class) {
         connection = new TransitionConnection(source, target);
      } else {
         throw new IllegalStateException("Unhandled connection type");
      }
   }

   @Override
   public void redo() {
      connection.reconnect();
   }

   /**
    * Set the target endpoint for the connection.
    * 
    * @param target that target endpoint (a non-null Shape instance)
    * @throws IllegalArgumentException if target is null
    */
   public void setTarget(Shape target) {
      if (target == null) {
         throw new IllegalArgumentException();
      }
      this.target = target;
   }

   @Override
   public void undo() {
      connection.disconnect();
   }
}
