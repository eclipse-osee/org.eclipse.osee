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

import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.health.HealthLink;
import org.eclipse.osee.orcs.health.HealthLinks;
import org.eclipse.osee.orcs.rest.internal.health.ServerHealthEndpointImpl;

/**
 * @author Donald G. Dunne
 */
public class ServerHealthMain {

   private final OrcsApi orcsApi;

   public ServerHealthMain(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public String getHtml() {
      String mainHtml = OseeInf.getResourceContents("web/health/main.html", ServerHealthEndpointImpl.class);
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
