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
package org.eclipse.osee.framework.database.internal.parser;

import java.util.Properties;
import java.util.Set;
import org.eclipse.osee.framework.core.data.IDatabaseInfo;
import org.eclipse.osee.framework.database.internal.parser.DbDetailData.ConfigField;
import org.eclipse.osee.framework.database.internal.parser.DbSetupData.ServerInfoFields;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Roberto E. Escobar
 */
public class DbInformation implements IDatabaseInfo {

   private static final long serialVersionUID = -4704655033702137367L;
   private final DbDetailData dbDetailData;
   private final DbSetupData dbSetupData;
   private final DbConnectionData dbConnectionData;

   public enum DbObjectType {
      ConnectionDescription,
      AvailableDbServices,
      DatabaseInfo
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
         if (pair.getSecond().startsWith("@")) {
            DbObjectType type = DbObjectType.valueOf(pair.getSecond().substring(1, pair.getSecond().indexOf('.')));
            String value = pair.getSecond().substring(pair.getSecond().indexOf('.') + 1);
            String realValue = getValue(type, value);

            toReturn = toReturn.replace(pair.getFirst(), realValue);
         } else {
            toReturn = toReturn.replace(pair.getFirst(), pair.getSecond());
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

   @Override
   public String toString() {
      return getFormattedURL() + " : user=" + getDatabaseLoginName();
   }

   @Override
   public String getConnectionUrl() {
      return getFormattedURL() + getConnectionData().getAttributes();
   }

   @Override
   public Properties getConnectionProperties() {
      Properties properties = getConnectionData().getProperties();
      properties.setProperty("user", getDatabaseLoginName());
      properties.setProperty("password", getDatabaseDetails().getFieldValue(ConfigField.Password));
      return properties;
   }

   @Override
   public String getDatabaseLoginName() {
      return getDatabaseDetails().getFieldValue(ConfigField.UserName);
   }

   @Override
   public String getDatabaseName() {
      return getDatabaseDetails().getFieldValue(ConfigField.DatabaseName);
   }

   @Override
   public String getDriver() {
      return getConnectionData().getDBDriver();
   }

   @Override
   public String getId() {
      return getDatabaseSetupDetails().getId();
   }

   @Override
   public boolean isProduction() {
      return Boolean.valueOf(getDatabaseSetupDetails().getServerInfoValue(ServerInfoFields.isProduction));
   }
}
