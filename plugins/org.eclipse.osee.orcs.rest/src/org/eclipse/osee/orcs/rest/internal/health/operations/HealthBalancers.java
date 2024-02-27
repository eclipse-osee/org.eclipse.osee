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
public class HealthBalancers {
   private final JdbcClient jdbcClient;
   private String auth = "";

   public HealthBalancers(JdbcClient jdbcClient, OrcsApi orcsApi) {
      this.jdbcClient = jdbcClient;
      this.auth = orcsApi.userService().getUser().getLoginIds().get(0);
   }

   public List<HealthBalancer> getBalancers() {
      List<HealthBalancer> balancers = new ArrayList<>();

      List<String> balancerNames = HealthUtils.getBalancers(jdbcClient);
      if (balancerNames.size() == 0) {
         balancers.add(new HealthBalancer("", false, "No balancers configured in osee_info"));
         return balancers;
      }
      for (String balancerName : balancerNames) {
         try {
            String urlStr = balancerName + "/balancer-manager";
            Boolean reachable = HealthUtils.isUrlReachable(urlStr, auth);
            if (reachable) {
               balancers.add(new HealthBalancer(balancerName, true, HealthUtils.getErrorMsg()));
            } else {
               balancers.add(new HealthBalancer(balancerName, false, HealthUtils.getErrorMsg()));
            }
         } catch (Exception ex) {
            balancers.add(new HealthBalancer(balancerName, false, HealthUtils.getErrorMsg()));
         }
      }
      return balancers;
   }

   public class HealthBalancer {
      private final String name;
      private final Boolean alive;
      private final String errorMsg;

      public HealthBalancer(String name, Boolean alive, String errorMsg) {
         this.name = name;
         this.alive = alive;
         this.errorMsg = errorMsg;
      }

      public String getName() {
         return this.name;
      }

      public Boolean getAlive() {
         return this.alive;
      }

      public String getErrorMsg() {
         return this.errorMsg;
      }
   }
}
