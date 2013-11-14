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

package org.eclipse.osee.framework.core.client.internal;

import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.core.data.OseeServerInfo;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Andrew M. Finkbeiner
 * @author Donald G. Dunne
 */
public class OseeApplicationServer {

   private static final ApplicationServer applicationServer = new ApplicationServer();

   private OseeApplicationServer() {
      // private constructor
   }

   public static void reset() {
      applicationServer.resetStatus();

      applicationServer.setServerInfo(null);
   }

   public static String getOseeApplicationServer() throws OseeCoreException {
      checkAndUpdateStatus();
      String serverAddress = applicationServer.getServerAddress();
      Conditions.checkNotNull(serverAddress, "resource server address");
      return serverAddress;
   }

   public static boolean isApplicationServerAlive() {
      checkAndUpdateStatus();
      return applicationServer.isAlive();
   }

   private synchronized static void checkAndUpdateStatus() {
      if (!applicationServer.isServerInfoValid()) {
         applicationServer.resetStatus();
         OseeServerInfo serverInfo = null;
         String appServerUri = OseeClientProperties.getOseeApplicationServer();
         if (Strings.isValid(appServerUri)) {
            try {
               serverInfo =
                  new OseeServerInfo(applicationServer.getServerAddress(), appServerUri,
                     new String[] {OseeCodeVersion.getVersion()}, new Timestamp(new Date().getTime()), true);
            } catch (Exception ex) {
               OseeLog.log(CoreClientActivator.class, Level.SEVERE, ex);
               applicationServer.set(Level.SEVERE, ex, "Error parsing server property [%s]", appServerUri);
            }
         }
         applicationServer.setServerInfo(serverInfo);
      }
      applicationServer.checkAlive();
      applicationServer.report();
   }

}
