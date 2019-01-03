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

import org.eclipse.gef.commands.Command;
import org.eclipse.osee.ats.ide.workdef.viewer.model.Connection;

/**
 * A command to disconnect (remove) a connection from its endpoints. The command can be undone or redone.
 * 
 * @author Donald G. Dunne
 */
public class ConnectionDeleteCommand extends Command {

   /** Connection instance to disconnect. */
   private final Connection connection;

   /**
    * Create a command that will disconnect a connection from its endpoints.
    * 
    * @param conn the connection instance to disconnect (non-null)
    * @throws IllegalArgumentException if conn is null
    */
   public ConnectionDeleteCommand(Connection conn) {
      if (conn == null) {
         throw new IllegalArgumentException();
      }
      setLabel("connection deletion");
      this.connection = conn;
   }

   @Override
   public void execute() {
      connection.disconnect();
   }

   @Override
   public void undo() {
      connection.reconnect();
   }
}
