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
import org.eclipse.osee.framework.jdk.core.type.Identity;

/**
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class AccountActiveData implements Identity<String> {

   private Long accountId;
   private boolean isActive;
   private String uuid;

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

   @Override
   public String getGuid() {
      return uuid;
   }

   public void setGuid(String uuid) {
      this.uuid = uuid;
   }

   @Override
   public int hashCode() {
      return getGuid().hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      boolean equal = false;
      if (obj instanceof Identity) {
         @SuppressWarnings("unchecked")
         Identity<String> identity = (Identity<String>) obj;
         if (getGuid() == identity.getGuid()) {
            equal = true;
         } else if (getGuid() != null) {
            equal = getGuid().equals(identity.getGuid());
         }
      }
      return equal;
   }

   @Override
   public String toString() {
      return String.valueOf(getGuid());
   }
}