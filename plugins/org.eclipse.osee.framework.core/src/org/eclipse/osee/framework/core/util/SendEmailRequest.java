/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.framework.core.util;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.framework.core.util.OseeEmail.BodyType;

public class SendEmailRequest {

   private Collection<String> toAddresses = Collections.emptyList();
   private Collection<String> ccAddresses = Collections.emptyList();
   private Collection<String> bccAddresses = Collections.emptyList();
   private String fromAddress;
   private String replyToAddress;
   private String subject;
   private String body;
   private BodyType bodyType;
   private Collection<String> emailAddressesAbridged = Collections.emptyList();
   private String subjectAbridged;
   private String bodyAbridged;

   public SendEmailRequest() {
      // for JSON serialization
   }

   public SendEmailRequest(Collection<String> toAddresses, String fromAddress, String replyToAddress, String subject, String body, BodyType bodyType, Collection<String> emailAddressesAbridged, String subjectAbridged, String bodyAbridged) {
      this.toAddresses = toAddresses != null ? toAddresses : Collections.emptyList();
      this.fromAddress = fromAddress;
      this.replyToAddress = replyToAddress;
      this.subject = subject;
      this.body = body;
      this.bodyType = bodyType;
      this.emailAddressesAbridged = emailAddressesAbridged != null ? emailAddressesAbridged : Collections.emptyList();
      this.subjectAbridged = subjectAbridged;
      this.bodyAbridged = bodyAbridged;
   }

   public Collection<String> getToAddresses() {
      return toAddresses;
   }

   public void setToAddresses(Collection<String> toAddresses) {
      this.toAddresses = toAddresses != null ? toAddresses : Collections.emptyList();
   }

   public Collection<String> getCcAddresses() {
      return ccAddresses;
   }

   public void setCcAddresses(Collection<String> ccAddresses) {
      this.ccAddresses = ccAddresses != null ? ccAddresses : Collections.emptyList();
   }

   public Collection<String> getBccAddresses() {
      return bccAddresses;
   }

   public void setBccAddresses(Collection<String> bccAddresses) {
      this.bccAddresses = bccAddresses != null ? bccAddresses : Collections.emptyList();
   }

   public String getFromAddress() {
      return fromAddress;
   }

   public void setFromAddress(String fromAddress) {
      this.fromAddress = fromAddress;
   }

   public String getReplyToAddress() {
      return replyToAddress;
   }

   public void setReplyToAddress(String replyToAddress) {
      this.replyToAddress = replyToAddress;
   }

   public String getSubject() {
      return subject;
   }

   public void setSubject(String subject) {
      this.subject = subject;
   }

   public String getBody() {
      return body;
   }

   public void setBody(String body) {
      this.body = body;
   }

   public BodyType getBodyType() {
      return bodyType;
   }

   public void setBodyType(BodyType bodyType) {
      this.bodyType = bodyType;
   }

   public Collection<String> getEmailAddressesAbridged() {
      return emailAddressesAbridged;
   }

   public void setEmailAddressesAbridged(Collection<String> emailAddressesAbridged) {
      this.emailAddressesAbridged = emailAddressesAbridged != null ? emailAddressesAbridged : Collections.emptyList();
   }

   public String getSubjectAbridged() {
      return subjectAbridged;
   }

   public void setSubjectAbridged(String subjectAbridged) {
      this.subjectAbridged = subjectAbridged;
   }

   public String getBodyAbridged() {
      return bodyAbridged;
   }

   public void setBodyAbridged(String bodyAbridged) {
      this.bodyAbridged = bodyAbridged;
   }
}
