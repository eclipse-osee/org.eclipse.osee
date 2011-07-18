/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.concurrent.Callable;
import javax.mail.MessagingException;
import org.eclipse.osee.framework.ui.skynet.notify.OseeEmail;

public final class SendEmailCall implements Callable<String> {
   private final OseeEmail emailMessage;
   private final String description;

   public SendEmailCall(OseeEmail emailMessage, String description) {
      this.emailMessage = emailMessage;
      this.description = description;
   }

   @Override
   public String call() throws Exception {
      try {
         emailMessage.sendLocalThread();
      } catch (MessagingException ex) {
         return String.format("An exception occured with sending the email [%s].  %s", description, ex);
      }
      return String.format("Sucess for [%s]", description);
   }
}