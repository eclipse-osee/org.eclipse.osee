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
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.core.OseeApplicationServer;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.info.DbInformation;
import org.eclipse.osee.framework.db.connection.info.DbSetupData.ServerInfoFields;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;

/**
 * @author Roberto E. Escobar
 */
public class ApplicationServer {

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

   protected static void initialize() throws OseeDataStoreException {
      DbInformation dbInfo = OseeDbConnection.getDefaultDatabaseService();
      String resourceServer = dbInfo.getDatabaseSetupDetails().getServerInfoValue(ServerInfoFields.applicationServer);
      if (Strings.isValid(resourceServer) != true) {
         throw new OseeDataStoreException(
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
               OseeLog.log(SkynetActivator.class, Level.INFO, "Deleting binary data from application server...");
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
            OseeLog.log(SkynetActivator.class, Level.WARNING, "Unable to delete binary data from application server");
         }
      }
   }
}
