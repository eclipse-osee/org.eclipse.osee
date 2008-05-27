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

package org.eclipse.osee.framework.db.connection.impl;

import static org.eclipse.osee.framework.jdk.core.util.OseeProperties.DEFAULT_DB_CONNECTION;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.Activator;
import org.eclipse.osee.framework.db.connection.IBind;
import org.eclipse.osee.framework.db.connection.IDbConnectionInformation;
import org.eclipse.osee.framework.db.connection.IDbConnectionInformationContributer;
import org.eclipse.osee.framework.db.connection.info.DbInformation;
import org.eclipse.osee.framework.db.connection.info.DbDetailData.ConfigField;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Andrew M. Finkbeiner
 */
public class DbConnectionInformationImpl implements IDbConnectionInformation, IBind {

   private Map<String, DbInformation> dbInfo;
   private String lastDefault;
   private String dbConnectionId;
   private DbInformation defaultInfo;

   public DbConnectionInformationImpl() {
      dbInfo = new HashMap<String, DbInformation>();
      dbConnectionId = System.getProperty(DEFAULT_DB_CONNECTION);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.IDbConnectionInformation#getDatabaseInfo(java.lang.String)
    */
   @Override
   public DbInformation getDatabaseInfo(String servicesId) {
      return dbInfo.get(servicesId);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.IDbConnectionInformation#getDefaultDatabaseInfo()
    */
   @Override
   public DbInformation getDefaultDatabaseInfo() {
      if (defaultInfo == null) {
         if (dbConnectionId != null) {
            defaultInfo = getDatabaseInfo(dbConnectionId);
         } else if (lastDefault != null) {
            defaultInfo = getDatabaseInfo(lastDefault);
         }
      }
      if (defaultInfo == null) {
         throw new IllegalStateException("Unable to locate default DB connection information.");
      }
      return defaultInfo;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.IBind#bind(java.lang.Object)
    */
   @Override
   public void bind(Object obj) {
      IDbConnectionInformationContributer contributer = (IDbConnectionInformationContributer) obj;
      try {
         for (DbInformation info : contributer.getDbInformation()) {
            if (info.getDatabaseSetupDetails().isDefault()) {
               lastDefault = info.getDatabaseSetupDetails().getId();
            }
            dbInfo.put(info.getDatabaseSetupDetails().getId(), info);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class.getName(), Level.SEVERE, ex.getMessage(), ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.IBind#unbind(java.lang.Object)
    */
   @Override
   public void unbind(Object obj) {
      IDbConnectionInformationContributer contributer = (IDbConnectionInformationContributer) obj;
      try {
         for (DbInformation info : contributer.getDbInformation()) {
            dbInfo.remove(info.getDatabaseDetails().getFieldValue(ConfigField.DatabaseName));
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class.getName(), Level.SEVERE, ex.getMessage(), ex);
      }
   }

}
