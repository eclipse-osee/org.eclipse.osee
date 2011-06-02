/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.mail;

import java.util.Collection;
import java.util.LinkedHashSet;
import javax.activation.DataSource;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class MailMessage {

   private String id;
   private String subject;
   private String from;

   @XmlElement
   private final Collection<String> replyToAddress = new LinkedHashSet<String>();
   @XmlElement
   private final Collection<String> recepientAddresses = new LinkedHashSet<String>();

   private final Collection<DataSource> attachments = new LinkedHashSet<DataSource>();

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

   public Collection<DataSource> getAttachments() {
      return attachments;
   }

   public void addAttachment(DataSource... dataSource) {
      for (DataSource source : dataSource) {
         attachments.add(source);
      }
   }

}
