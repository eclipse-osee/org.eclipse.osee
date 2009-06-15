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
package org.eclipse.osee.framework.core.client;

import java.net.URL;
import java.util.Dictionary;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author Roberto E. Escobar
 */
public class OseeClientProperties extends OseeProperties {

   private static final OseeClientProperties instance = new OseeClientProperties();

   private static final String HEADER_TAG = "OSEE-Client-Property-Init";

   private static final String OSEE_AUTHENTICATION_PROTOCOL = "osee.authentication.protocol";
   private static final String OSEE_APPLICATION_SERVER = "osee.application.server";
   private static final String OSEE_ARBITRATION_SERVER = "osee.arbitration.server";

   private static final String OSEE_LOCAL_APPLICATION_SERVER = "osee.local.application.server";
   private static final String OSEE_LOCAL_HTTP_WORKER_PORT = "osee.local.http.worker.port";

   // Database Initialization Properties
   private static final String OSEE_IMPORT_DURING_DB_INIT = "osee.import.on.db.init";
   private static final String OSEE_IMPORT_FROM_DB_SERVICE = "osee.import.from.connection.id.on.db.init";
   private static final String OSEE_USE_FILE_SPECIFIED_SCHEMAS = "osee.file.specified.schema.names.on.db.init";

   private static final String OSEE_TABLE_DATA_SPACE_ON_DB_INIT = "osee.table.data.space.on.db.init";
   private static final String OSEE_INDEX_DATA_SPACE_ON_DB_INIT = "osee.index.data.space.on.db.init";

   private static final String OSEE_PROMPT_ON_DB_INIT = "osee.prompt.on.db.init";
   private static final String OSEE_CHOICE_ON_DB_INIT = "osee.choice.on.db.init";

   private enum InitializerFlag {
      overwrite_settings, client_defaults;

      public static InitializerFlag fromString(String value) {
         InitializerFlag toReturn = client_defaults;
         if (Strings.isValid(value)) {
            value = value.toLowerCase();
            for (InitializerFlag flag : InitializerFlag.values()) {
               if (flag.name().equals(value)) {
                  toReturn = flag;
                  break;
               }
            }
         }
         return toReturn;
      }
   }

   private final Properties defaultProperties;
   private final Properties overwriteProperties;

   private OseeClientProperties() {
      super();
      this.defaultProperties = new Properties();
      this.overwriteProperties = new Properties();
      initialize();
   }

   /**
    * Retrieves where table data should be imported from during OSEE database initialization. The default is to use the
    * database connection id specified in the schema.xml files.
    * 
    * @return the connection id to use as a source of the data to be imported.
    */
   public static String getTableImportSource() {
      return getProperty(OSEE_IMPORT_FROM_DB_SERVICE);
   }

   /**
    * Retrieves whether OSEE database initialization should use the schema names specified in the schema.xml files
    * instead of using the connection schema. Using the connection specified schema is the default behavior.
    * 
    * @return <b>true</b> if the file specified schemas should be used.
    */
   public static boolean useSchemasSpecifiedInDbConfigFiles() {
      return Boolean.valueOf(getProperty(OSEE_USE_FILE_SPECIFIED_SCHEMAS));
   }

   /**
    * Retrieves the table data space to use during OSEE database initialization
    * 
    * @return table data space
    */
   public static String getOseeTableDataSpaceForDbInit() {
      return getProperty(OSEE_TABLE_DATA_SPACE_ON_DB_INIT);
   }

   /**
    * Retrieves the index data space to use during OSEE database initialization
    * 
    * @return index data space
    */
   public static String getOseeIndexDataSpaceForDbInit() {
      return getProperty(OSEE_INDEX_DATA_SPACE_ON_DB_INIT);
   }

   /**
    * @return whether to interactively prompt the user during database initialization for init choice
    */
   public static boolean promptOnDbInit() {
      return Boolean.valueOf(getProperty(OSEE_PROMPT_ON_DB_INIT, "true"));
   }

   /**
    * @return the predefined database initialization choice
    */
   public static String getChoiceOnDbInit() {
      return getProperty(OSEE_CHOICE_ON_DB_INIT);
   }

   /**
    * Retrieves whether OSEE database initialization should import database data as part of its tasks.
    * 
    * @return <b>true</b> if database initialization should import database data as part of its tasks.
    */
   public static boolean isOseeImportAllowed() {
      return Boolean.valueOf(getProperty(OSEE_IMPORT_DURING_DB_INIT));
   }

   /**
    * Gets whether local application server launch is required
    * 
    * @return <b>true</b> if local application server launch is required. <b>false</b> if local application server
    *         launch is not required.
    */
   public static boolean isLocalApplicationServerRequired() {
      return Boolean.valueOf(getProperty(OSEE_LOCAL_APPLICATION_SERVER));
   }

   /**
    * Retrieves the specified port to use for the local HTTP server
    * 
    * @return port to use
    */
   public static String getLocalHttpWorkerPort() {
      return getProperty(OSEE_LOCAL_HTTP_WORKER_PORT);
   }

   /**
    * Authentication Protocol to use
    * 
    * @return client/server authentication protocol.
    */
   public static String getAuthenticationProtocol() {
      return getProperty(OSEE_AUTHENTICATION_PROTOCOL);
   }

   /**
    * <pre>
    * Sets the application server address and port to use. This system property sets the URL used to reference
    * the application server. Arbitration is bypassed.
    * </pre>
    * 
    * <b>Format: </b> <code>http://address:port</code>
    * 
    * @param application server URL to use instead going through the arbitration server
    */
   public static void setOseeApplicationServer(String value) {
      System.setProperty(OSEE_APPLICATION_SERVER, value);
   }

   /**
    * <pre>
    * Retrieves the application server address and port to use. When specified, this system property sets the URL used to reference
    * the application server. Arbitration is bypassed.
    * </pre>
    * 
    * <b>Format: </b> <code>http://address:port</code>
    * 
    * @return application server URL to use instead going through the arbitration server
    */
   public static String getOseeApplicationServer() {
      return getProperty(OSEE_APPLICATION_SERVER);
   }

   /**
    * <pre>
    * Retrieves the arbitration server address and port to use. This system property must be specified for the system to
    * gain access to OSEE data. If the application server property is set then that address takes precedence and
    * arbitration is bypassed.
    * </pre>
    * 
    * <b>Format: </b> <code>http://address:port</code>
    * 
    * @return default arbitration server URL to set preferences.
    */
   public static String getOseeArbitrationServer() {
      return getProperty(OSEE_ARBITRATION_SERVER);
   }

   private static String getProperty(String name) {
      return getProperty(name, "");
   }

   private static String getProperty(String name, String defaultValue) {
      String toReturn = null;
      if (instance.overwriteProperties.containsKey(name)) {
         toReturn = instance.overwriteProperties.getProperty(name);
      } else if (instance.defaultProperties.containsKey(name)) {
         toReturn = System.getProperty(name, instance.defaultProperties.getProperty(name));
      } else {
         toReturn = System.getProperty(name, defaultValue);
      }
      return toReturn;
   }

   /**
    * A string representation of all the property setting specified by this class
    * 
    * @return settings for all properties specified by this class
    */
   public static String getAllSettings() {
      return instance.toString();
   }

   public void initialize() {
      BundleContext context = CoreClientActivator.getBundleContext();
      for (Bundle bundle : context.getBundles()) {
         Dictionary<?, ?> header = bundle.getHeaders();
         if (header != null) {
            String data = (String) header.get(HEADER_TAG);
            if (Strings.isValid(data)) {
               String[] entries = data.split(",");
               for (String entry : entries) {
                  int index = entry.indexOf(';');
                  if (index != -1) {
                     String resourcePath = entry.substring(0, index);
                     String flagString = entry.substring(index + 1, entry.length());
                     processInitializer(bundle, resourcePath, InitializerFlag.fromString(flagString));
                  } else {
                     processInitializer(bundle, entry, InitializerFlag.client_defaults);
                  }
               }
            }
         }
      }
   }

   private void processInitializer(Bundle bundle, String resourcePath, InitializerFlag flag) {
      URL url = bundle.getResource(resourcePath);
      if (url != null) {
         Properties properties = new Properties();
         try {
            properties.loadFromXML(url.openStream());
         } catch (Exception ex) {
            OseeLog.log(CoreClientActivator.class, Level.SEVERE, ex.toString(), ex);
         }
         if (!properties.isEmpty()) {
            OseeLog.log(CoreClientActivator.class, Level.INFO, String.format("Initializing properties [%s]", flag));

            Properties itemToSet = null;
            switch (flag) {
               case client_defaults:
                  itemToSet = defaultProperties;
                  break;
               case overwrite_settings:
                  itemToSet = overwriteProperties;
                  // Set System properties. Also cache the values to prevent application overwrites.
                  for (Entry<Object, Object> entry : properties.entrySet()) {
                     System.setProperty((String) entry.getKey(), (String) entry.getValue());
                  }
                  break;
            }

            if (itemToSet != null) {
               itemToSet.putAll(properties);
            }
         }
      }
   }
}
