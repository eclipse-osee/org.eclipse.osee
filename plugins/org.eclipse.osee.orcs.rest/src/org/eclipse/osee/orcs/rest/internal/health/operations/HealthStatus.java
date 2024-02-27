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
         HealthServer link = new HealthServer();
         // Check server + db alive
         String urlStr = server + "/orcs/branches?branchUuids=1";
         String res = HealthUtils.makeHttpRequestWithStringResult(urlStr, auth);
         if (res.contains("\"System Root Branch\"")) {
            link = new HealthServer(server, true, true, HealthUtils.getErrorMsg());
         } else if (HealthUtils.getErrorMsg() == "") {
            link = new HealthServer(server, true, false, HealthUtils.getErrorMsg());
         } else {
            link = new HealthServer(server, false, false, HealthUtils.getErrorMsg());
         }
         servers.add(link);
      }

      return servers;
   }

   public class HealthServer {
      private String name = "";
      private Boolean serverAlive = false;
      private Boolean dbAlive = false;
      private String errorMsg = "";

      public HealthServer() {
      }

      public HealthServer(String name, Boolean serverAlive, Boolean dbAlive, String errorMsg) {
         this.name = name;
         this.serverAlive = serverAlive;
         this.dbAlive = dbAlive;
         this.errorMsg = errorMsg;
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

      public String getErrorMsg() {
         return this.errorMsg;
      }
   }
}
