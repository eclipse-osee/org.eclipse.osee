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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.OseeServerInfo;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.HttpProcessor;

/**
 * @author Andrew M. Finkbeiner
 * @author Donald G. Dunne
 */
public class OseeApplicationServer {

   public static ArbitrationServer arbitrationServer = new ArbitrationServer();
   public static ApplicationServer applicationServer = new ApplicationServer();
   private static DateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

   private OseeApplicationServer() {
      // private constructor
   }

   public static String getOseeApplicationServer() throws OseeCoreException {
      checkAndUpdateStatus();
      applicationServer.validate();
      return applicationServer.getOseeServer();
   }

   public static boolean isApplicationServerAlive() {
      checkAndUpdateStatus();
      return applicationServer.isAlive();
   }

   private static void checkAndUpdateStatus() {
      applicationServer.reset();
      if (!applicationServer.hasServerInfo()) {
         arbitrationServer.acquireApplicationServer(applicationServer);
      } else if (applicationServer.isOverrideArbitration()) {
         arbitrationServer.set(Level.INFO, null, "Arbitration Overridden");
      }
      OseeServerInfo serverInfo = applicationServer.getServerInfo();
      if (serverInfo != null) {
         boolean alive = HttpProcessor.isAlive(serverInfo.getServerAddress(), serverInfo.getPort());
         applicationServer.setAlive(alive);
         if (alive) {
            applicationServer.set(
               Level.INFO,
               null,
               String.format("%s %s Running Since: %s", applicationServer.getOseeServer(),
                  Arrays.deepToString(serverInfo.getVersion()), format.format(serverInfo.getDateStarted())));
         } else {
            applicationServer.set(Level.SEVERE, null,
               String.format("Unable to Connect to [%s]", applicationServer.getOseeServer()));
         }
      }
      arbitrationServer.report();
      applicationServer.report();
   }
}
