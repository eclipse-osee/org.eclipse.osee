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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.util.HttpProcessor;
import org.eclipse.osee.framework.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.jdbc.JdbcClient;

/**
 * @author Donald G. Dunne
 */
public class ServerBalancerStatusTable {

   private final JdbcClient jdbcClient;

   public ServerBalancerStatusTable(JdbcClient jdbcClient) {
      this.jdbcClient = jdbcClient;
   }

   public String getHtml() {
      List<String> servers = ServerUtils.getServers(jdbcClient);
      if (servers.size() == 0) {
         return AHTML.simplePage("No application.servers configured in osee.json file");
      }

      StringBuilder sb = new StringBuilder();

      sb.append("<h3>Load Blancer Status</h3>");
      sb.append(AHTML.beginMultiColumnTable(95, 3));
      List<String> headers = new LinkedList<>();
      headers = new LinkedList<>();
      headers.add("Name");
      headers.add("Alive");
      sb.append(AHTML.addHeaderRowMultiColumnTable(headers));
      getBlanacerManagers(sb, servers);
      sb.append(AHTML.endMultiColumnTable());

      String html = AHTML.simplePage(sb.toString());
      return html;
   }

   private void getBlanacerManagers(StringBuilder sb, List<String> servers) {
      List<String> mgrVisited = new ArrayList<String>();
      for (String server : servers) {
         List<String> values = new LinkedList<>();
         server = server.replaceFirst(":.*$", "");
         if (!mgrVisited.contains(server)) {
            String balMgrUrl = "http://" + server + "/balancer-manager";
            values.add(AHTML.getHyperlinkNewTab(balMgrUrl, server));
            try {
               URL url = new URL(balMgrUrl);
               ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
               AcquireResult result = HttpProcessor.acquire(url, outputStream, 5000);
               if (result.wasSuccessful()) {
                  values.add(ServerUtils.getImage(ServerUtils.GREEN_DOT, balMgrUrl));
               } else {
                  values.add(ServerUtils.getImage(ServerUtils.RED_DOT, balMgrUrl));
               }
            } catch (Exception ex) {
               values.add(ServerUtils.getImage(ServerUtils.RED_DOT, balMgrUrl));
            }
            mgrVisited.add(server);
         }
         sb.append(AHTML.addRowMultiColumnTable(values.toArray(new String[values.size()])));
      }
   }

}
