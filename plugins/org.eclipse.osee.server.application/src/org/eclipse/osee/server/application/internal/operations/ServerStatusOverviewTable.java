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
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.jdbc.JdbcClient;

/**
 * @author Donald G. Dunne
 */
public class ServerStatusOverviewTable {

   private final JdbcClient jdbcClient;

   public ServerStatusOverviewTable(JdbcClient jdbcClient) {
      this.jdbcClient = jdbcClient;
   }

   public String getHtml() {
      List<String> servers = ServerUtils.getServers(jdbcClient);
      if (servers.size() == 0) {
         return AHTML.simplePage("No application.servers configured in osee.json file");
      }

      StringBuilder sb = new StringBuilder();

      sb.append("<h3>Server Status</h3>");
      sb.append(AHTML.beginMultiColumnTable(95, 3));
      List<String> headers = new LinkedList<>();
      headers.add("Name");
      headers.add("Alive");
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
         URL url = new URL(urlStr);
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         AcquireResult result = HttpProcessor.acquire(url, outputStream, 5000);
         if (result.wasSuccessful()) {
            values.add(ServerUtils.getImage(ServerUtils.GREEN_DOT, urlStr));
         } else {
            values.add(ServerUtils.getImage(ServerUtils.RED_DOT, urlStr));
         }
      } catch (Exception ex) {
         values.add(ServerUtils.getImage(ServerUtils.RED_DOT, urlStr));
      }
      urlStr = String.format("http://%s%s", server, "/orcs/datastore/info");
      try {
         URL url = new URL(urlStr);
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         AcquireResult result = HttpProcessor.acquire(url, outputStream, 5000);
         if (result.wasSuccessful()) {
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
