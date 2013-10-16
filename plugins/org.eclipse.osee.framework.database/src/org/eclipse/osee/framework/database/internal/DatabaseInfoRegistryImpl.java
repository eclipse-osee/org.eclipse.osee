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
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.database.DatabaseInfoRegistry;
import org.eclipse.osee.framework.database.core.IDbConnectionInformationContributor;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseInfoRegistryImpl implements DatabaseInfoRegistry {

   private final List<IDbConnectionInformationContributor> contributors =
      new CopyOnWriteArrayList<IDbConnectionInformationContributor>();

   private IDatabaseInfo selectedDbInfo;

   public void addConnectionInfo(IDbConnectionInformationContributor contributor) {
      contributors.add(contributor);
   }

   public void removeConnectionInfo(IDbConnectionInformationContributor contributor) {
      contributors.remove(contributor);
   }

   @Override
   public IDatabaseInfo getDatabaseInfo(String serviceId) throws OseeCoreException {
      Conditions.checkNotNull(serviceId, "Service Id to find");
      return findDatabaseInfo(serviceId);
   }

   @Override
   public IDatabaseInfo getSelectedDatabaseInfo() throws OseeDataStoreException {
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

   private IDatabaseInfo findDatabaseInfo(String serverIdToFind) throws OseeDataStoreException {
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
            OseeLog.log(DatabaseInfoRegistryImpl.class, Level.SEVERE, ex);
         }
      }
      throw new OseeDataStoreException(
         "DB connection information was not found for: [%s]\n Available connection ids are: [%s]\n", serverIdToFind,
         infoKeys);
   }
}
