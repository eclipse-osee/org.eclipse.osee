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
package org.eclipse.osee.framework.server.admin.management;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.server.CoreServerActivator;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.server.admin.BaseCmdWorker;

/**
 * @author Roberto E. Escobar
 */
class ServerStats extends BaseCmdWorker {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.server.admin.search.BaseCmdWorker#doWork(long)
    */
   @Override
   protected void doWork(long startTime) throws Exception {
      IApplicationServerManager manager = CoreServerActivator.getApplicationServerManager();

      StringBuffer buffer = new StringBuffer();
      buffer.append("\n----------------------------------------------\n");
      buffer.append("                  Server Stats                \n");
      buffer.append("----------------------------------------------\n");
      buffer.append("Osee Application Server: ");
      buffer.append(Arrays.deepToString(CoreServerActivator.getApplicationServerManager().getSupportedVersions()));
      buffer.append("\n");
      buffer.append(String.format("Server State: [%s]\n", manager.isSystemIdle() ? "IDLE" : "BUSY"));
      buffer.append(String.format("Active Threads: [%s]\n", manager.getNumberOfActiveThreads()));
      buffer.append("Current Tasks: ");
      List<String> entries = manager.getCurrentProcesses();
      if (entries.isEmpty()) {
         buffer.append("[NONE]");
      } else {
         buffer.append("\n");
         for (int index = 0; index < entries.size(); index++) {
            buffer.append(String.format("[%s] ", index));
            buffer.append(entries.get(index));
            if (index + 1 < entries.size()) {
               buffer.append("\n");
            }
         }
      }
      println(buffer.toString());
   }
}
