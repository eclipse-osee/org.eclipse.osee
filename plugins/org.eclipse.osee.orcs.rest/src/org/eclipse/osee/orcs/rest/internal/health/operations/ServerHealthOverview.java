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

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.jdbc.JdbcClient;

/**
 * Shows table of list of servers (from OSEE_INFO) and pings for server alive and database connection from that server
 * works. Red/green icons will be shown based on ping status.
 *
 * @author Donald G. Dunne
 */
public class ServerHealthOverview {

   private final JdbcClient jdbcClient;

   public ServerHealthOverview(JdbcClient jdbcClient) {
      this.jdbcClient = jdbcClient;
   }

   public String getHtml() {
      List<String> servers = ServerUtils.getServers(jdbcClient);
      if (servers.size() == 0) {
         return AHTML.simplePage("No osee.health.servers configured in osee_info table");
      }

      StringBuilder sb = new StringBuilder();

      sb.append("<h3>OSEE Health - Server Health - Overview</h3>");
      sb.append(AHTML.beginMultiColumnTable(95, 3));
      List<String> headers = new LinkedList<>();
      headers.add("Server:Port (select to see server status)");
      headers.add("Server Alive");
      headers.add("DB Alive");
      sb.append(AHTML.addHeaderRowMultiColumnTable(headers));
      for (String server : servers) {
         addServer(sb, server);
      }
      sb.append(AHTML.endMultiColumnTable());

      String html = AHTML.simplePage(sb.toString());
      return html;
   }

   private void addServer(StringBuilder sb, String server) {
      List<String> values = new LinkedList<>();
      String statusUrl = "http://" + server + "/server/health/status";
      values.add(AHTML.getHyperlinkNewTab(statusUrl, server));
      String urlStr = String.format("http://%s%s", server, "/ide/versions");
      try {
         String results = ServerUtils.getUrlResults(urlStr);
         if (results.contains("\"versions\"")) {
            values.add(ServerUtils.getImage(ServerUtils.GREEN_DOT, urlStr));
         } else {
            values.add(ServerUtils.getImage(ServerUtils.RED_DOT, urlStr));
         }
      } catch (Exception ex) {
         values.add(ServerUtils.getImage(ServerUtils.RED_DOT, urlStr));
      }
      urlStr = String.format("http://%s%s", server, "/orcs/branches");
      try {
         String results = ServerUtils.getUrlResults(urlStr);
         if (results.contains("\"System Root Branch\"")) {
            values.add(ServerUtils.getImage(ServerUtils.GREEN_DOT, urlStr));
         } else {
            values.add(ServerUtils.getImage(ServerUtils.RED_DOT, urlStr));
         }
      } catch (Exception ex) {
         values.add(ServerUtils.getImage(ServerUtils.RED_DOT, urlStr));
      }
      sb.append(AHTML.addRowMultiColumnTable(values.toArray(new String[values.size()])));
   }

}
