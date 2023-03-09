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
package org.eclipse.osee.ats.api.workflow;

/**
 * @author Donald G. Dunne
 */
public class WorkItemLastMod {

   private String atsId;
   private String id;
   private Long lastmod;

   public WorkItemLastMod(String atsId, String id, Long lastmod) {
      this.atsId = atsId;
      this.id = id;
      this.lastmod = lastmod;
   }

   public String getAtsId() {
      return atsId;
   }

   public void setAtsId(String atsId) {
      this.atsId = atsId;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public Long getLastmod() {
      return lastmod;
   }

   public void setLastmod(Long lastmod) {
      this.lastmod = lastmod;
   }

}
