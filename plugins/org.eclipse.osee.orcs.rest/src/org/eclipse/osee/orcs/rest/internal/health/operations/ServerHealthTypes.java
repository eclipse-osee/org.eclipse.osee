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
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.jdbc.JdbcClient;

/**
 * @author Donald G. Dunne
 */
public class ServerHealthTypes {

   private final JdbcClient jdbcClient;

   public ServerHealthTypes(JdbcClient jdbcClient) {
      this.jdbcClient = jdbcClient;
   }

   public String getHtml() {
      List<String> servers = ServerUtils.getServers(jdbcClient);
      if (servers.size() == 0) {
         return AHTML.simplePage("No osee.health.servers configured in osee_info table");
      }

      StringBuilder sb = new StringBuilder();

      sb.append("<h3>OSEE Health - Server Health - Types</h3>");
      sb.append("Select to see server Types Health<br/><br/>");
      for (String server : servers) {
         String statusUrl = "http://" + server + "/orcs/types/health";
         sb.append(AHTML.getHyperlinkNewTab(statusUrl, server));
         sb.append("<br/><br/>");
      }
      String html = AHTML.simplePage(sb.toString());
      return html;
   }

}
