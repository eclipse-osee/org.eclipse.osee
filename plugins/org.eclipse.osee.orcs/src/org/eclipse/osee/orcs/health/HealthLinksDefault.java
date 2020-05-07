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
package org.eclipse.osee.orcs.health;

/**
 * @author Donald G. Dunne
 */
public class HealthLinksDefault {

   public static HealthLinks get() {
      HealthLinks links = new HealthLinks();
      links.getLinks().add(HealthLink.valueOf("Eclipse.org", "http://www.eclipse.org"));
      links.getLinks().add(HealthLink.valueOf("Eclipse.org - OSEE", "http://www.eclipse.org/osee"));
      links.getLinks().add(HealthLink.valueOf("OSEE Usage Report", "/server/health/usage"));
      links.getLinks().add(HealthLink.valueOf("OSEE Usage Links", "/server/health/links"));
      links.getLinks().add(HealthLink.valueOf("OSEE Server Headers", "/server/health/headers"));
      links.getLinks().add(HealthLink.valueOf("OSEE Server Status - Overview", "/server/health/server/overview"));
      links.getLinks().add(HealthLink.valueOf("OSEE Server Status - Single", "/server/health/status"));
      links.getLinks().add(HealthLink.valueOf("OSEE Server Status - All", "/server/health/status/all"));
      links.getLinks().add(
         HealthLink.valueOf("OSEE Server Status - All - Details", "/server/health/status/all/details"));
      links.getLinks().add(HealthLink.valueOf("OSEE Server Balancers", "/server/health/server/balancer"));
      links.getLinks().add(HealthLink.valueOf("OSEE Server TOP", "/server/health/top"));
      links.getLinks().add(HealthLink.valueOf("OSEE Server Processes", "/server/health/processes"));
      return links;
   }

}
