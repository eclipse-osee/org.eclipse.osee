/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.account.rest.model;

import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class AccountSessionDetailsData {

   private Long accountId;
   private Date createdOn;
   private Date lastAccessedOn;
   private String accessedFrom;
   private String accessDetails;

   public Long getAccountId() {
      return accountId;
   }

   public void setAccountId(Long accountId) {
      this.accountId = accountId;
   }

   public Date getCreatedOn() {
      return createdOn;
   }

   public void setCreatedOn(Date createdOn) {
      this.createdOn = createdOn;
   }

   public Date getLastAccessedOn() {
      return lastAccessedOn;
   }

   public void setLastAccessedOn(Date lastAccessedOn) {
      this.lastAccessedOn = lastAccessedOn;
   }

   public String getAccessedFrom() {
      return accessedFrom;
   }

   public void setAccessedFrom(String accessedFrom) {
      this.accessedFrom = accessedFrom;
   }

   public String getAccessDetails() {
      return accessDetails;
   }

   public void setAccessDetails(String accessDetails) {
      this.accessDetails = accessDetails;
   }

}
