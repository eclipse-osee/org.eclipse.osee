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
   private Collection<String> emailAddressesAbridged = new HashSet<>();
   private String id;
   private String fromEmailAddress;
   private String subjectType;
   private String subjectDescription;
   private String subjectDescriptionAbridged;
   private String url;
   private String cancelUrl;

   public String getId() {
      return id;
   }

   public String getSubjectType() {
      return subjectType;
   }

   /**
    * @param subjectType Must be a generic notification type without detailed information
    */
   public void setSubjectType(String subjectType) {
      this.subjectType = subjectType;
   }

   public String getSubjectDescription() {
      return subjectDescription;
   }

   public void setSubjectDescription(String subjectDescription) {
      this.subjectDescription = subjectDescription;
   }

   public String getSubjectDescriptionAbridged() {
      return subjectDescriptionAbridged;
   }

   /**
    * @param subjectDescriptionAbridged Must be generic subject desc with no detailed information. eg: "for Change
    * Request" vs workflow title
    */
   public void setSubjectDescriptionAbridged(String subjectDescriptionAbridged) {
      this.subjectDescriptionAbridged = subjectDescriptionAbridged;
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
      return "AtsNotificationEvent [emailAddresses=" + emailAddresses + ", abridgedEmailAddresses=" + emailAddressesAbridged + ", id=" + id + ", fromEmailAddress=" + fromEmailAddress + ", type=" + subjectType + ", description=" + subjectDescription + ", url=" + url + "]";
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

   public Collection<String> getEmailAddressesAbridged() {
      return emailAddressesAbridged;
   }

   public void setEmailAddressesAbridged(Collection<String> emailAddressesAbridged) {
      this.emailAddressesAbridged = emailAddressesAbridged;
   }

   public String getFromEmailAddress() {
      return fromEmailAddress;
   }

   public void setFromEmailAddress(String fromEmailAddress) {
      this.fromEmailAddress = fromEmailAddress;
   }

}
