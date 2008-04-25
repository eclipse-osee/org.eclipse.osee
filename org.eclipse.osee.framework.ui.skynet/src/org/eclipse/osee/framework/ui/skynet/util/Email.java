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
package org.eclipse.osee.framework.ui.skynet.util;

import java.io.File;
import javax.mail.Message;
import javax.mail.MessagingException;

public class Email {

   public Email() {

   }

   public void sendEmail(String email, String title, String message) throws MessagingException, InterruptedException {
      sendEmailWithAttachment(email, title, message, null);
   }

   public void sendEmailWithAttachment(String email, String title, String message, File file) throws MessagingException, InterruptedException {
      sendEmailWithAttachments(email, title, message, new File[] {file});
   }

   public void sendEmailWithAttachment(String email, String title, String message, String content, String attachmentName) throws MessagingException, InterruptedException {
      AEmail emailMessage = createEmail(email, title, message);
      emailMessage.addAttachment(content, attachmentName);
      emailMessage.send();
   }

   public void sendEmailWithAttachments(String email, String title, String message, File[] files) throws MessagingException, InterruptedException {

      AEmail emailMessage = createEmail(email, title, message);

      if (files != null) {
         for (File file : files)
            emailMessage.addAttachment(file);
      }

      emailMessage.send();
   }

   private AEmail createEmail(String email, String title, String message) throws MessagingException {

      AEmail emailMessage = new AEmail(null, email, email, title);
      emailMessage.setRecipients(Message.RecipientType.TO, email);
      emailMessage.setRecipients(Message.RecipientType.CC, email);
      emailMessage.setRecipients(Message.RecipientType.BCC, email);
      emailMessage.addHTMLBody(message);
      return emailMessage;
   }
}
