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
package org.eclipse.osee.ote.ui.host.cmd;

import org.eclipse.osee.framework.jdk.core.type.TreeObject;
import org.eclipse.osee.ote.core.OSEEPerson1_4;
import org.eclipse.osee.ote.core.environment.interfaces.IRemoteCommandConsole;


/**
 * @author Ken J. Aguilar
 */
public class ConsoleNode extends TreeObject {

   private final IRemoteCommandConsole console;
   private String users;
   
   public ConsoleNode(IRemoteCommandConsole console, OSEEPerson1_4[] users) {
      super("Console");
      this.console = console;
      if (users.length > 1) {
         StringBuilder sb = new StringBuilder(128);
         sb.append(users[0].getName());
         sb.append(", ");
         for (int i = 1; i < users.length - 1; i++) {
            sb.append(users[i].getName());
            sb.append(", ");
         }
         sb.append(users[users.length -1]);
         this.users = sb.toString();
      } else {
         this.users = users.length == 0 ? "<none>" : users[0].getName();
      }
   }
   
   public IRemoteCommandConsole getConsole() {
      return console;
   }

   public String getUsers() {
      return users;
   }
}
