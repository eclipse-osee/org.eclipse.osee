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
import java.io.File;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.client.BaseCredentialProvider;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.CoreClientActivator;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilder;
import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.data.OseeDatabaseId;
import org.eclipse.osee.framework.core.data.OseeInfo;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.database.IDbInitializationRule;
import org.eclipse.osee.framework.database.IDbInitializationTask;
import org.eclipse.osee.framework.database.data.SchemaData;
import org.eclipse.osee.framework.database.utility.DatabaseConfigurationData;
import org.eclipse.osee.framework.database.utility.DatabaseSchemaExtractor;
import org.eclipse.osee.framework.database.utility.DbInit;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.core.SequenceManager;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.info.SupportedDatabase;
import org.eclipse.osee.framework.jdk.core.db.DbConfigFileInformation;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.osgi.framework.Bundle;

/**
 * @author Andrew M. Finkbeiner
 */
public class SkynetDbInit implements IDbInitializationTask {
   private static final String ADD_PERMISSION =
         "INSERT INTO " + PERMISSION_TABLE.columnsForInsert("PERMISSION_ID", "PERMISSION_NAME");
   private static boolean isInDbInit;

   public void run() throws OseeCoreException {
      isInDbInit = true;
      DatabaseConfigurationData databaseConfigurationData = new DatabaseConfigurationData(getSchemaFiles());
      Map<String, SchemaData> userSpecifiedConfig = databaseConfigurationData.getUserSpecifiedSchemas();
      DatabaseSchemaExtractor schemaExtractor = new DatabaseSchemaExtractor(userSpecifiedConfig.keySet());
      schemaExtractor.extractSchemaData();
      Map<String, SchemaData> currentDatabaseConfig = schemaExtractor.getSchemas();
      Set<String> schemas = userSpecifiedConfig.keySet();
      DbInit.dropViews();
      DbInit.dropIndeces(schemas, userSpecifiedConfig, currentDatabaseConfig);
      DbInit.dropTables(schemas, userSpecifiedConfig, currentDatabaseConfig);
      if (SupportedDatabase.isDatabaseType(SupportedDatabase.postgresql)) {
         DbInit.dropSchema(schemas);
         DbInit.createSchema(schemas);
      }
      DbInit.addTables(schemas, userSpecifiedConfig);
      DbInit.addIndeces(schemas, userSpecifiedConfig);
      DbInit.addViews();
      initializeApplicationServer();
      OseeInfo.putValue(OseeDatabaseId.getKey(), GUID.generateGuidStr());
      populateSequenceTable();
      addDefaultPermissions();
   }

   public static boolean isDbInit() {
      return isInDbInit;
   }

   private static void initializeApplicationServer() throws OseeCoreException {
      try {
         Map<String, String> parameters = new HashMap<String, String>();
         parameters.put("registerToLookup", "true");
         String url =
               HttpUrlBuilder.getInstance().getOsgiServletServiceUrl(OseeServerContext.LOOKUP_CONTEXT, parameters);
         String response = HttpProcessor.post(new URL(url));
         OseeLog.log(Activator.class, Level.INFO, response);
      } catch (Exception ex1) {
         throw new OseeDataStoreException(ex1);
      }

      ClientSessionManager.authenticate(new BaseCredentialProvider() {

         @Override
         public OseeCredential getCredential() throws OseeCoreException {
            OseeCredential credential = new OseeCredential();
            credential.setUserName(SystemUser.BootStrap.getName());
            return credential;
         }

      });

      boolean displayWarning = false;
      String server = HttpUrlBuilder.getInstance().getApplicationServerPrefix();
      try {
         URL serverUrl = new URL(server);
         Socket socket = new Socket(serverUrl.getHost(), serverUrl.getPort());
         if (socket.getInetAddress().isLoopbackAddress()) {
            OseeLog.log(CoreClientActivator.class, Level.INFO, "Deleting binary data from application server...");
            String binaryDataPath = ClientSessionManager.getDataStorePath();
            Lib.deleteDir(new File(binaryDataPath + File.separator + "attr"));
            Lib.deleteDir(new File(binaryDataPath + File.separator + "snapshot"));
         } else {
            displayWarning = true;
         }
      } catch (Exception ex) {
         displayWarning = true;
      }
      if (displayWarning) {
         OseeLog.log(CoreClientActivator.class, Level.WARNING, "Unable to delete binary data from application server");
      }
   }

   private List<URL> getSchemaFiles() throws OseeCoreException {
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
                  try {
                     Class<?> taskClass = bundle.loadClass(initRuleClassName);
                     IDbInitializationRule rule = (IDbInitializationRule) taskClass.newInstance();
                     isAllowed = rule.isAllowed();
                  } catch (Exception ex) {
                     OseeLog.log(Activator.class, Level.SEVERE, ex);
                  }
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
    * @throws OseeDataStoreException
    */
   private void addDefaultPermissions() throws OseeDataStoreException {
      for (PermissionEnum permission : PermissionEnum.values()) {
         ConnectionHandler.runPreparedUpdate(ADD_PERMISSION, permission.getPermId(), permission.getName());
      }
   }

   /**
    * @throws OseeDataStoreException
    */
   private void populateSequenceTable() throws OseeDataStoreException {
      for (String sequenceName : SequenceManager.sequenceNames) {
         SequenceManager.internalInitializeSequence(sequenceName);
      }
   }
}
