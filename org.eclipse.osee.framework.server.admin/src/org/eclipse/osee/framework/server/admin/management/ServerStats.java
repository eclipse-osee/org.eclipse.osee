/*
 * Created on Jul 16, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.server.admin.management;

import java.util.List;
import org.eclipse.osee.framework.resource.common.IApplicationServerManager;
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
      IApplicationServerManager manager =
            org.eclipse.osee.framework.resource.common.Activator.getInstance().getApplicationServerManager();

      StringBuffer buffer = new StringBuffer();
      buffer.append("\n----------------------------------------------\n");
      buffer.append("                  Server Stats                \n");
      buffer.append("----------------------------------------------\n");
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
