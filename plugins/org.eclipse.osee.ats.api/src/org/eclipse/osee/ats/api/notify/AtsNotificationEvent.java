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

   private Collection<String> userIds = new HashSet<>();
   private Collection<String> emailAddresses = new HashSet<>();
   private String id;
   private String fromUserId;
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

   public Collection<String> getUserIds() {
      return userIds;
   }

   public void setUserIds(Collection<String> userIds) {
      this.userIds = userIds;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getFromUserId() {
      return fromUserId;
   }

   public void setFromUserId(String fromUserId) {
      this.fromUserId = fromUserId;
   }

   @Override
   public String toString() {
      return "AtsNotificationEvent [userIds=" + userIds + ", emailAddresses=" + emailAddresses + ", id=" + id + ", fromUserId=" + fromUserId + ", type=" + type + ", description=" + description + ", url=" + url + "]";
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

}
