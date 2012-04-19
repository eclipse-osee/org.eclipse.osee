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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.IDatabaseInfo;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IDbConnectionInformationContributor;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseHelper {
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.database";

   private final static List<IDbConnectionInformationContributor> contributors =
      new CopyOnWriteArrayList<IDbConnectionInformationContributor>();

   private static IDatabaseInfo selectedDbInfo;
   private static IOseeDatabaseService databaseService;

   public void setDatabaseService(IOseeDatabaseService databaseService) {
      DatabaseHelper.databaseService = databaseService;
   }

   public void addConnectionInfo(IDbConnectionInformationContributor contributor) {
      contributors.add(contributor);
   }

   public void removeConnectionInfo(IDbConnectionInformationContributor contributor) {
      contributors.remove(contributor);
   }

   public static IOseeDatabaseService getOseeDatabaseService() throws OseeDataStoreException {
      if (databaseService == null) {
         throw new OseeDataStoreException("OseeDatabaseService not found");
      }
      return databaseService;
   }

   public static IDatabaseInfo getDatabaseInfo(String serviceId) throws OseeCoreException {
      Conditions.checkNotNull(serviceId, "Service Id to find");
      return findDatabaseInfo(serviceId);
   }

   public static IDatabaseInfo getSelectedDatabaseInfo() throws OseeDataStoreException {
      if (selectedDbInfo == null) {
         String dbConnectionId = OseeProperties.getOseeDbConnectionId();
         if (Strings.isValid(dbConnectionId)) {
            selectedDbInfo = findDatabaseInfo(dbConnectionId);
         } else {
            throw new IllegalStateException("No DB connection information provided");
         }
      }
      return selectedDbInfo;
   }

   private static IDatabaseInfo findDatabaseInfo(String serverIdToFind) throws OseeDataStoreException {
      Set<String> infoKeys = new HashSet<String>();
      for (IDbConnectionInformationContributor contributor : contributors) {
         try {
            for (IDatabaseInfo databaseInfo : contributor.getDbInformation()) {
               String key = databaseInfo.getId();
               infoKeys.add(key);
               if (serverIdToFind.equals(key)) {
                  return databaseInfo;
               }
            }
         } catch (Exception ex) {
            OseeLog.log(DatabaseHelper.class, Level.SEVERE, ex);
         }
      }
      throw new OseeDataStoreException(
         "DB connection information was not found for: [%s]\n Available connection ids are: [%s]\n", serverIdToFind,
         infoKeys);
   }
}
