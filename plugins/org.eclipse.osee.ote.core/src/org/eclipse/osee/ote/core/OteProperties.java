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
package org.eclipse.osee.ote.core;

import org.eclipse.osee.framework.jdk.core.util.OseeProperties;

/**
 * @author Roberto E. Escobar
 */
public class OteProperties extends OseeProperties {
   private static final OteProperties instance = new OteProperties();
   private static final String OSEE_BENCHMARK = "osee.ote.benchmark";
   private static final String OSEE_CMD_CONSOLE = "osee.ote.cmd.console";
   private static final String OSEE_OTE_SERVER_TITLE = "osee.ote.server.title";
   private static final String OSEE_OTE_BATCH = "osee.ote.batch";
   private static final String OSEE_OTE_LOG_FILE_PATH = "osee.ote.logfilepath";

   private OteProperties() {
      super();
   }

   public static String getOseeOteServerTitle() {
      return System.getProperty(OSEE_OTE_SERVER_TITLE, "");
   }

   public static boolean isOseeOteInBatchModeEnabled() {
      return System.getProperty(OSEE_OTE_BATCH) != null;
   }

   public static String getOseeOteLogFilePath() {
      return System.getProperty(OSEE_OTE_LOG_FILE_PATH);
   }

   public static boolean isBenchmarkingEnabled() {
      return System.getProperty(OSEE_BENCHMARK) != null ? true : false;
   }

   public static boolean isOteCmdConsoleEnabled() {
      return System.getProperty(OSEE_CMD_CONSOLE) != null ? true : false;
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
