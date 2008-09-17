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

package org.eclipse.osee.framework.skynet.core.dbinit;

import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.PERMISSION_TABLE;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.database.data.SchemaData;
import org.eclipse.osee.framework.database.initialize.tasks.DbInitializationTask;
import org.eclipse.osee.framework.database.utility.DatabaseConfigurationData;
import org.eclipse.osee.framework.database.utility.DatabaseSchemaExtractor;
import org.eclipse.osee.framework.database.utility.DbInit;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.core.OseeDbVersion;
import org.eclipse.osee.framework.db.connection.core.SequenceManager;
import org.eclipse.osee.framework.db.connection.info.SupportedDatabase;
import org.eclipse.osee.framework.jdk.core.db.DbConfigFileInformation;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.osgi.framework.Bundle;

/**
 * @author Andrew M. Finkbeiner
 */
public class SkynetDbInit extends DbInitializationTask {
   private static final String ADD_PERMISSION =
         "INSERT INTO " + PERMISSION_TABLE.columnsForInsert("PERMISSION_ID", "PERMISSION_NAME");
   private static boolean isInDbInit;

   public void run(Connection connection) throws Exception {
      setIsInDbInit(true);
      SkynetAuthentication.setBasicUsersCreated(false);
      DatabaseConfigurationData databaseConfigurationData = new DatabaseConfigurationData(connection, getSchemaFiles());
      Map<String, SchemaData> userSpecifiedConfig = databaseConfigurationData.getUserSpecifiedSchemas();
      DatabaseSchemaExtractor schemaExtractor = new DatabaseSchemaExtractor(connection, userSpecifiedConfig.keySet());
      schemaExtractor.extractSchemaData();
      Map<String, SchemaData> currentDatabaseConfig = schemaExtractor.getSchemas();
      SupportedDatabase databaseType = SupportedDatabase.getDatabaseType(connection);
      Set<String> schemas = userSpecifiedConfig.keySet();
      DbInit.dropViews(connection);
      DbInit.dropIndeces(schemas, userSpecifiedConfig, connection, databaseType, currentDatabaseConfig);
      DbInit.dropTables(schemas, userSpecifiedConfig, connection, databaseType, currentDatabaseConfig);
      DbInit.addTables(schemas, userSpecifiedConfig, connection, databaseType);
      DbInit.addIndeces(schemas, userSpecifiedConfig, connection, databaseType);
      DbInit.addViews(connection, databaseType);
      OseeDbVersion.initializeDbVersion(connection);
      ApplicationServer.initialize();
      populateSequenceTable();
      addDefaultPermissions();
   }

   public static boolean isDbInit() {
      return isInDbInit;
   }

   public static void setIsInDbInit(boolean isInDbInit) {
      SkynetDbInit.isInDbInit = isInDbInit;
   }

   private List<URL> getSchemaFiles() throws Exception {
      List<URL> toReturn = new ArrayList<URL>();
      List<IConfigurationElement> list =
            ExtensionPoints.getExtensionElements("org.eclipse.osee.framework.skynet.core.OseeDbSchema", "Schema");
      for (IConfigurationElement element : list) {
         String fileName = element.getAttribute("SchemaFile");
         String bundleName = element.getContributor().getName();
         String initRuleClassName = element.getAttribute("DbInitRule");

         if (Strings.isValid(bundleName) && Strings.isValid(fileName)) {
            if (false != fileName.endsWith(DbConfigFileInformation.getSchemaFileExtension())) {
               Bundle bundle = Platform.getBundle(bundleName);

               boolean isAllowed = true;
               if (Strings.isValid(initRuleClassName)) {
                  isAllowed = false;
                  Class<?> taskClass = bundle.loadClass(initRuleClassName);
                  IDbInitRule rule = (IDbInitRule) taskClass.newInstance();
                  isAllowed = rule.isAllowed();
               }

               if (isAllowed) {
                  URL url = bundle.getEntry(fileName);
                  if (url != null) {
                     System.out.println("Adding Schema: [" + fileName + "]");
                     toReturn.add(url);
                  }
               }
            }
         }
      }
      return toReturn;
   }

   /**
    * @throws SQLException
    */
   private void addDefaultPermissions() throws SQLException {
      for (PermissionEnum permission : PermissionEnum.values()) {
         ConnectionHandler.runPreparedUpdate(ADD_PERMISSION, permission.getPermId(), permission.getName());
      }
   }

   /**
    * @throws SQLException
    */
   private void populateSequenceTable() throws SQLException {
      SequenceManager seqManager = SequenceManager.getInstance();
      for (String sequenceName : SequenceManager.sequenceNames) {
         seqManager.initializeSequence(sequenceName);
      }
   }
}
