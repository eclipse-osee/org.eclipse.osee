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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class AccountSessionData {

   private long accountId;
   private String token;

   public String getToken() {
      return token;
   }

   public void setToken(String token) {
      this.token = token;
   }

   public long getAccountId() {
      return accountId;
   }

   public void setAccountId(long accountId) {
      this.accountId = accountId;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (accountId ^ (accountId >>> 32));
      result = prime * result + ((token == null) ? 0 : token.hashCode());
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
