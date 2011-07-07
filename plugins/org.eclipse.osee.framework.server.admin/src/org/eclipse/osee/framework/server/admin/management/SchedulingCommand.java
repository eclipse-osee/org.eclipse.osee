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
package org.eclipse.osee.framework.server.admin.management;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.server.admin.internal.Activator;
import org.eclipse.osgi.framework.console.CommandInterpreter;

/**
 * @author Ryan D. Brooks
 */
public class SchedulingCommand extends AbstractOperation {
   private final long delayMiliseconds;
   private final int iterations;
   private final String command;
   private final CommandInterpreter ci;

   public SchedulingCommand(OperationLogger logger, CommandInterpreter ci) {
      super("Schedule", Activator.PLUGIN_ID, logger);
      delayMiliseconds = Integer.parseInt(ci.nextArgument()) * 1000;
      iterations = Integer.parseInt(ci.nextArgument());

      StringBuilder strB = new StringBuilder(200);
      String argument;
      while ((argument = ci.nextArgument()) != null) {
         strB.append(argument);
         strB.append(" ");
      }

      command = strB.toString();
      this.ci = ci;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      for (int i = 0; i < iterations; i++) {
         Thread.sleep(delayMiliseconds);
         ci.execute(command);
      }
   }
}