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
package org.eclipse.osee.framework.db.connection.internal.parser;

import java.util.Properties;
import java.util.Set;
import org.eclipse.osee.framework.db.connection.IDatabaseInfo;
import org.eclipse.osee.framework.db.connection.internal.parser.DbDetailData.ConfigField;
import org.eclipse.osee.framework.db.connection.internal.parser.DbSetupData.ServerInfoFields;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Roberto E. Escobar
 */
public class DbInformation implements IDatabaseInfo {

   private DbDetailData dbDetailData;
   private DbSetupData dbSetupData;
   private DbConnectionData dbConnectionData;

   public enum DbObjectType {
      ConnectionDescription, AvailableDbServices, DatabaseInfo
   }

   public DbInformation(DbDetailData dbDetailData, DbSetupData dbSetupData, DbConnectionData dbConnectionData) {
      this.dbDetailData = dbDetailData;
      this.dbSetupData = dbSetupData;
      this.dbConnectionData = dbConnectionData;
   }

   public DbConnectionData getConnectionData() {
      return dbConnectionData;
   }

   public DbDetailData getDatabaseDetails() {
      return dbDetailData;
   }

   public DbSetupData getDatabaseSetupDetails() {
      return dbSetupData;
   }

   public String getFormattedURL() {
      String toReturn = dbConnectionData.getRawUrl();
      Set<DbDetailData.ConfigField> keys = dbDetailData.getConfigMap().keySet();
      for (DbDetailData.ConfigField field : keys) {
         Pair<String, String> pair = dbDetailData.getConfigMap().get(field);
         if (pair.getValue().startsWith("@")) {
            DbObjectType type = DbObjectType.valueOf(pair.getValue().substring(1, pair.getValue().indexOf('.')));
            String value = pair.getValue().substring(pair.getValue().indexOf('.') + 1);
            String realValue = getValue(type, value);

            toReturn = toReturn.replace(pair.getKey(), realValue);
         } else {
            toReturn = toReturn.replace(pair.getKey(), pair.getValue());
         }
      }
      return toReturn;
   }

   private String getValue(DbObjectType type, String key) {
      switch (type) {
         case AvailableDbServices:
            return dbSetupData.getServerInfoValue(DbSetupData.ServerInfoFields.valueOf(key));
         case ConnectionDescription:
            break;
         case DatabaseInfo:
            break;
      }
      return "none";
   }

   public String toString() {
      return getFormattedURL() + " : user=" + getDatabaseLoginName();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.IDatabaseInfo#getConnectionUrl()
    */
   @Override
   public String getConnectionUrl() {
      return getFormattedURL() + getConnectionData().getAttributes();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.IDatabaseInfo#getConnectionProperties()
    */
   @Override
   public Properties getConnectionProperties() {
      Properties properties = getConnectionData().getProperties();
      properties.setProperty("user", getDatabaseLoginName());
      properties.setProperty("password", getDatabaseDetails().getFieldValue(ConfigField.Password));
      return properties;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.IDatabaseInfo#getDatabaseLoginName()
    */
   @Override
   public String getDatabaseLoginName() {
      return getDatabaseDetails().getFieldValue(ConfigField.UserName);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.IDatabaseInfo#getDatabaseName()
    */
   @Override
   public String getDatabaseName() {
      return getDatabaseDetails().getFieldValue(ConfigField.DatabaseName);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.IDatabaseInfo#getDriver()
    */
   @Override
   public String getDriver() {
      return getConnectionData().getDBDriver();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.IDatabaseInfo#getId()
    */
   @Override
   public String getId() {
      return getDatabaseSetupDetails().getId();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.IDatabaseInfo#isProduction()
    */
   @Override
   public boolean isProduction() {
      return Boolean.valueOf(getDatabaseSetupDetails().getServerInfoValue(ServerInfoFields.isProduction));
   }
}
