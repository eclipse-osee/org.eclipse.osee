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
package org.eclipse.osee.framework.skynet.core.usage;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;

/**
 * @author Robert A. Fisher
 */
public class SkynetUsageHandler extends Handler {
   boolean logOutReported;

   public SkynetUsageHandler() {
      logOutReported = false;
   }

   @Override
   public void close() throws SecurityException {
      try {
         /* Bug NSXR5 */
         if (UsageLog.isInstantiated()) {
            UsageLog log = UsageLog.getInstance();

            if (!logOutReported) {
               log.addEntry(new LogoutEntry());
               logOutReported = true;
            }
            log.writeOutLog();
         }
      } catch (OseeDataStoreException ex) {
         OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void flush() {
   }

   @Override
   public void publish(LogRecord record) {
      try {
         if (!OseeDbConnection.hasOpenConnection()) return;
      } catch (OseeDataStoreException ex) {
         OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
      }
      if (record.getLevel().intValue() == Level.SEVERE.intValue() && record.getThrown() instanceof Exception) {
         UsageLog.getInstance().addEntry(new ExceptionEntry((Exception) record.getThrown()));
      }
   }

}
