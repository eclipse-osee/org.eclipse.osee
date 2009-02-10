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