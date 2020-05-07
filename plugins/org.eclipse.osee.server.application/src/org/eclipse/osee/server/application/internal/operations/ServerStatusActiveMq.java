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

import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.server.OseeInfo;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;

/**
 * @author Donald G. Dunne
 */
public class ServerStatusActiveMq {

   private final JdbcClient jdbcClient;

   public ServerStatusActiveMq(IApplicationServerManager appServerMgr, JdbcClient jdbcClient) {
      this.jdbcClient = jdbcClient;
   }

   public String getHtml() {

      StringBuilder sb = new StringBuilder();
      sb.append("<h3>Active MQ</h3>");
      String activeMqUrl = OseeInfo.getCachedValue(jdbcClient, "osee.activemq.url");
      if (Strings.isValid(activeMqUrl)) {
         try {
            String urlPageHtml = AHTML.getUrlPageHtml(activeMqUrl);
            boolean goodPage = urlPageHtml.contains("Apache ActiveMQ");
            String urlImage = "";
            String result = "";
            if (goodPage) {
               urlImage = ServerUtils.getImage(ServerUtils.GREEN_DOT, activeMqUrl);
               result = "ActiveMQ - Active";
            } else {
               urlImage = ServerUtils.getImage(ServerUtils.RED_DOT, activeMqUrl);
               result = "ActiveMQ - Down";
            }
            return AHTML.simplePageWithImageUrl("Active MQ Event Service", urlImage, result);
         } catch (Exception ex) {
            return AHTML.simplePage("Active MQ Event Service", Lib.exceptionToString(ex));
         }
      } else {
         return AHTML.simplePage("Active MQ Event Service", "osee.activemq.url not in defined in OSEE_INFO");
      }
   }

}
