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
package org.eclipse.osee.server.application.internal.operations;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.util.HttpProcessor;
import org.eclipse.osee.framework.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.orcs.health.ServerStatus;
import org.eclipse.osee.orcs.health.StatusKey;

/**
 * @author Donald G. Dunne
 */
public class ServerStatusTable {

   private final JdbcClient jdbcClient;
   private final boolean details;

   public ServerStatusTable(JdbcClient jdbcClient, boolean details) {
      this.jdbcClient = jdbcClient;
      this.details = details;
   }

   public String getHtml() {

      List<String> servers = ServerUtils.getServers(jdbcClient);
      if (servers.size() == 0) {
         return AHTML.simplePage("No application.servers configured in osee.json file");
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
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         URL url = new URL(String.format("http://%s%s", server, "/server/health/status"));
         AcquireResult result = HttpProcessor.acquire(url, outputStream, 5000);
         if (result.wasSuccessful()) {
            values.add("Ok");
            String json = outputStream.toString(result.getEncoding());
            ServerStatus stat = JsonUtil.readValue(json, ServerStatus.class);
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
            values.add("Not successful: " + result.getResult());
         }
      } catch (Exception ex) {
         values.add("Exception: " + ex.getMessage());
      }
      sb.append(AHTML.addRowMultiColumnTable(values.toArray(new String[values.size()])));
   }

}
