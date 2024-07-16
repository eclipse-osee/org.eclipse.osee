/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.client;

import org.eclipse.osee.framework.jdk.core.util.OseeProperties;

/**
 * @author Roberto E. Escobar
 */
public class OseeClientProperties extends OseeProperties {

   private static final OseeClientProperties instance = new OseeClientProperties();

   private static final String OSEE_LOCAL_APPLICATION_SERVER = "osee.local.application.server";
   private static final String OSEE_LOCAL_HTTP_WORKER_PORT = "osee.local.http.worker.port";

   // Database Initialization Properties

   private static final String OSEE_CHOICE_ON_DB_INIT = "osee.choice.on.db.init";

   private static final String OSEE_COMMIT_SKIP_CHECKS_AND_EVENTS = "osee.commit.skipChecksAndEvents";
   private static final String OSEE_COMMIT_SKIP_EVENTS = "osee.commit.skipEvents";

   public static boolean isSkipCommitChecksAndEvents() {
      return Boolean.valueOf(getProperty(OSEE_COMMIT_SKIP_CHECKS_AND_EVENTS, "false"));
   }

   public static boolean isSkipCommitEvents() {
      return Boolean.valueOf(getProperty(OSEE_COMMIT_SKIP_EVENTS, "false"));
   }

   /**
    * @return the predefined database initialization choice
    */
   public static String getChoiceOnDbInit() {
      return getProperty(OSEE_CHOICE_ON_DB_INIT);
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

   /**
    * A string representation of all the property setting specified by this class
    *
    * @return settings for all properties specified by this class
    */
   public static String getAllSettings() {
      return instance.toString();
   }

}
