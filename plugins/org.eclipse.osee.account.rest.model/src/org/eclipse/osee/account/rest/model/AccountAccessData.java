/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.account.rest.model;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class AccountAccessData {

   private long accountId;
   private Date createdOn;
   private Date lastAccessedOn;
   private String accessedFrom;
   private String accessDetails;

   public long getAccountId() {
      return accountId;
   }

   public void setAccountId(long accountId) {
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
