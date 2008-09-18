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

import java.io.File;
import java.net.Socket;
import java.net.URL;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.db.connection.OseeDb;
import org.eclipse.osee.framework.db.connection.core.OseeApplicationServer;
import org.eclipse.osee.framework.db.connection.info.DbInformation;
import org.eclipse.osee.framework.db.connection.info.DbSetupData.ServerInfoFields;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;

/**
 * @author Roberto E. Escobar
 */
public class ApplicationServer {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(ApplicationServer.class);

   private static OseeSession oseeSession;

   /**
    * @return the oseeSession
    */
   public static OseeSession getOseeSession() {
      if (oseeSession == null) {
         oseeSession = new OseeSession();
      }
      return oseeSession;
   }

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

      if (SkynetDbInit.isDbInit()) {
         boolean displayWarning = false;
         String server = OseeApplicationServer.getOseeApplicationServer();
         try {
            URL url = new URL(server);
            Socket socket = new Socket(url.getHost(), url.getPort());
            if (socket.getInetAddress().isLoopbackAddress()) {
               logger.log(Level.INFO, "Deleting binary data from application server...");
               String binaryDataPath = OseeProperties.getInstance().getOseeApplicationServerData();
               Lib.deleteDir(new File(binaryDataPath + File.separator + "attr"));
               Lib.deleteDir(new File(binaryDataPath + File.separator + "snapshot"));
            } else {
               displayWarning = true;
            }
         } catch (Exception ex) {
            displayWarning = true;
         }
         if (displayWarning) {
            logger.log(Level.WARNING, "Unable to delete binary data from application server");
         }
      }
   }
}
