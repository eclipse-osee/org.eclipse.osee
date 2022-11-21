/*********************************************************************
 * Copyright (c) 2022 Boeing
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

import org.eclipse.osee.framework.core.server.OseeInfo;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.health.HealthLink;
import org.eclipse.osee.orcs.health.HealthLinks;
import org.eclipse.osee.orcs.rest.internal.health.ServerHealthEndpointImpl;

/**
 * @author Donald G. Dunne
 */
public class ServerHealthMain {

   private final OrcsApi orcsApi;
   private final JdbcClient jdbcClient;

   public ServerHealthMain(OrcsApi orcsApi, JdbcClient jdbcClient) {
      this.orcsApi = orcsApi;
      this.jdbcClient = jdbcClient;
   }

   public String getHtml() {
      String mainHtml = OseeInf.getResourceContents("web/health/main.html", ServerHealthEndpointImpl.class);
      String dbName = OseeInfo.getCachedValue(jdbcClient, OseeProperties.OSEE_DB);
      if (Strings.isValid(dbName)) {
         mainHtml = mainHtml.replaceAll("OSEE Health", "OSEE " + dbName.toUpperCase() + " Health");
      }
      StringBuffer sb = new StringBuffer();
      ServerHealthLinks healthLinks = new ServerHealthLinks(orcsApi);
      HealthLinks links = healthLinks.getLinks();
      for (HealthLink link : links.getLinks()) {
         sb.append(String.format("<li><a target=\"_blank\" title=\"%s\" href=\"%s\">%s</a></li>\n", link.getName(),
            link.getUrl(), link.getName()));
      }
      mainHtml = mainHtml.replace("PUT_LI_HERE", sb.toString());
      return mainHtml;
   }

}
