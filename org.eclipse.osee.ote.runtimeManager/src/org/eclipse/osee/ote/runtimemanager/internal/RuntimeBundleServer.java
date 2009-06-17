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
package org.eclipse.osee.ote.runtimemanager.internal;

import java.net.BindException;
import java.net.InetAddress;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.CorePreferences;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.server.ClassServer;
import org.eclipse.osee.framework.plugin.core.server.ResourceFinder;
import org.eclipse.osee.ote.runtimemanager.RuntimeManager;
import org.eclipse.osee.ote.runtimemanager.SafeWorkspaceTracker;

public class RuntimeBundleServer {
   private ClassServer classServer;
   private String classServerPath;
   private ResourceFinder resourceFinder;

   /**
    * Creates a new ClassServer which will serve all projects currently in the workspace
    * @param safeWorkspaceTracker 
    * 
    * @param testManager
    */
   public RuntimeBundleServer(SafeWorkspaceTracker safeWorkspaceTracker) {
      try {
         InetAddress useHostAddress = CorePreferences.getDefaultInetAddress();
         classServer = new ClassServer(0, InetAddress.getLocalHost()){
            @Override
            protected void fileDownloaded(String fp, InetAddress addr) {
               System.out.println("RuntimeBundleServer: File " + fp + " downloaded to " + addr);
            }
         };
         resourceFinder = new RuntimeLibResourceFinder(safeWorkspaceTracker);
         classServer.addResourceFinder(resourceFinder);
         classServer.start();

         classServerPath = "http://" + useHostAddress.getHostAddress() + ":" + classServer.getPort() + "/";

      } catch (BindException ex) {
         OseeLog.log(
               RuntimeManager.class,
               Level.SEVERE,
               "Class Server not started.  Likely the IP address used is not local.  Set your IP address in the advanced page.",
               ex);
      } catch (Exception ex) {
         OseeLog.log(RuntimeManager.class, Level.SEVERE, "Class Server not started.", ex);
      }
   }

   /**
    * @return the path to the class server, to be passed to the environment upon connection
    */
   public String getClassServerPath() {
      return classServerPath;
   }

   /**
    * Stops the class server. This should be called upon stop of the RuntimeManager
    */
   public void stopServer() {
      classServer.terminate();
   }
}
