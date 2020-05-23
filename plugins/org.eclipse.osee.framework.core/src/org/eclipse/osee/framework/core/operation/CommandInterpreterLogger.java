/*********************************************************************
 * Copyright (c) 2010 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.operation;

import org.eclipse.osgi.framework.console.CommandInterpreter;

/**
 * @author Ryan D. Brooks
 */
public class CommandInterpreterLogger extends OperationLogger {
   private final CommandInterpreter ci;

   public CommandInterpreterLogger(CommandInterpreter ci) {
      this.ci = ci;
   }

   @Override
   public void log(String... row) {
      for (String cell : row) {
         ci.print(cell);
         ci.print("   ");
      }
      ci.println();
   }

   @Override
   public void log(Throwable th) {
      ci.printStackTrace(th);
   }
}