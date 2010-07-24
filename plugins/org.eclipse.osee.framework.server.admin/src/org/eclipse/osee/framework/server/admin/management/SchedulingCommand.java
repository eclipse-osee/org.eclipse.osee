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
import org.eclipse.osee.framework.server.admin.BaseServerCommand;
import org.eclipse.osgi.framework.console.CommandInterpreter;

/**
 * @author Ryan D. Brooks
 */
public class SchedulingCommand extends BaseServerCommand {
   private final long delayMiliseconds;
   private final int iterations;
   private final String command;

   public SchedulingCommand(CommandInterpreter ci) {
      super("Schedule", ci);
      delayMiliseconds = Integer.parseInt(ci.nextArgument()) * 1000;
      iterations = Integer.parseInt(ci.nextArgument());
      command = ci.nextArgument();
   }

   @Override
   protected void doCommandWork(IProgressMonitor monitor) throws Exception {
      for (int i = 0; i < iterations; i++) {
         Thread.sleep(delayMiliseconds);
         getCommandInterpreter().execute(command);
      }
   }
}