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
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.Identity;

/**
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class SubscriptionGroupData implements Identifiable<String>, Id {

   private String uuid;
   private String name;
   private SubscriptionGroupId groupId;

   @Override
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public String getGuid() {
      return uuid;
   }

   public void setGuid(String uuid) {
      this.uuid = uuid;
   }

   public SubscriptionGroupId getSubscriptionGroupId() {
      return groupId;
   }

   public void setSubscriptionGroupId(SubscriptionGroupId id) {
      this.groupId = id;
   }

   @Override
   public int hashCode() {
      return getGuid().hashCode();
   }

   @Override
   public Long getId() {
      return null;
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
