/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.orcs.rest.internal;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import org.eclipse.osee.framework.core.data.OseeClient;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * Shared utilities for cold storage operations (path resolution, SQL escaping, timestamp formatting).
 */
public final class ColdStorageUtil {

   private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

   private ColdStorageUtil() {
      // utility class
   }

   /**
    * Resolves the cold storage directory path, creating it if necessary.
    *
    * @return the absolute path to the cold_storage directory, or null if the server data path cannot be determined
    */
   public static String getColdStoragePath() {
      String serverPath = System.getProperty(OseeClient.OSEE_APPLICATION_SERVER_DATA);
      if (serverPath == null) {
         serverPath = System.getProperty("user.home");
      }
      if ("null".equals(serverPath)) {
         return null;
      }
      Path purgeFolder = Paths.get(serverPath + File.separator + "purge");
      if (Files.exists(purgeFolder)) {
         serverPath = purgeFolder.toString();
      }
      Path coldFolder = Paths.get(serverPath + File.separator + "cold_storage");
      try {
         Files.createDirectories(coldFolder);
      } catch (IOException ex) {
         throw OseeCoreException.wrap(ex);
      }
      return coldFolder.toString();
   }

   /**
    * Escapes single quotes and backslashes in a string for use in SQL INSERT statement previews. Also escapes newline
    * characters to prevent SQL injection in preview output.
    *
    * @param value the string to escape
    * @return the escaped string, or empty string if value is null
    */
   public static String escapeSql(String value) {
      if (value == null) {
         return "";
      }
      return value.replace("\\", "\\\\").replace("'", "''").replace("\n", "\\n").replace("\r", "\\r");
   }

   /**
    * Formats a Timestamp to a consistent ISO-like format suitable for SQL INSERT previews.
    *
    * @param ts the timestamp to format
    * @return formatted timestamp string in 'yyyy-MM-dd HH:mm:ss.SSS' format
    */
   public static String formatTimestamp(Timestamp ts) {
      if (ts == null) {
         return "";
      }
      return ts.toLocalDateTime().format(TIMESTAMP_FORMAT);
   }
}
