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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class WorkItemLastMod {

   private String atsId;
   private String id;
   private Long lastmod;
   private List<String> siblings;
   private Long opened;
   private Long closed;

   public WorkItemLastMod(String atsId, String id, Long lastmod) {
      this.atsId = atsId;
      this.id = id;
      this.lastmod = lastmod;
      this.siblings = new ArrayList<>();
      this.opened = 0L;
      this.closed = 0L;
   }

   public WorkItemLastMod(String atsId, String id, Long lastmod, List<String> siblings, Long opened, Long closed) {
      this.atsId = atsId;
      this.id = id;
      this.lastmod = lastmod;
      this.siblings = siblings;
      this.setOpened(opened);
      this.setClosed(closed);
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

   public List<String> getSiblings() {
      return siblings;
   }

   public void setSiblings(List<String> siblings) {
      this.siblings = siblings;
   }

   public Long getOpened() {
      return opened;
   }

   public void setOpened(Long opened) {
      this.opened = opened;
   }

   public Long getClosed() {
      return closed;
   }

   public void setClosed(Long closed) {
      this.closed = closed;
   }

}
