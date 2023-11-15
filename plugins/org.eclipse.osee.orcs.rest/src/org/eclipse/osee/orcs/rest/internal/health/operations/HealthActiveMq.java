/*********************************************************************
 * Copyright (c) 2023 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.orcs.rest.internal.health.operations;

import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;

/**
 * @author Jaden W. Puckett
 */
public class HealthActiveMq {
   private final JdbcClient jdbcClient;

   private String activeMqUrl = "";
   private Boolean active = false;
   private String errorMsg = "";

   public HealthActiveMq(JdbcClient jdbcClient) {
      this.jdbcClient = jdbcClient;
   }

   public void setActiveMqInfo() {
      String fetchedActiveMQUrl =
         jdbcClient.fetch("", "Select OSEE_VALUE FROM osee_info where OSEE_KEY = ?", "osee.activemq.url");
      if (Strings.isValid(fetchedActiveMQUrl)) {
         this.activeMqUrl = fetchedActiveMQUrl;
         try {
            String urlPageHtml = AHTML.getUrlPageHtml(fetchedActiveMQUrl);
            this.active = urlPageHtml.contains("Apache ActiveMQ");
         } catch (Exception e) {
            this.errorMsg = e.getMessage();
         }
      } else {
         this.activeMqUrl = "osee.activemq.url not set in osee_info";
      }
   }

   public String getActiveMqUrl() {
      return activeMqUrl;
   }

   public Boolean getActive() {
      return active;
   }

   public String getErrorMsg() {
      return errorMsg;
   }
}
