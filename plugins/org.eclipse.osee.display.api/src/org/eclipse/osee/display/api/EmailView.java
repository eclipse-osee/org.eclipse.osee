/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.api;

import java.util.Collection;

/**
 * @author Roberto E. Escobar
 */
public interface EmailView {

   public static interface SendListener {
      void onSend();
   }

   public static interface Validator {
      boolean accept(String toValidate);
   }

   public enum EmailSendStatus {
      SEND_IN_PROGRESS,
      SUCCESS,
      CANCELLED,
      FAILED,
      NONE;
   }

   void setFrom(String email);

   String getFrom();

   void setReplyTo(Collection<String> emails);

   Collection<String> getReplyTos();

   void setRecipients(Collection<String> emails);

   Collection<String> getRecipients();

   void setSubject(String subject);

   String getSubject();

   void setBody(String body);

   String getBody();

   void setEmailStatus(EmailSendStatus status);

   void addOnSendListener(SendListener listener);

   void addEmailValidator(Validator validator);

   void displayMessage(String caption);

   void displayMessage(String caption, String description);
}
