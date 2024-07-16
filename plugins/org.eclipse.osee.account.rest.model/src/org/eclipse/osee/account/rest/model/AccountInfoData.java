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

import java.util.Set;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class AccountInfoData {

   private String name;
   private String userName;
   private String email;
   private Long accountId;
   private boolean isActive;
   private Set<String> roles;

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getUserName() {
      return userName;
   }

   public void setUserName(String userName) {
      this.userName = userName;
   }

   public String getEmail() {
      return email;
   }

   public Set<String> getRoles() {
      return roles;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public Long getAccountId() {
      return accountId;
   }

   public void setAccountId(Long accountId) {
      this.accountId = accountId;
   }

   public boolean isActive() {
      return isActive;
   }

   public void setActive(boolean isActive) {
      this.isActive = isActive;
   }

   public void setRoles(Set<String> roles) {
      this.roles = roles;
   }

   @Override
   public String toString() {
      return String.valueOf(accountId);
   }
}