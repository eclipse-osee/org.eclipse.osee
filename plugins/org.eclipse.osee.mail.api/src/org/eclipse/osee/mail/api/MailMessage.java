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

package org.eclipse.osee.mail.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.eclipse.osee.framework.jdk.core.util.GUID;

/**
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class MailMessage {

   private String id;
   private String subject;
   private String from;

   @XmlElement
   private final Collection<String> replyToAddress = new LinkedHashSet<>();

   @XmlElement
   private final Collection<String> recepientAddresses = new LinkedHashSet<>();

   @XmlTransient
   private final List<DataHandler> attachments = new ArrayList<>();

   public void setId(String id) {
      this.id = id;
   }

   public String getId() {
      return id;
   }

   public String getSubject() {
      return subject;
   }

   public void setSubject(String subject) {
      this.subject = subject;
   }

   public String getFrom() {
      return from;
   }

   public void setFrom(String from) {
      this.from = from;
   }

   public Collection<String> getReplyTo() {
      return replyToAddress;
   }

   public void setReplyTo(Collection<String> replyTo) {
      this.replyToAddress.clear();
      this.replyToAddress.addAll(replyTo);
   }

   public Collection<String> getRecipients() {
      return recepientAddresses;
   }

   public void setRecipients(Collection<String> recipientAddress) {
      this.recepientAddresses.clear();
      this.recepientAddresses.addAll(recipientAddress);
   }

   @XmlElement
   public Collection<DataHandler> getAttachments() {
      return attachments;
   }

   public void addAttachment(DataSource... dataSource) {
      for (DataSource source : dataSource) {
         attachments.add(new DataHandler(source));
      }
   }

   public void addAttachment(DataHandler... dataHandler) {
      for (DataHandler handler : dataHandler) {
         attachments.add(handler);
      }
   }

   public static MailMessageBuilder newBuilder() {
      return new MailMessageBuilder();
   }

   public static class MailMessageBuilder {

      private String subject;
      private String from;
      private final Collection<String> replyTos = new LinkedHashSet<>();
      private final Collection<String> recipients = new LinkedHashSet<>();
      private final List<DataHandler> attachments = new ArrayList<>();

      public MailMessage build() {
         MailMessage message = new MailMessage();
         message.setId(GUID.create());
         message.setFrom(from);
         message.setSubject(subject);
         message.setRecipients(recipients);
         message.setReplyTo(replyTos);
         message.getAttachments().addAll(attachments);
         return message;
      }

      public MailMessageBuilder from(String from) {
         this.from = from;
         return this;
      }

      public MailMessageBuilder subject(String subject) {
         this.subject = subject;
         return this;
      }

      public MailMessageBuilder recipient(String... recipient) {
         recipients(Arrays.asList(recipient));
         return this;
      }

      public MailMessageBuilder recipients(Collection<String> values) {
         this.recipients.addAll(values);
         return this;
      }

      public MailMessageBuilder replyTo(String... replyTo) {
         replyTos(Arrays.asList(replyTo));
         return this;
      }

      public MailMessageBuilder replyTos(Collection<String> values) {
         this.replyTos.addAll(values);
         return this;
      }

      public MailMessageBuilder addHtml(String html) {
         String id = GUID.create();
         attach(MailUtils.createFromHtml(id, html));
         return this;
      }

      public MailMessageBuilder addText(String text) {
         String id = GUID.create();
         attach(MailUtils.createFromString(id, text));
         return this;
      }

      public MailMessageBuilder addOutlookEvent(String eventName, String location, Date startDate, Date endDate) {
         attach(MailUtils.createOutlookEvent(eventName, location, startDate, endDate));
         return this;
      }

      public MailMessageBuilder attach(DataSource ds) {
         attach(new DataHandler(ds));
         return this;
      }

      public MailMessageBuilder attach(DataHandler dh) {
         attachments.add(dh);
         return this;
      }
   }
}
