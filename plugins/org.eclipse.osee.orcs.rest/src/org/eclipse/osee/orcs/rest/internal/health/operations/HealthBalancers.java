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

/**
 * @author Jaden W. Puckett
 */
public class HealthBalancers {
   private final JdbcClient jdbcClient;

   public HealthBalancers(JdbcClient jdbcClient) {
      this.jdbcClient = jdbcClient;
   }

   public List<HealthBalancer> getBalancers() {
      List<HealthBalancer> balancers = new ArrayList<>();

      List<String> balancerNames = HealthUtils.getBalancers(jdbcClient);
      if (balancerNames.size() == 0) {
         balancers.add(new HealthBalancer("No application.servers configured in osee.json file", false));
         return balancers;
      }
      for (String balancerName : balancerNames) {
         try {
            String results = HealthUtils.getUrlResults("http://" + balancerName + "/balancer-manager");
            if (results.contains("Load Balancer Manager")) {
               balancers.add(new HealthBalancer(balancerName, true));
            } else {
               balancers.add(new HealthBalancer(balancerName, false));
            }
         } catch (Exception ex) {
            balancers.add(new HealthBalancer(balancerName, false));
         }
      }
      return balancers;
   }

   public class HealthBalancer {
      private final String name;
      private final Boolean alive;

      public HealthBalancer(String name, Boolean alive) {
         this.name = name;
         this.alive = alive;
      }

      public String getName() {
         return this.name;
      }

      public Boolean getAlive() {
         return this.alive;
      }
   }
}
