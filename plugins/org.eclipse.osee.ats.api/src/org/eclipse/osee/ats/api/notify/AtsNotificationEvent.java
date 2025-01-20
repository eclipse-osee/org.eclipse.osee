/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.api.notify;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Donald G. Dunne
 */
public class AtsNotificationEvent {

   private Collection<String> emailAddresses = new HashSet<>();
   private Collection<String> abridgedEmailAddresses = new HashSet<>();
   private String id;
   private String fromEmailAddress;
   private String type;
   private String description;
   private String url;
   private String cancelUrl;

   public String getId() {
      return id;
   }

   public String getType() {
      return type;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public void setType(String type) {
      this.type = type;
   }

   public String getUrl() {
      return url;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public void setId(String id) {
      this.id = id;
   }

   @Override
   public String toString() {
      return "AtsNotificationEvent [emailAddresses=" + emailAddresses + ", abridgedEmailAddresses=" + abridgedEmailAddresses + ", id=" + id + ", fromEmailAddress=" + fromEmailAddress + ", type=" + type + ", description=" + description + ", url=" + url + "]";
   }

   public Collection<String> getEmailAddresses() {
      return emailAddresses;
   }

   public void setEmailAddresses(Collection<String> emailAddresses) {
      this.emailAddresses = emailAddresses;
   }

   public String getCancelUrl() {
      return cancelUrl;
   }

   public void setCancelUrl(String cancelUrl) {
      this.cancelUrl = cancelUrl;
   }

   public Collection<String> getAbridgedEmailAddresses() {
      return abridgedEmailAddresses;
   }

   public void setAbridgedEmailAddresses(Collection<String> abridgedEmailAddresses) {
      this.abridgedEmailAddresses = abridgedEmailAddresses;
   }

   public String getFromEmailAddress() {
      return fromEmailAddress;
   }

   public void setFromEmailAddress(String fromEmailAddress) {
      this.fromEmailAddress = fromEmailAddress;
   }

}
