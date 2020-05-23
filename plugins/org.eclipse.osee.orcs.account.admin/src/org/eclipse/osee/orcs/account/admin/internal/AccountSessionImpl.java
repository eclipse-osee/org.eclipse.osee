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

package org.eclipse.osee.orcs.account.admin.internal;

import java.util.Date;
import org.eclipse.osee.account.admin.AccountSession;

/**
 * @author Roberto E. Escobar
 */
public class AccountSessionImpl implements AccountSession {

   private Long accountId;
   private String sessionToken;
   private Date createdOn;
   private Date lastAccessedOn;
   private String accessedFrom;
   private String accessDetails;

   @Override
   public Long getAccountId() {
      return accountId;
   }

   @Override
   public String getSessionToken() {
      return sessionToken;
   }

   @Override
   public Date getCreatedOn() {
      return createdOn;
   }

   @Override
   public Date getLastAccessedOn() {
      return lastAccessedOn;
   }

   @Override
   public String getAccessedFrom() {
      return accessedFrom;
   }

   @Override
   public String getAccessDetails() {
      return accessDetails;
   }

   public void setAccountId(Long accountId) {
      this.accountId = accountId;
   }

   public void setSessionToken(String sessionToken) {
      this.sessionToken = sessionToken;
   }

   public void setCreatedOn(Date createdOn) {
      this.createdOn = createdOn;
   }

   public void setLastAccessedOn(Date lastUpdated) {
      this.lastAccessedOn = lastUpdated;
   }

   public void setAccessedFrom(String accessedFrom) {
      this.accessedFrom = accessedFrom;
   }

   public void setAccessDetails(String accessDetails) {
      this.accessDetails = accessDetails;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (sessionToken == null ? 0 : sessionToken.hashCode());
      result = prime * result + (int) (accountId ^ accountId >>> 32);
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      AccountSessionImpl other = (AccountSessionImpl) obj;
      if (sessionToken == null) {
         if (other.sessionToken != null) {
            return false;
         }
      } else if (!sessionToken.equals(other.sessionToken)) {
         return false;
      }
      if (!accountId.equals(other.accountId)) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "AccountSessionImpl [accountId=" + accountId + ", sessionToken=" + sessionToken + ", createdOn=" + createdOn + ", lastAccessedOn=" + lastAccessedOn + ", accessedFrom=" + accessedFrom + ", accessDetails=" + accessDetails + "]";
   }

}
