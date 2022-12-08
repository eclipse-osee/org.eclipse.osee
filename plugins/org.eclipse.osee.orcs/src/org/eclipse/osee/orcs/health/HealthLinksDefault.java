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
      links.add(HealthLink.valueOf("OSEE Server Status - Overview", "/server/health/overview"));
      links.add(HealthLink.valueOf("OSEE Server Status - Overview Details", "/server/health/overview/details"));
      links.add(HealthLink.valueOf("OSEE Server Status - Overview Details All", "/server/health/overview/details/all"));
      links.add(HealthLink.valueOf("OSEE Server Balancers", "/server/health/balancer"));
      links.add(HealthLink.valueOf("OSEE Server TOP", "/server/health/top"));
      links.add(HealthLink.valueOf("OSEE Server Logs", "/server/health/logs"));
      links.add(HealthLink.valueOf("OSEE Server Types Check", "/server/health/types"));
      links.add(HealthLink.valueOf("OSEE Server Java Processes", "/server/health/processes"));
      links.add(HealthLink.valueOf("OSEE Server Headers", "/server/health/headers"));
      links.add(HealthLink.valueOf("OSEE Active MQ", "/server/health/activemq"));
      links.add(HealthLink.valueOf("OSEE Health Links (populates this list)", "/server/health/links"));
      links.add(HealthLink.valueOf("OSEE Usage Report", "/server/health/usage"));
      return links;
   }

}
