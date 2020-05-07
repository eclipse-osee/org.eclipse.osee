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
public class HealthLink {
   String name;
   String url;

   public void setName(String name) {
      this.name = name;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public String getName() {
      return name;
   }

   public String getUrl() {
      return url;
   }

   public static HealthLink valueOf(String name, String url) {
      HealthLink link = new HealthLink();
      link.setName(name);
      link.setUrl(url);
      return link;
   }

}
