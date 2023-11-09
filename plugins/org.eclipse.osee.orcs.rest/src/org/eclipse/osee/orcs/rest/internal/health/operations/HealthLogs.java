/*******************************************************************************
 * Copyright (c) 2023 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal.health.operations;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Jaden W. Puckett
 */
public class HealthLogs {
   private final String appServerDir;
   private final String serverUri;

   public HealthLogs(String appServerDir, String serverUri) {
      this.appServerDir = appServerDir;
      this.serverUri = serverUri;
   }

   public String getLogs() {
      if (this.appServerDir.length() == 0 || this.serverUri.length() == 0) {
         return "There are missing query parameters - Please try again";
      }
      // Build cat command
      String trimmedUri = serverUri.replaceFirst("http://", "");
      trimmedUri = trimmedUri.replaceFirst(":.*$", "");
      String port = serverUri.replaceFirst("^.*:", "");
      String filename = String.format("osee_app_server_%s_%s.log", trimmedUri, port);
      String catLogCmd = String.format("cat %s/logs/%s", this.appServerDir, filename);
      // Run cat command
      Scanner s = null;
      InputStream runtime = null;
      try {
         runtime = Runtime.getRuntime().exec(catLogCmd).getInputStream();
         s = new Scanner(runtime).useDelimiter("\\A");
         String results = s.hasNext() ? s.next() : "";
         results = String.format("cmd [%s] %s", catLogCmd, results);
         return results;
      } catch (Exception ex) {
         return String.format("cmd [%s] Exception: %s", catLogCmd, Lib.exceptionToString(ex));
      } finally {
         if (s != null) {
            s.close();
         }
         if (runtime != null) {
            try {
               runtime.close();
            } catch (IOException ex) {
               //do nothing
            }
         }
      }
   }

}
