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

import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.orcs.rest.client.OseeClient;

/**
 * @author Roberto E. Escobar
 */
public class OseeClientProperties extends OseeProperties {

   private static final OseeClientProperties instance = new OseeClientProperties();

   private static final String OSEE_LOCAL_APPLICATION_SERVER = "osee.local.application.server";
   private static final String OSEE_LOCAL_HTTP_WORKER_PORT = "osee.local.http.worker.port";

   // Database Initialization Properties
   private static final String OSEE_IMPORT_DURING_DB_INIT = "osee.import.on.db.init";

   private static final String OSEE_PROMPT_ON_DB_INIT = "osee.prompt.on.db.init";
   private static final String OSEE_CHOICE_ON_DB_INIT = "osee.choice.on.db.init";

   private static final String OSEE_IS_IN_DB_INIT = "osee.is.in.db.init";

   private static final String OSEE_COMMIT_SKIP_CHECKS_AND_EVENTS = "osee.commit.skipChecksAndEvents";

   public static boolean isInDbInit() {
      return Boolean.valueOf(getProperty(OSEE_IS_IN_DB_INIT));
   }

   public static void setInDbInit(boolean value) {
      System.setProperty(OSEE_IS_IN_DB_INIT, Boolean.toString(value));
   }

   /**
    * @return whether to interactively prompt the user during database initialization for init choice
    */
   public static boolean promptOnDbInit() {
      return Boolean.valueOf(getProperty(OSEE_PROMPT_ON_DB_INIT, "true"));
   }

   public static boolean isSkipCommitChecksAndEvents() {
      return Boolean.valueOf(getProperty(OSEE_COMMIT_SKIP_CHECKS_AND_EVENTS, "false"));
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
    * launch is not required.
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
    * <pre>
    * Sets the application server address and port to use. This system property sets the URL used to reference
    * the application server.
    * </pre>
    *
    * <b>Format: </b> <code>http://address:port</code>
    *
    * @param application server URL to use
    */
   public static void setOseeApplicationServer(String value) {
      System.setProperty(OseeClient.OSEE_APPLICATION_SERVER, value);
   }

   /**
    * <pre>
    * Retrieves the application server address and port to use. When specified, this system property sets the URL used to reference
    * the application server.
    * </pre>
    *
    * <b>Format: </b> <code>http://address:port</code>
    *
    * @return application server URL
    */
   public static String getOseeApplicationServer() {
      return getProperty(OseeClient.OSEE_APPLICATION_SERVER, "http://localhost:8089");
   }

   private static String getProperty(String name) {
      return getProperty(name, "");
   }

   private static String getProperty(String name, String defaultValue) {
      return System.getProperty(name, defaultValue);
   }

   /**
    * A string representation of all the property setting specified by this class
    *
    * @return settings for all properties specified by this class
    */
   public static String getAllSettings() {
      return instance.toString();
   }

}
