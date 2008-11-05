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

package org.eclipse.osee.framework.db.connection.internal;

import static org.eclipse.osee.framework.jdk.core.util.OseeProperties.OSEE_DB_CONNECTION_ID;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.IDatabaseInfo;
import org.eclipse.osee.framework.db.connection.IDbConnectionInformation;
import org.eclipse.osee.framework.db.connection.IDbConnectionInformationContributer;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Andrew M. Finkbeiner
 */
public class DbConnectionInformationImpl implements IDbConnectionInformation, IBind {

   private Map<String, IDatabaseInfo> dbInfo;
   private IDatabaseInfo selectedDbInfo;

   public DbConnectionInformationImpl() {
      dbInfo = new HashMap<String, IDatabaseInfo>();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.IDbConnectionInformation#getDatabaseInfo(java.lang.String)
    */
   @Override
   public IDatabaseInfo getDatabaseInfo(String servicesId) {
      return dbInfo.get(servicesId);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.IDbConnectionInformation#getSelectedDatabaseInfo()
    */
   @Override
   public IDatabaseInfo getSelectedDatabaseInfo() {
      if (selectedDbInfo == null) {
         String dbConnectionId = System.getProperty(OSEE_DB_CONNECTION_ID);
         if (dbConnectionId != null && dbConnectionId.length() > 0) {
            selectedDbInfo = getDatabaseInfo(dbConnectionId);
            if (selectedDbInfo == null) {
               throw new IllegalStateException(String.format("DB connection information was not found. [%s]",
                     dbConnectionId));
            }
         } else {
            throw new IllegalStateException("No DB connection information provided");
         }
      }
      return selectedDbInfo;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.IBind#bind(java.lang.Object)
    */
   @Override
   public void bind(Object obj) {
      IDbConnectionInformationContributer contributer = (IDbConnectionInformationContributer) obj;
      try {
         for (IDatabaseInfo info : contributer.getDbInformation()) {
            dbInfo.put(info.getId(), info);
         }
      } catch (Exception ex) {
         OseeLog.log(InternalActivator.class, Level.SEVERE, ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.IBind#unbind(java.lang.Object)
    */
   @Override
   public void unbind(Object obj) {
      IDbConnectionInformationContributer contributer = (IDbConnectionInformationContributer) obj;
      try {
         for (IDatabaseInfo info : contributer.getDbInformation()) {
            dbInfo.remove(info.getDatabaseName());
         }
      } catch (Exception ex) {
         OseeLog.log(InternalActivator.class, Level.SEVERE, ex);
      }
   }

}
