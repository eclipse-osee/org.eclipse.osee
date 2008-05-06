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
package org.eclipse.osee.framework.skynet.core.attribute;

import java.sql.SQLException;
import org.eclipse.osee.framework.db.connection.core.OseeInfo;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Andrew M. Finkbeiner
 */
public class OseeResourceServer {
   private static final String key = "osee.resource.server";
   private static String oseeServer = null;

   /**
    * @param string
    * @throws SQLException
    */
   public static void setOseeServer(String string) throws SQLException {
      OseeInfo.putValue(key, string);
   }

   public static String getOseeServer() throws SQLException {
      if (oseeServer == null) {
         oseeServer = OseeInfo.getValue(key);
      }
      if (Strings.isValid(oseeServer) != true) {
         throw new SQLException("Invalid resource server address in DB. Check OSEE_INFO table.");
      }
      return oseeServer;
   }
}
