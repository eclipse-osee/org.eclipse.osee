/*******************************************************************************
 * Copyright (c) 2022 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal.health.operations;

import java.util.Scanner;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;

/**
 * @author Donald G. Dunne
 */
public class ServerHealthProcesses {

   private final JdbcClient jdbcClient;

   public ServerHealthProcesses(JdbcClient jdbcClient) {
      this.jdbcClient = jdbcClient;
   }

   public String getHtml() {
      StringBuilder sb = new StringBuilder();
      try {
         String psResults = "";
         if (Lib.isWindows()) {
            if (ServerUtils.isCurlServerSet(jdbcClient)) {
               psResults = ServerUtils.runCurlExecFromCurlServer("ps%20-ef", jdbcClient);
            }
            if (Strings.isInValid(psResults)) {
               sb.append("<h3>ps -ef is not available for windows (example below)</h3>");
               String str = OseeInf.getResourceContents("web/health/psef.txt", ServerHealthProcesses.class);
               str = String.format("<pre>%s</pre>", str);
               sb.append(str);
               return AHTML.simplePage(sb.toString());
            }
         } else {
            sb.append("<h3>Machine processes with java in cmd</h3>");
            try (Scanner s = new Scanner(Runtime.getRuntime().exec("ps -ef").getInputStream()).useDelimiter("\\A")) {
               psResults = s.hasNext() ? s.next() : "";
            }
         }
         for (String line : psResults.split("<br/>")) {
            if (line.contains("java")) {
               sb.append(line + "<br/><br/>");
            }
         }
      } catch (Exception ex) {
         sb.append(Lib.exceptionToString(ex));
      }
      return AHTML.simplePage(sb.toString());
   }

}
