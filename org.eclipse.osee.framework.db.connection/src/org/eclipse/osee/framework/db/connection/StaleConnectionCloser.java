/*
 * Created on Dec 18, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.db.connection;

import java.util.TimerTask;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.internal.InternalActivator;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Ryan D. Brooks
 */
public class StaleConnectionCloser extends TimerTask {
   private final OseeConnectionPool connectionPool;

   /**
    * @param connectionPool
    */
   public StaleConnectionCloser(OseeConnectionPool connectionPool) {
      super();
      this.connectionPool = connectionPool;
   }

   /* (non-Javadoc)
    * @see java.util.TimerTask#run()
    */
   @Override
   public void run() {
      try {
         connectionPool.releaseUneededConnections();
      } catch (OseeDataStoreException ex) {
         OseeLog.log(InternalActivator.class, Level.SEVERE, ex);
      }
   }
}