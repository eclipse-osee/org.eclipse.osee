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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Jaden W. Puckett
 */
public class HealthStatus {
   private final JdbcClient jdbcClient;
   private String auth = "";

   public HealthStatus(JdbcClient jdbcClient, OrcsApi orcsApi) {
      this.jdbcClient = jdbcClient;
      this.auth = orcsApi.userService().getUser().getLoginIds().get(0);
   }

   public List<HealthServer> getServers() {
      List<HealthServer> servers = new ArrayList<>();
      List<String> serverStrings = HealthUtils.getServers(jdbcClient);

      for (String server : serverStrings) {
         HealthServer link = new HealthServer(server, false, false);
         // Check server + db alive
         try {
            String urlStr = String.format("http://%s%s", server, "/orcs/branches?branchUuids=1");
            String res = HealthUtils.getUrlResultsWithAuth(urlStr, auth);
            if (res.contains("\"System Root Branch\"")) {
               link.setServerAlive(true);
               link.setDbAlive(true);
            } else {
               link.setServerAlive(true);
               link.setDbAlive(false);
            }
         } catch (Exception ex) {
            link.setServerAlive(false);
            link.setDbAlive(false);
         }
         servers.add(link);
      }

      return servers;
   }

   public class HealthServer {
      private final String name;
      private Boolean serverAlive;
      private Boolean dbAlive;

      public HealthServer(String name, Boolean serverAlive, Boolean dbAlive) {
         this.name = name;
         this.serverAlive = serverAlive;
         this.dbAlive = dbAlive;
      }

      public void setServerAlive(Boolean serverAlive) {
         this.serverAlive = serverAlive;
      }

      public void setDbAlive(Boolean dbAlive) {
         this.dbAlive = dbAlive;
      }

      public String getName() {
         return this.name;
      }

      public Boolean getServerAlive() {
         return this.serverAlive;
      }

      public Boolean getDbAlive() {
         return this.dbAlive;
      }
   }
}
