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

import java.sql.SQLException;
import org.eclipse.osee.framework.db.connection.OseeDb;
import org.eclipse.osee.framework.db.connection.core.OseeApplicationServer;
import org.eclipse.osee.framework.db.connection.info.DbInformation;
import org.eclipse.osee.framework.db.connection.info.DbSetupData.ServerInfoFields;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class ApplicationServer {

   protected static void initialize() throws SQLException {
      DbInformation dbInfo = OseeDb.getDefaultDatabaseService();
      String resourceServer = dbInfo.getDatabaseSetupDetails().getServerInfoValue(ServerInfoFields.applicationServer);
      if (Strings.isValid(resourceServer) != true) {
         throw new SQLException(
               String.format(
                     "Invalid resource server address [%s]. Please ensure db service info has a valid resource server defined.",
                     resourceServer));
      }
      OseeApplicationServer.setApplicationOseeServer(resourceServer);
   }
}
