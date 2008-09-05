/*
 * Created on Jul 16, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.server.admin.management;

import org.eclipse.osee.framework.resource.common.IApplicationServerManager;
import org.eclipse.osee.framework.server.admin.BaseCmdWorker;

/**
 * @author Roberto E. Escobar
 */
class ServerShutdownWorker extends BaseCmdWorker {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.server.admin.search.BaseCmdWorker#doWork(long)
    */
   @Override
   protected void doWork(long startTime) throws Exception {
      IApplicationServerManager manager =
            org.eclipse.osee.framework.resource.common.Activator.getInstance().getApplicationServerManager();
      manager.shutdown();

      // TODO - more here!
   }
}
