/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.operation;

import org.eclipse.osgi.framework.console.CommandInterpreter;

/**
 * @author Ryan D. Brooks
 */
public class CommandInterpreterReporter implements OperationReporter {
   private final CommandInterpreter ci;

   public CommandInterpreterReporter(CommandInterpreter ci) {
      this.ci = ci;
   }

   @Override
   public void report(String... row) {
      for (String cell : row) {
         ci.print(cell);
         ci.print("   ");
      }
      ci.println();
   }

}
