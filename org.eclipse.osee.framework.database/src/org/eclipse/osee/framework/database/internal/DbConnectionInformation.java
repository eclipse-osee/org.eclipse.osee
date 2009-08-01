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

package org.eclipse.osee.framework.database.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.IDatabaseInfo;
import org.eclipse.osee.framework.database.core.IDbConnectionInformationContributor;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Andrew M. Finkbeiner
 */
public class DbConnectionInformation implements IDbConnectionInformation {

   private final Map<String, IDatabaseInfo> dbInfo = new HashMap<String, IDatabaseInfo>();
   private IDatabaseInfo selectedDbInfo;
   private final Object myWait = new Object();

   @Override
   public IDatabaseInfo getDatabaseInfo(String servicesId) {
      return dbInfo.get(servicesId);
   }

   @Override
   public IDatabaseInfo getSelectedDatabaseInfo() {
      if (selectedDbInfo == null) {
         String dbConnectionId = OseeProperties.getOseeDbConnectionId();
         if (dbConnectionId != null && dbConnectionId.length() > 0) {
            selectedDbInfo = getDatabaseInfo(dbConnectionId);
            if (selectedDbInfo == null) {
               long endTime = System.currentTimeMillis() + (1000 * 20);
               long timeLeft = 1000 * 20;
               while (timeLeft > 0 && selectedDbInfo == null) {
                  synchronized (myWait) {
                     try {
                        myWait.wait(timeLeft);
                     } catch (InterruptedException ex) {
                     }
                     selectedDbInfo = getDatabaseInfo(dbConnectionId);
                  }
                  timeLeft = endTime - System.currentTimeMillis();
               }
               if (selectedDbInfo == null) {
                  throw new IllegalStateException(String.format(
                        "DB connection information was not found for: [%s]\n Available connection ids are: [%s]\n",
                        dbConnectionId, dbInfo.keySet()));
               }
            }
         } else {
            throw new IllegalStateException("No DB connection information provided");
         }
      }
      return selectedDbInfo;
   }

   @Override
   public void bind(IDbConnectionInformationContributor obj) {
      IDbConnectionInformationContributor contributor = (IDbConnectionInformationContributor) obj;
      try {
         for (IDatabaseInfo info : contributor.getDbInformation()) {
            dbInfo.put(info.getId(), info);
         }
      } catch (Exception ex) {
         OseeLog.log(InternalActivator.class, Level.SEVERE, ex);
      }
      synchronized (myWait) {
         myWait.notifyAll();
      }
   }

   @Override
   public void unbind(IDbConnectionInformationContributor obj) {
      IDbConnectionInformationContributor contributor = (IDbConnectionInformationContributor) obj;
      try {
         for (IDatabaseInfo info : contributor.getDbInformation()) {
            dbInfo.remove(info.getDatabaseName());
         }
      } catch (Exception ex) {
         OseeLog.log(InternalActivator.class, Level.SEVERE, ex);
      }
   }
}
