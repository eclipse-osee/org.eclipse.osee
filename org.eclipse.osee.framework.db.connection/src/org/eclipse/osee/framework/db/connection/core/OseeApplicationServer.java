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
package org.eclipse.osee.framework.db.connection.core;

import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Andrew M. Finkbeiner
 */
public class OseeApplicationServer {
   private static final String key = "osee.resource.server";
   private static String oseeServer = null;

   /**
    * @param string
    * @throws SQLException
    */
   public static void setApplicationOseeServer(String string) throws SQLException {
      OseeInfo.putValue(key, string);
   }

   public static String getOseeApplicationServer() throws SQLException {
      if (oseeServer == null) {
         oseeServer = OseeInfo.getValue(key);
      }
      if (Strings.isValid(oseeServer) != true) {
         throw new SQLException("Invalid resource server address in DB. Check OSEE_INFO table.");
      }
      return oseeServer;
   }

   public static boolean isApplicationServerAlive() {
      boolean canConnection = false;
      try {
         URL url = new URL(getOseeApplicationServer());
         URLConnection connection = url.openConnection();
         connection.connect();
         canConnection = true;
      } catch (Exception ex) {

      }
      return canConnection;
   }
}
