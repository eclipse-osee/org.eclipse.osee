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

import java.util.List;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.orcs.health.ServerStatus;
import org.eclipse.osee.orcs.health.StatusKey;

/**
 * @author Donald G. Dunne
 */
public class ServerHealthLogs {

   private final JdbcClient jdbcClient;

   public ServerHealthLogs(JdbcClient jdbcClient) {
      this.jdbcClient = jdbcClient;
   }

   public String getHtml() {
      List<String> servers = ServerUtils.getServers(jdbcClient);
      if (servers.size() == 0) {
         return AHTML.simplePage("No osee.health.servers configured in osee_info table");
      }

      StringBuilder sb = new StringBuilder();

      sb.append("<h3>OSEE Health - Server Health - Logs</h3>");
      sb.append("Select to see server log<br/><br/>");
      for (String server : servers) {
         String serverLogCurlUrl = getServerLogCurlUrl(server);
         if (serverLogCurlUrl.startsWith("Error")) {
            sb.append(server + " - " + serverLogCurlUrl);
         } else {
            sb.append(AHTML.getHyperlinkNewTab(serverLogCurlUrl, server));
         }
         sb.append("<br/><br/>");
      }
      String html = AHTML.simplePage(sb.toString());
      return html;
   }

   private String getServerLogCurlUrl(String server) {
      try {
         // First, get the server install location from health/status
         String statusUrlStr = String.format("http://%s%s", server, "/server/health/status");
         String statusResults = ServerUtils.getUrlResults(statusUrlStr);
         if (statusResults.contains("{\"data\" ")) {
            ServerStatus stat = JsonUtil.readValue(statusResults, ServerStatus.class);
            String appServerDir = stat.get(StatusKey.CodeLocation);
            String uri = stat.get(StatusKey.ServerUri);
            uri = uri.replaceFirst("http://", "");
            uri = uri.replaceFirst(":.*$", "");
            String port = stat.get(StatusKey.ServerUri);
            port = port.replaceFirst("^.*:", "");
            String filename = String.format("osee_app_server_%s_%s.log", uri, port);
            String catLogCmd = String.format("cat %s/logs/%s", appServerDir, filename);
            String curlExecUrl = ServerUtils.getCurlExecUrl(catLogCmd, jdbcClient);
            return curlExecUrl;
         } else {
            return String.format("Error: Unsuccessful reading health/status for server [%s] results [%s]", server,
               statusResults);
         }
      } catch (Exception ex) {
         return "Error: " + ex.getMessage();
      }
   }

}
