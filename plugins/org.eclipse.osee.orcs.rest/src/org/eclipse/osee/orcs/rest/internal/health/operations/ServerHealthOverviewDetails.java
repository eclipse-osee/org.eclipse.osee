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
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.orcs.health.ServerStatus;
import org.eclipse.osee.orcs.health.StatusKey;

/**
 * Shows a basic table of servers and the basic server/status results. If details==true, then all fields will be shown
 * including threads and gc.
 *
 * @author Donald G. Dunne
 */
public class ServerHealthOverviewDetails {

   private final JdbcClient jdbcClient;
   private final boolean details;

   public ServerHealthOverviewDetails(JdbcClient jdbcClient, boolean details) {
      this.jdbcClient = jdbcClient;
      this.details = details;
   }

   public String getHtml() {

      List<String> servers = ServerUtils.getServers(jdbcClient);
      if (servers.size() == 0) {
         return AHTML.simplePage("No osee.health.servers configured in osee_info table");
      }

      StringBuilder sb = new StringBuilder();
      sb.append("<h3>Server Status</h3>");
      sb.append(AHTML.beginMultiColumnTable(95, 2));
      List<String> headers = new LinkedList<>();
      headers.add("Name");
      headers.add("Alive");
      for (StatusKey key : StatusKey.values()) {
         if (details || !key.isDetails()) {
            headers.add(key.name());
         }
      }
      sb.append(AHTML.addHeaderRowMultiColumnTable(headers));
      for (String server : servers) {
         addServer(sb, server, details);
      }
      sb.append(AHTML.endMultiColumnTable());
      return AHTML.simplePage(sb.toString());
   }

   private void addServer(StringBuilder sb, String server, boolean details) {
      List<String> values = new LinkedList<>();
      String statusUrl = "http://" + server + "/server/health/status";
      values.add(AHTML.getHyperlink(statusUrl, server));
      try {
         String urlStr = String.format("http://%s%s", server, "/server/health/status");
         String results = ServerUtils.getUrlResults(urlStr);
         if (results.contains("{\"data\" ")) {
            values.add("Ok");
            ServerStatus stat = JsonUtil.readValue(results, ServerStatus.class);
            for (StatusKey key : StatusKey.values()) {
               if (details || !key.isDetails()) {
                  String value = stat.get(key);
                  if (value == null) {
                     value = "";
                  }
                  values.add(value);
               }
            }
         } else {
            values.add("Not successful: " + results);
         }
      } catch (Exception ex) {
         values.add("Exception: " + ex.getMessage());
      }
      sb.append(AHTML.addRowMultiColumnTable(values.toArray(new String[values.size()])));
   }

}
