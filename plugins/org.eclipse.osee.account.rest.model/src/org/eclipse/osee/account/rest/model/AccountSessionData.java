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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class AccountSessionData {

   private Long accountId;
   private String token;

   public String getToken() {
      return token;
   }

   public void setToken(String token) {
      this.token = token;
   }

   public Long getAccountId() {
      return accountId;
   }

   public void setAccountId(Long accountId) {
      this.accountId = accountId;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (accountId ^ accountId >>> 32);
      result = prime * result + (token == null ? 0 : token.hashCode());
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
      AccountSessionData other = (AccountSessionData) obj;
      if (accountId != other.accountId) {
         return false;
      }
      if (token == null) {
         if (other.token != null) {
            return false;
         }
      } else if (!token.equals(other.token)) {
         return false;
      }
      return true;
   }

}
